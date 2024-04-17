package com.gb.staymanager.Report

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.gb.staymanager.Adapters.ReportListAdapter
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityDisplayReportBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DisplayReportActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDisplayReportBinding
    private lateinit var reportList : ArrayList<CustomerBill>
    private lateinit var phoneNumbers : ArrayList<String>
    private lateinit var startDate : String
    private lateinit var endDate : String
    private lateinit var reportListAdapter : ReportListAdapter
    private lateinit var auth : FirebaseAuth
    private lateinit var dateFormat : DateTimeFormatter
    private var db = Firebase.firestore
    private val database = Firebase.database

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reportList = arrayListOf()
        phoneNumbers = arrayListOf()
        auth = Firebase.auth
        dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

       //receive data from intent
//        startDate = LocalDate.parse((intent.getStringExtra("start")))
//        endDate = LocalDate.parse((intent.getStringExtra("end")))

        startDate = intent.getStringExtra("start") as String
        endDate = intent.getStringExtra("end") as String

        Log.d("Gaurav", startDate)
        Log.d("Gaurav", endDate)

        //back button
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, ReportFeaturesActivity::class.java))
            finish()
        }

        //set up recycler view
        setUpRecyclerView()

        //retrieve data from firebase
        retrieveData(startDate, endDate)


    }

    private fun showTotal() {
        val sum = reportListAdapter.getSum()
        val cash = reportListAdapter.getCash()
        val online = reportListAdapter.getOnline()
        binding.totalText.text = "Total : $sum₹"
        binding.cashText.text = "Cash : $cash₹"
        binding.onlineText.text = "Online : $online₹"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    private fun retrieveData(startDate: String, endDate: String) {
        // Show progress dialog
        val progressBar = ProgressDialog(this).apply {
            setMessage("Retrieving Data...")
            setCancelable(false)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
        progressBar.show()


        var startDateG: LocalDate = LocalDate.parse(startDate, dateFormat)
        var endDateG: LocalDate = LocalDate.parse(endDate, dateFormat)


        val allowedFormats = listOf(DateTimeFormatter.ofPattern("dd/MM/yyyy"), DateTimeFormatter.ofPattern("MM-dd-yyyy"), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        for (format in allowedFormats) {
            try {
                startDateG = LocalDate.parse(startDate, format)
                endDateG = LocalDate.parse(endDate, format)
                dateFormat = format
                break
            } catch (e: DateTimeParseException) {

            }
        }

        val customerDocRef = db.collection(auth.currentUser?.email!!)
            .document("customer")

        GlobalScope.launch(Dispatchers.Main) {
            progressBar.show()
            try {
                val phoneNumbers = retrievePhoneNumbers()
                for (collectionName in phoneNumbers) {
                    val collectionRef = customerDocRef.collection(collectionName)
                    val documents = collectionRef.get().await()
                    for (document in documents) {
                        val data = document.data
                        val dateToCheck: LocalDate = LocalDate.parse(data["date"] as String, dateFormat)
                        val isBetween = dateToCheck.isEqual(startDateG) || dateToCheck.isEqual(endDateG) ||
                                (dateToCheck.isAfter(startDateG) && dateToCheck.isBefore(endDateG))

                        if(isBetween){
                            reportList.add(CustomerBill(
                                data["date"] as String,
                                data["customerName"] as String,
                                data["phone"] as String,
                                data["noOfPeople"] as String,
                                data["aadhaarNo"] as String,
                                data["amount"] as String,
                                data["cash"] as Boolean,
                                data["online"] as Boolean,
                                data["roomNo"] as String,
                                data["source"] as String
                            ))
                        }
                    }
                }
                reportList.sortByDescending { LocalDate.parse(it.date, dateFormat) }
                reportListAdapter.notifyDataSetChanged()
                delay(30)
                showTotal()
            } catch (_: Exception) {

            } finally {
                progressBar.dismiss()
            }
        }
    }

    private suspend fun retrievePhoneNumbers(): ArrayList<String> = suspendCoroutine { cont ->
        val ref = database.getReference("phone/${auth.currentUser?.uid}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val phoneNumbers = ArrayList<String>()
                for (childSnapshot in snapshot.children) {
                    val phone = childSnapshot.child("phone").value.toString()
                    phoneNumbers.add(phone)
                }
                cont.resume(phoneNumbers)
            }

            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(error.toException())
            }
        })
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        reportListAdapter = ReportListAdapter(this, reportList)
        binding.recyclerView.adapter = reportListAdapter

        reportListAdapter.setOnClickListener(object : ReportListAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val customerBill = CustomerBill(
                    reportList[position].date,
                    reportList[position].customerName,
                    reportList[position].phone,
                    reportList[position].noOfPeople,
                    reportList[position].aadhaarNo,
                    reportList[position].amount,
                    reportList[position].isCash,
                    reportList[position].isOnline,
                    reportList[position].roomNo,
                    reportList[position].source
                )
                val intent = Intent(this@DisplayReportActivity, DetailedReportActivity::class.java)
                intent.putExtra("customerBill", customerBill)
                startActivity(intent)
            }
        })
    }
}