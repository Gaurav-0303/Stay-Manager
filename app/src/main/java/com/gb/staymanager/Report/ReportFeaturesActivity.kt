package com.gb.staymanager.Report

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.gb.staymanager.MainActivity
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityReportFeaturesBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

class ReportFeaturesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReportFeaturesBinding
    private var isDaySelected : Boolean = false
    private var isMonthSelected : Boolean = false
    private var startDateG : String? = null
    private var endDateG : String? = null
    private lateinit var today : LocalDate
    private lateinit var firstDayOfMonth : LocalDate
    private lateinit var dateFormatter : DateTimeFormatter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //day selection
        daySelection()

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

        //on click next button
        binding.nextButton.setOnClickListener { onPressNextButton() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun daySelection() {
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        today = LocalDate.now()
        firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
    }

    private fun isFilled(): Boolean {
        Log.d("Adarsh", "$isDaySelected $isMonthSelected $startDateG $endDateG")
        if(isDaySelected || isMonthSelected) return true
        if(startDateG != null && endDateG != null) return true
        return false;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onPressNextButton() {
        if(isFilled()){
            val intent = Intent(this, DisplayReportActivity::class.java)
            if(isDaySelected){
                intent.putExtra("start", today.format(dateFormatter))
                intent.putExtra("end", today.format(dateFormatter))
            }
            else if(isMonthSelected){
                intent.putExtra("start", today.format(dateFormatter))
                intent.putExtra("end", firstDayOfMonth.format(dateFormatter))
            }
            else if(startDateG?.isNotEmpty()!! && endDateG?.isNotEmpty()!!){
                intent.putExtra("start", startDateG)
                intent.putExtra("end", endDateG)
            }
            startActivity(intent)
        }
        else{
            Toast.makeText(this, "Please select option from above list", Toast.LENGTH_LONG).show()
        }
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
                if(i){
                    binding.startDateText.text = formattedDate
                    startDateG = formattedDate
                }
                else{
                    binding.endDateText.text = formattedDate
                    endDateG = formattedDate
                }
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