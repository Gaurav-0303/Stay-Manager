package com.gb.staymanager

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.WindowManager
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gb.staymanager.AddCustomer.AddCustomerActivity
import com.gb.staymanager.ContactUs.ContactUs_Activity
import com.gb.staymanager.Employee.EmployeeActivity
import com.gb.staymanager.Registration.SignInActivity
import com.gb.staymanager.Report.ReportFeaturesActivity
import com.gb.staymanager.databinding.ActivityMainBinding
import com.gb.staymanager.databinding.CustomAlertBoxBinding
import com.gb.staymanager.databinding.DialogEmployeeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.EmailAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.WHITE
        }

        //add customer
        binding.cardAddCustomer.setOnClickListener { startActivity(Intent(this, AddCustomerActivity::class.java)) }
        binding.addButton.setOnClickListener { startActivity(Intent(this, AddCustomerActivity::class.java)) }

        //employee dialog box
        binding.cardEmployee.setOnClickListener { checkPasswordAndProceed() }

        //report show
        binding.cardReport.setOnClickListener { startActivity(Intent(this, ReportFeaturesActivity::class.java))}

        //3 lines click
        binding.buttonMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        val navigationView = findViewById<NavigationView>(R.id.naview)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.add_customer -> {
                    startActivity(Intent(this,AddCustomerActivity::class.java))
                    true
                }
                R.id.report -> {
                    startActivity(Intent(this,ReportFeaturesActivity::class.java))
                    true
                }
                R.id.logout -> {
                    Firebase.auth.signOut()
                    startActivity(Intent(this,SignInActivity::class.java))
                    finish()
                    true
                }
                R.id.contactus -> {
                    startActivity(Intent(this, ContactUs_Activity::class.java))
                    true
                }
                R.id.employee->{
                    checkPasswordAndProceed()
                    true
                }

                else -> false
            }
        }
    }

    private fun checkPasswordAndProceed() {
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
                        val intent = Intent(this, EmployeeActivity::class.java)
                        startActivity(intent)
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
}