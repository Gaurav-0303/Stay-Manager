package com.gb.staymanager.Report

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gb.staymanager.Adapters.EmployeeListAdapter
import com.gb.staymanager.Adapters.ReportListAdapter
import com.gb.staymanager.Employee.AddDepositActivity
import com.gb.staymanager.Employee.AddSalaryActivity
import com.gb.staymanager.MainActivity
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.Models.DepositSalary
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    private var db = Firebase.firestore
    private val database = Firebase.database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reportList = arrayListOf()
        phoneNumbers = arrayListOf()
        auth = Firebase.auth

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        //receive data from intent
        startDate = (intent.getStringExtra("start") as? String).toString()
        endDate = (intent.getStringExtra("end") as? String).toString()

        //back button
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, ReportFeaturesActivity::class.java))
            finish()
        }

        //set up recycler view
        setUpRecyclerView()

        //retrieve data from firebase
        retrieveData()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun retrieveData() {
        // Show progress dialog
        val progressBar = ProgressDialog(this).apply {
            setMessage("Retrieving Data...")
            setCancelable(false)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
        progressBar.show()

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
                        Log.d("Gaurav", data.toString())
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
                    reportListAdapter.notifyDataSetChanged()
                }
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
    }
}