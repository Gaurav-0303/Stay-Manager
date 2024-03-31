package com.gb.staymanager.Report

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.gb.staymanager.MainActivity
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityReportFeaturesBinding
import java.util.Calendar

class ReportFeaturesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReportFeaturesBinding
    private var isDaySelected : Boolean = false
    private var isMonthSelected : Boolean = false
    private var startDateG : String? = null
    private var endDateG : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportFeaturesBinding.inflate(layoutInflater)
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

        //select features and custom date
        selectFeatures()
    }

    private fun selectFeatures() {

        binding.cardToday.setOnClickListener {
            isDaySelected = !isDaySelected
            isMonthSelected = false
            if(isDaySelected){
                binding.startDateText.text = "Start Date"
                binding.endDateText.text = "End Date"
                startDateG = null
                endDateG = null
            }
            changeCardColor()
        }

        binding.cardMonth.setOnClickListener {
            isMonthSelected = !isMonthSelected
            isDaySelected = false
            if(isMonthSelected){
                binding.startDateText.text = "Start Date"
                binding.endDateText.text = "End Date"
                startDateG = null
                endDateG = null
            }
            changeCardColor()
        }

        binding.startDate.setOnClickListener { selectDate(true) }
        binding.endDate.setOnClickListener { selectDate(false) }
    }

    @SuppressLint("ResourceAsColor")
    private fun changeCardColor() {
        if(isDaySelected){
            binding.cardToday.backgroundTintList = getColorStateList(R.color.blue)
        }
        else{
            binding.cardToday.backgroundTintList = getColorStateList(R.color.white)
        }

        if(isMonthSelected){
            binding.cardMonth.backgroundTintList = getColorStateList(R.color.blue)
        }
        else{
            binding.cardMonth.backgroundTintList = getColorStateList(R.color.white)
        }
    }

    private fun selectDate(i: Boolean) {
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
                if(i) binding.startDateText.text = formattedDate
                else binding.endDateText.text = formattedDate
//                binding.cardToday.backgroundTintList = getColorStateList(R.color.white)
//                binding.cardMonth.backgroundTintList = getColorStateList(R.color.white)
                isMonthSelected = false
                isDaySelected = false
                changeCardColor()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}