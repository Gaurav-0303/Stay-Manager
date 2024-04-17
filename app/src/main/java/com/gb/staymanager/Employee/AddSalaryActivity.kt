package com.gb.staymanager.Employee

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gb.staymanager.Adapters.DepositSalaryListAdapter
import com.gb.staymanager.Models.DepositSalary
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityAddSalaryBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddSalaryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddSalaryBinding
    private lateinit var salaryList : ArrayList<DepositSalary>
    private lateinit var depositSalaryListAdapter: DepositSalaryListAdapter
    private var selectedDate: String? = null
    private var isCash: Boolean = false
    private var isOnline: Boolean = false
    private lateinit var auth : FirebaseAuth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSalaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        salaryList = arrayListOf()
        auth = Firebase.auth
        salaryList = arrayListOf()

        //receive intent data
        val name1 = intent.getStringExtra("name")
        val number1 = intent.getStringExtra("phone")

        // Retrieve deposit data from Firebase
        fetchDepositData(number1!!)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        //back button
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, EmployeeActivity::class.java).putExtra("option", "salary"))
            finish()
        }

        //set up recycler view
        setUpRecyclerView(number1)

        //adding employee by clicking plus button
        binding.addButtonCenter.setOnClickListener { addDialogBox(number1!!) }
        binding.addButtonBottom.setOnClickListener { addDialogBox(number1!!) }
    }

    private fun isCashOrOnline(dialogView: View) {
        dialogView.findViewById<MaterialButton>(R.id.button_cash_2).setOnClickListener {
            isCash = !isCash
            isOnline = false
            changeColor(dialogView)
        }
        dialogView.findViewById<MaterialButton>(R.id.button_online_2).setOnClickListener {
            isOnline = !isOnline
            isCash = false
            changeColor(dialogView)
        }
    }

    private fun fetchDepositData(number: String) {
        // Show progress dialog
        val progressBar = ProgressDialog(this).apply {
            setMessage("Retrieving Salary Data...")
            setCancelable(false)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
        progressBar.show()

        val collectionRef = db.collection(auth.currentUser?.email!!)
            .document("salary")
            .collection(number)

        collectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                progressBar.dismiss()
                for (document in querySnapshot.documents) {
                    val data = document.data
                    val date = data?.get("date").toString()
                    val amount = data?.get("amount").toString()
                    val cash : Boolean = data?.get("cash") as Boolean
                    val online : Boolean = data.get("online") as Boolean
                    val depositSalary = DepositSalary(date, amount, cash, online, document.id)

                    salaryList.add(0, depositSalary)
                }
                salaryList.sortWith(compareByDescending { it.date })
                depositSalaryListAdapter.notifyDataSetChanged()
                changeLayout()
            }
            .addOnFailureListener { exception ->
                progressBar.dismiss()
            }
    }


    private fun changeColor(dialogView: View) {
        if (isCash) dialogView.findViewById<MaterialButton>(R.id.button_cash_2).backgroundTintList = getColorStateList(R.color.blue)
        else dialogView.findViewById<MaterialButton>(R.id.button_cash_2).backgroundTintList = getColorStateList(R.color.grey)

        if (isOnline) dialogView.findViewById<MaterialButton>(R.id.button_online_2).backgroundTintList = getColorStateList(R.color.blue)
        else dialogView.findViewById<MaterialButton>(R.id.button_online_2).backgroundTintList = getColorStateList(R.color.grey)
    }

    private fun setDate(dialogView: View) {
        val currentDate = Calendar.getInstance().time

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        dialogView.findViewById<TextView>(R.id.text_date).text = formattedDate
        if (selectedDate == null) selectedDate = formattedDate
    }

    private fun selectDate(dialogView: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%02d/%02d/%d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )
                dialogView.findViewById<TextView>(R.id.text_date).text = formattedDate
                selectedDate = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun addDialogBox(phone : String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_deposit_or_salary, null)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amount_edit_text)

        isCashOrOnline(dialogView)
        setDate(dialogView)
        dialogView.setOnClickListener { selectDate(dialogView) }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add Salary")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val amount = amountEditText.text.toString() + "₹"

                if (amount.isNotEmpty() && selectedDate != null && (isCash || isOnline)) {
                    dialog.dismiss()

                    val progressBar = ProgressDialog(this).apply {
                        setMessage("Adding Salary...")
                        setCancelable(false)
                        setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    }
                    progressBar.show()

                    var dashedDate : String = ""
                    for(i in selectedDate!!){
                        dashedDate += if(i == '/') '-'
                        else i;
                    }


                    //store data in firestore
                    val docRef = db.collection(auth.currentUser?.email!!)
                        .document("salary")
                        .collection(phone)
                        .document()

                    val depositSalary = DepositSalary(selectedDate!!, amount, isCash, isOnline, docRef.id)

                    docRef.set(depositSalary)
                        .addOnSuccessListener {
                            progressBar.dismiss()
                            Toast.makeText(this, "Salary entry added successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            progressBar.dismiss()
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }

                    salaryList.add(0, DepositSalary(selectedDate!!, amount, isCash, isOnline, docRef.id))
                    salaryList.sortWith(compareByDescending { it.date })
                    depositSalaryListAdapter.notifyDataSetChanged()
                    changeLayout()
                } else {
                    Toast.makeText(this, "Please fill all information", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun changeLayout() {
        if (salaryList.isEmpty()) {
            binding.addButtonBottom.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
            binding.addButtonCenter.visibility = View.VISIBLE
            binding.addEmployee.visibility = View.VISIBLE
        } else {
            binding.addButtonBottom.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.VISIBLE
            binding.addButtonCenter.visibility = View.GONE
            binding.addEmployee.visibility = View.GONE
        }
    }

    private fun setUpRecyclerView(phone: String) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        depositSalaryListAdapter = DepositSalaryListAdapter(this, salaryList)
        binding.recyclerView.adapter = depositSalaryListAdapter

        changeLayout()

        depositSalaryListAdapter.setOnDeleteIconClickListener(object : DepositSalaryListAdapter.OnDeleteIconClickListener {
            override fun onDeleteIconClick(position: Int) {

                val dialog = MaterialAlertDialogBuilder(this@AddSalaryActivity)
                    .setTitle("Are you sure, want to delete this salary?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()

                        // Show progress dialog
                        val progressBar = ProgressDialog(this@AddSalaryActivity).apply {
                            setMessage("Deleting salary...")
                            setCancelable(false)
                            setProgressStyle(ProgressDialog.STYLE_SPINNER)
                        }
                        progressBar.show()

                        val docRef = db.collection(auth.currentUser?.email!!).document("salary")
                            .collection(phone).document(salaryList[position].id)

                        docRef.delete()
                            .addOnSuccessListener {
                                progressBar.dismiss()
                                salaryList.removeAt(position)
                                depositSalaryListAdapter.notifyItemRemoved(position)
                                changeLayout()
                                Toast.makeText(this@AddSalaryActivity, "Salary deleted successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                progressBar.dismiss()
                                Toast.makeText(this@AddSalaryActivity, "Failed to delete salary", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                dialog.show()
            }
        })

    }
}