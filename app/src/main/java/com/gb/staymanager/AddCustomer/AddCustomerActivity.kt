package com.gb.staymanager.AddCustomer

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.gb.staymanager.MainActivity
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityAddCustomerBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCustomerBinding
    private var selectedRoom: String = "Select Room"
    private var selectedSource: String = "Select Source"
    private var selectedDate: String? = null
    private var isCash: Boolean = false
    private var isOnline: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        //back button
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        //set the date default
        dateSet()

        //select date
        binding.textDate.setOnClickListener { selectDate() }

        //drop down room selection
        roomSelection()

        //drop down source selection
        sourceSelection()

        //cash or online
        isCashOrOnline()

        //generate bill
        binding.cardGenerate.setOnClickListener { generateBill() }
    }

    private fun generateBill() {
        if (isAllFilled()) {
            val customerBill = CustomerBill(
                selectedDate!!,
                binding.editTextCustomerName.text.toString(),
                binding.editTextPhoneNumber.text.toString(),
                binding.editTextNoOfPeople.text.toString(),
                binding.editTextAadharNumber.text.toString(),
                binding.editTextAmount.text.toString(),
                isCash,
                isOnline,
                selectedRoom,
                selectedSource
            )
            Toast.makeText(this, "Bill Generated Successfully", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please fill above details", Toast.LENGTH_LONG).show()
        }
    }

    private fun isAllFilled(): Boolean {
        val customerName = binding.editTextCustomerName.text.toString().trim()
        val customerPhone = binding.editTextPhoneNumber.text.toString().trim()
        val noOfPeople = binding.editTextNoOfPeople.text.toString().trim()
        val aadharNumber = binding.editTextAadharNumber.text.toString().trim()
        val amount = binding.editTextAmount.text.toString().trim()

        if (customerName.isEmpty() || customerPhone.isEmpty() || noOfPeople.isEmpty() || aadharNumber.isEmpty() || amount.isEmpty()) return false
        else if (!isCash && !isOnline) return false
        else if (selectedRoom == "Select Room") return false
        else if (selectedSource == "Select Source") return false
        else if (selectedDate == null) return false
        return true
    }

    private fun isCashOrOnline() {
        binding.buttonCash.setOnClickListener {
            isCash = !isCash
            isOnline = false
            changeColor()
        }
        binding.buttonOnline.setOnClickListener {
            isOnline = !isOnline
            isCash = false
            changeColor()
        }
    }

    private fun changeColor() {
        if (isCash) binding.buttonCash.backgroundTintList = getColorStateList(R.color.blue)
        else binding.buttonCash.backgroundTintList = getColorStateList(R.color.grey)

        if (isOnline) binding.buttonOnline.backgroundTintList = getColorStateList(R.color.blue)
        else binding.buttonOnline.backgroundTintList = getColorStateList(R.color.grey)
    }

    private fun roomSelection() {
        val spinnerArray: Array<String> =
            arrayOf("Select Room", "101", "102", "103", "104", "105", "106", "107", "201")
        val spinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        binding.spinnerRoom.adapter = spinnerAdapter

        binding.spinnerRoom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedRoom = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun sourceSelection() {
        val spinnerArray: Array<String> =
            arrayOf("Select Source", "Direct", "Third Party")
        val spinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        binding.spinnerSource.adapter = spinnerAdapter

        binding.spinnerSource.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSource = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun selectDate() {
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
                binding.textDate.text = formattedDate
                selectedDate = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun dateSet() {
        val currentDate = Calendar.getInstance().time

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        binding.textDate.text = formattedDate
        if (selectedDate == null) selectedDate = formattedDate
    }
}