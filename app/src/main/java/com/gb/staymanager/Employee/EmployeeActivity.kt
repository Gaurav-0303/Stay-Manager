package com.gb.staymanager.Employee

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.gb.staymanager.Adapters.EmployeeListAdapter
import com.gb.staymanager.MainActivity
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityEmployeeBinding
import com.gb.staymanager.databinding.DialogAddEmployeeBinding
import com.gb.staymanager.databinding.DialogEmployeeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EmployeeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEmployeeBinding
    private lateinit var employeeList : ArrayList<Pair<String, String>>
    private lateinit var employeeListAdapter : EmployeeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        employeeList = arrayListOf()

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

        //set up recycler view
        setUpRecyclerView()

        //adding employee by clicking plus button
        binding.addButtonCenter.setOnClickListener { addEmployeeDialogBox() }
        binding.addButtonBottom.setOnClickListener { addEmployeeDialogBox() }
    }

    private fun addEmployeeDialogBox() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_employee, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.name_edit_text)
        val numberEditText = dialogView.findViewById<EditText>(R.id.number_edit_textt)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add Employee")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = nameEditText.text.toString()
                val number = numberEditText.text.toString()

                if (name.isNotEmpty() && number.isNotEmpty()) {
                    employeeList.add(0, Pair(name, number))
                    employeeListAdapter.notifyDataSetChanged()
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

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        employeeListAdapter = EmployeeListAdapter(this, employeeList)
        binding.recyclerView.adapter = employeeListAdapter

        changeLayout()

        employeeListAdapter.setOnClickListener(object : EmployeeListAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val name = employeeList[position].first
                val number = employeeList[position].second
                Log.d("Gaurav", number)
                val option = intent.getStringExtra("option")
                if(option == "deposit"){
                    val intent = Intent(this@EmployeeActivity, AddDepositActivity::class.java)
                    intent.putExtra("name", name)
                    intent.putExtra("phone", number)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this@EmployeeActivity, AddSalaryActivity::class.java)
                    intent.putExtra("name", name)
                    intent.putExtra("phone", number)
                    startActivity(intent)
                }
            }
        })
    }

    private fun changeLayout() {
        if (employeeList.isEmpty()) {
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
}