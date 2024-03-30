package com.gb.staymanager

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gb.staymanager.AddCustomer.AddCustomerActivity
import com.gb.staymanager.Employee.EmployeeActivity
import com.gb.staymanager.Registration.SignInActivity
import com.gb.staymanager.Report.ReportFeaturesActivity
import com.gb.staymanager.databinding.ActivityMainBinding
import com.gb.staymanager.databinding.DialogEmployeeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        //add customer
        binding.cardAddCustomer.setOnClickListener { startActivity(Intent(this, AddCustomerActivity::class.java)) }
        binding.addButton.setOnClickListener { startActivity(Intent(this, AddCustomerActivity::class.java)) }

        //employee dialog box
        binding.cardEmployee.setOnClickListener { selectEmployeeOption() }

        //report show
        binding.cardReport.setOnClickListener { startActivity(Intent(this, ReportFeaturesActivity::class.java))}

        //3 lines click
        binding.buttonMenu.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun selectEmployeeOption() {
        val dialogBinding = DialogEmployeeBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)

        //employee deposit button
        dialogBinding.cardEmployeeDeposit.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, EmployeeActivity::class.java)
            intent.putExtra("option", "deposit")
            startActivity(intent)
        }

        //employee deposit button
        dialogBinding.cardEmployeeSalary.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, EmployeeActivity::class.java)
            intent.putExtra("option", "salary")
            startActivity(intent)
        }

        dialog.show()
    }
}