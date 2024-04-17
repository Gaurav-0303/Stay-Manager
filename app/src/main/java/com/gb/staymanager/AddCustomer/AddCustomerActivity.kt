package com.gb.staymanager.AddCustomer

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.gb.staymanager.MainActivity
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityAddCustomerBinding
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import me.kariot.invoicegenerator.data.ModelInvoiceFooter
import me.kariot.invoicegenerator.data.ModelInvoiceHeader
import me.kariot.invoicegenerator.data.ModelInvoiceInfo
import me.kariot.invoicegenerator.data.ModelInvoiceItem
import me.kariot.invoicegenerator.data.ModelInvoicePriceInfo
import me.kariot.invoicegenerator.data.ModelTableHeader
import me.kariot.invoicegenerator.utils.InvoiceGenerator
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val AddCustomerActivity.progressDialog: ProgressDialog
    get() {
        val progressBar = ProgressDialog(this).apply {
            setMessage("Adding Customer...")
            setCancelable(false)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
        return progressBar
    }

class AddCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCustomerBinding
    private var selectedRoom: String = "Select Room"
    private var selectedSource: String = "Select Source"
    private var selectedDate: String? = null
    private var isCash: Boolean = false
    private var isOnline: Boolean = false
    private lateinit var auth : FirebaseAuth
    private var db = Firebase.firestore
    private val database = Firebase.database
    private lateinit var databaseReference: DatabaseReference
    private lateinit var progressBar : ProgressDialog
    private var incrementedCount: Int = 0
    private var customerBill: CustomerBill? = null


    @RequiresApi(Build.VERSION_CODES.O)
    private val requestStoragePermission = requestMultiplePermissions { isGranted ->
        if(isGranted){
            val customerBill: CustomerBill? = retriveCustomerBill()

            if(customerBill != null){
                createPDFFile(customerBill,incrementedCount)
            }else{
                toast("Unable to generate PDF")
            }
        }
        else{
            toast("Permission Denied")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

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

        databaseReference = FirebaseDatabase.getInstance().getReference("cnt")



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
        binding.cardGenerate.setOnClickListener {
            generateBill()
            incrementCount()
//            val customerBill = generateBill()
//            if(customerBill != null){
//                generatePDF(it, customerBill)
//            }else{
//                toast("Unable to Generate PDF")
//            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generatePDF(view: View, customerBill: CustomerBill) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            createPDFFile(customerBill,incrementedCount)
            return
        }
        requestStoragePermission.launch(Constants.storagePermission)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPDFFile(customerBill: CustomerBill,incrementedCount: Int){

        val invoiceNumber = "INV-$incrementedCount"

        val invoiceAddress = ModelInvoiceHeader.ModelAddress(
            "A/P Shendur, Kolhapur",
            getCurrentTime(),
            ""
        )

        val headerData = ModelInvoiceHeader(
            "Hotel Diamond",
            "9697600303",
            "gauravbodake1@gmail.com" , invoiceAddress
        )

        val customerInfo = ModelInvoiceInfo.ModelCustomerInfo(
            "Name: " + customerBill.customerName,
            "Phone: "+customerBill.phone,
            "Aadhar: " + customerBill.aadhaarNo,
            "Source: " + customerBill.source
        )

        val invoiceInfo = ModelInvoiceInfo(
            customerInfo,
            invoiceNumber,
            customerBill.date,
            customerBill.amount
        )

        val tableHeader = ModelTableHeader(
            "Description",
            "    ",
            "    ",
            "    ",
            "Amount"
        )

        val tableData = ModelInvoiceItem(
            customerBill.roomNo,
            "Alloted Room",
            "",
            "",
            "",
            customerBill.amount
        )



        val invoicePriceInfo = ModelInvoicePriceInfo(
            "",
            "",
            customerBill.amount
        )

        val footerData = ModelInvoiceFooter("Thanks For Your Business")

        val pdfGenerator = InvoiceGenerator(this).apply {
            setInvoiceLogo(R.drawable.hotel_diamond)
            setInvoiceColor("#000000")
            setInvoiceHeaderData(headerData)
            setInvoiceInfo(invoiceInfo)
            setInvoiceTableHeaderDataSource(tableHeader)
            setInvoiceTableData(arrayListOf(tableData))
            setPriceInfoData(invoicePriceInfo)
            setInvoiceFooterData(footerData)
        }

        val fileUri = pdfGenerator.generatePDF("${(0..99999).random()}.pdf")
        try{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri,"application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }catch (e: ActivityNotFoundException){
            e.printStackTrace()
            Toast.makeText(this,"There is no PDF Viewer",Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateBill(): CustomerBill? {
        if (isAllFilled()) {

            progressBar = ProgressDialog(this).apply {
                setMessage("Adding Customer...")
                setCancelable(false)
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
            }
            progressBar.show()

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


            var dashedDate : String = ""
            for(i in selectedDate!!){
                dashedDate += if(i == '/') '-'
                else i;
            }

            //store data in firestore
            val docRef = db.collection(auth.currentUser?.email!!).document("customer")
                .collection(binding.editTextPhoneNumber.text.toString()).document()

            docRef.set(customerBill)
                .addOnSuccessListener {
                    Toast.makeText(this, "Customer added successfully on $selectedDate", Toast.LENGTH_SHORT).show()
                    createPDFFile(customerBill,incrementedCount)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }

            //store customer in realtime firebase
            val path = "phone/${auth.currentUser?.uid}/${binding.editTextPhoneNumber.text.toString()}"
            val ref = database.getReference(path)
            ref.setValue(mapOf("phone" to binding.editTextPhoneNumber.text.toString()))
                .addOnSuccessListener {
                    progressBar.dismiss()

                }
                .addOnFailureListener { e ->
                    progressBar.dismiss()
                }
            return customerBill


        } else {
            Toast.makeText(this, "Please fill above details", Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun retriveCustomerBill(): CustomerBill?{
        return generateBill()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String{
        val currentTime = Date()

        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault())

        return formatter.format(currentTime)
    }

    private fun incrementCount(){
        databaseReference.runTransaction(object : Transaction.Handler{
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var currentCount = currentData.getValue(Int::class.java) ?: 0

                currentCount++

                incrementedCount = currentCount

                currentData.value = currentCount

                return Transaction.success(currentData)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if(committed && error == null){
                    customerBill?.let {
                        createPDFFile(it,incrementedCount)
                    }
                }else{

                }
            }
        })
    }

}