package com.gb.staymanager.Report

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gb.staymanager.Employee.EmployeeActivity
import com.gb.staymanager.MainActivity
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.databinding.ActivityDetailedReportBinding
import com.gb.staymanager.databinding.CustomAlertBoxBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailedReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedReportBinding
    private lateinit var customerBill: CustomerBill
    private var db = Firebase.firestore
    private val database = Firebase.database
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        customerBill = intent.getSerializableExtra("customerBill") as CustomerBill

        //show customer details
        showCustomerDetails(customerBill)

        //delete entry
        binding.buttonDelete.setOnClickListener { checkPasswordAndProceed() }
    }

    private fun showDeleteDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Are you sure, want to delete the entry?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                deleteEntry()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun deleteEntry() {

        // Show progress dialog
        val progressBar = ProgressDialog(this).apply {
            setMessage("Deleting entry...")
            setCancelable(false)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
        progressBar.show()


        //delete firestore data
        val docRef = db.collection(auth.currentUser?.email!!).document("customer")
            .collection(customerBill.phone).document(customerBill.id)

        docRef.delete()
            .addOnSuccessListener {
                progressBar.dismiss()
                Toast.makeText(this, "Customer deleted successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressBar.dismiss()
                Toast.makeText(this, "Failed to delete customer", Toast.LENGTH_SHORT).show()
            }


        //delete realtime firebase data
//        val path = "phone/${auth.currentUser?.uid}/${customerBill.phone}"
//        val ref = database.getReference(path)
//
//        ref.removeValue()
//            .addOnSuccessListener {
//
//            }
//            .addOnFailureListener { e ->
//
//            }
    }

    private fun checkPasswordAndProceed(){
        val customDialogBinding = CustomAlertBoxBinding.inflate(layoutInflater)
        val passwordEditText = customDialogBinding.password
        val passwordInputLayout = customDialogBinding.tilPassword

        passwordEditText.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(customDialogBinding.root)
            .setPositiveButton("OK") { dialog, _ ->

                // Show progress dialog
                val progressBar = ProgressDialog(this).apply {
                    setMessage("Please wait...")
                    setCancelable(false)
                    setProgressStyle(ProgressDialog.STYLE_SPINNER)
                }
                progressBar.show()

                val password = passwordEditText.text.toString()
                // Check the password against Firebase Authentication credentials
                val currentUser = Firebase.auth.currentUser
                val credential =
                    EmailAuthProvider.getCredential(currentUser?.email ?: "", password)
                currentUser?.reauthenticate(credential)
                    ?.addOnSuccessListener {
                        dialog.dismiss()
                        progressBar.dismiss()
                        showDeleteDialog()
                    }
                    ?.addOnFailureListener {
                        progressBar.dismiss()
                        // Password is incorrect, show a message
                        // You can customize the message as per your requirement
                        AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Incorrect password. Please try again.")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        // Create the AlertDialog object
        val alertDialog = alertDialogBuilder.create()

        // Show the AlertDialog
        alertDialog.show()
    }

    private fun showCustomerDetails(customerBill: CustomerBill) {

        var paymentType: String = ""
        paymentType = if (customerBill.isCash) "Cash"
        else "Online"

        val previewText = """
                Date : ${customerBill.date}                
                
                Customer Name : ${customerBill.customerName}           
                Customer Phone : ${customerBill.phone}                
                Customer Aadhaar : ${customerBill.aadhaarNo}                
                No. of People : ${customerBill.noOfPeople}             
                
                Amount : ${customerBill.amount}₹                
                Payment Type : ${paymentType}                
                Room no : ${customerBill.roomNo}                
                Source : ${customerBill.source}
      
            """.trimIndent()

        binding.textViewMessage.text = previewText
    }
}