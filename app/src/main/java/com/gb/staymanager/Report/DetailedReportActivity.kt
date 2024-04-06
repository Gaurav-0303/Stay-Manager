package com.gb.staymanager.Report

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.databinding.ActivityDetailedReportBinding

class DetailedReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        val customerBill = intent.getSerializableExtra("customerBill") as CustomerBill

        //show customer details
        showCustomerDetails(customerBill)
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