package com.gb.staymanager.Employee

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gb.staymanager.Adapters.EmployeeListAdapter
import com.gb.staymanager.MainActivity
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityEmployeeBinding
import com.gb.staymanager.databinding.DialogEmployeeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EmployeeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEmployeeBinding
    private lateinit var employeeList : ArrayList<Pair<String, String>>
    private lateinit var employeeListAdapter : EmployeeListAdapter
    private val database = Firebase.database
    private lateinit var auth : FirebaseAuth
    private lateinit var context : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        employeeList = arrayListOf()
        context = this

        // Retrieve employee data from Firebase
        fetchEmployeeData()

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

    private fun fetchEmployeeData() {

        // Show progress dialog
        val progressBar = ProgressDialog(this).apply {
            setMessage("Retrieving Employee Data...")
            setCancelable(false)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
        progressBar.show()

        val ref = database.getReference("${auth.currentUser?.uid}")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.dismiss()

                for (childSnapshot in snapshot.children) {
                    val name = childSnapshot.child("name").value.toString()
                    val phone = childSnapshot.child("phone").value.toString()
                    employeeList.add(Pair(name, phone))
                }

                employeeListAdapter.notifyDataSetChanged()
                changeLayout()
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.dismiss()
                // Handle error if data retrieval is cancelled
                Toast.makeText(this@EmployeeActivity, "Failed to retrieve employee data", Toast.LENGTH_SHORT).show()
            }
        })
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
                    dialog.dismiss()

                    val progressBar = ProgressDialog(this).apply {
                        setMessage("Adding Employee...")
                        setCancelable(false)
                        setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    }
                    progressBar.show()

                    //store data in realtime firebase
                    val path = "${auth.currentUser?.uid}/$number"
                    val ref = database.getReference(path)
                    ref.setValue(mapOf("name" to name, "phone" to number))
                        .addOnSuccessListener {
                            progressBar.dismiss()
                            Toast.makeText(this, "Employee Added Successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            progressBar.dismiss()
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }

                    employeeList.add(0, Pair(name, number))
                    employeeListAdapter.notifyDataSetChanged()
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

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        employeeListAdapter = EmployeeListAdapter(this, employeeList)
        binding.recyclerView.adapter = employeeListAdapter

        changeLayout()

        employeeListAdapter.setOnItemClickListener(object : EmployeeListAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val name = employeeList[position].first
                val number = employeeList[position].second
                selectEmployeeOption(name, number)
            }
        })

        employeeListAdapter.setOnDeleteIconClickListener(object : EmployeeListAdapter.OnDeleteIconClickListener {
            override fun onDeleteIconClick(position: Int) {
                val employee = employeeList[position]
                val number = employee.second

                val dialog = MaterialAlertDialogBuilder(context)
                    .setTitle("Are you sure, want to delete this employee?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()

                        // Show progress dialog
                        val progressBar = ProgressDialog(this@EmployeeActivity).apply {
                            setMessage("Deleting employee...")
                            setCancelable(false)
                            setProgressStyle(ProgressDialog.STYLE_SPINNER)
                        }
                        progressBar.show()

                        // Remove employee from realtime Firebase
                        val path = "${auth.currentUser?.uid}/$number"
                        val ref = database.getReference(path)
                        ref.removeValue()
                            .addOnSuccessListener {
                                progressBar.dismiss()
                                // Remove employee from the list
                                employeeList.removeAt(position)
                                employeeListAdapter.notifyItemRemoved(position)
                                changeLayout()
                                Toast.makeText(this@EmployeeActivity, "Employee deleted successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                progressBar.dismiss()
                                Toast.makeText(this@EmployeeActivity, "Failed to delete employee", Toast.LENGTH_SHORT).show()
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

    private fun selectEmployeeOption(name: String, number: String) {
        val dialogBinding = DialogEmployeeBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)

        //employee deposit button
        dialogBinding.cardEmployeeDeposit.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, AddDepositActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("phone", number)
            startActivity(intent)
        }

        //employee deposit button
        dialogBinding.cardEmployeeSalary.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, AddSalaryActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("phone", number)
            startActivity(intent)
        }

        dialog.show()
    }
}