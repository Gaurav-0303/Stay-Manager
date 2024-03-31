package com.gb.staymanager.Employee

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gb.staymanager.Adapters.DepositSalaryListAdapter
import com.gb.staymanager.Models.DepositSalary
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityAddDepositBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddDepositBinding
    private lateinit var depositList : ArrayList<DepositSalary>
    private lateinit var depositSalaryListAdapter: DepositSalaryListAdapter
    private var selectedDate: String? = null
    private var isCash: Boolean = false
    private var isOnline: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        depositList = arrayListOf()

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        //back button
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, EmployeeActivity::class.java).putExtra("option", "deposit"))
            finish()
        }

        //receive intent data
        val name = intent.getStringExtra("name")
        val number = intent.getStringExtra("phone")

        //set up recycler view
        setUpRecyclerView()

        //adding employee by clicking plus button
        binding.addButtonCenter.setOnClickListener { addDialogBox() }
        binding.addButtonBottom.setOnClickListener { addDialogBox() }
    }

    private fun addDialogBox() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_deposit_or_salary, null)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amount_edit_text)

        isCashOrOnline(dialogView)
        setDate(dialogView)
        dialogView.setOnClickListener { selectDate(dialogView) }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add Salary")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val amount = amountEditText.text.toString()

                if (amount.isNotEmpty() && selectedDate != null && (isCash || isOnline)) {
                    depositList.add(0, DepositSalary(selectedDate!!, amount, isCash, isOnline))
                    depositList.sortWith(compareByDescending { it.date })
                    depositSalaryListAdapter.notifyDataSetChanged()
                    changeLayout()
                    dialog.dismiss()
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

    private fun changeColor(dialogView: View) {
        if (isCash) dialogView.findViewById<MaterialButton>(R.id.button_cash_2).backgroundTintList = getColorStateList(
            R.color.blue)
        else dialogView.findViewById<MaterialButton>(R.id.button_cash_2).backgroundTintList = getColorStateList(
            R.color.grey)

        if (isOnline) dialogView.findViewById<MaterialButton>(R.id.button_online_2).backgroundTintList = getColorStateList(
            R.color.blue)
        else dialogView.findViewById<MaterialButton>(R.id.button_online_2).backgroundTintList = getColorStateList(
            R.color.grey)
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

    private fun changeLayout() {
        if (depositList.isEmpty()) {
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

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        depositSalaryListAdapter = DepositSalaryListAdapter(this, depositList)
        binding.recyclerView.adapter = depositSalaryListAdapter

        changeLayout()
    }
}