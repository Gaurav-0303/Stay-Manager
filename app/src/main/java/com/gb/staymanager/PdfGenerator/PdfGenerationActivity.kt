package com.gb.staymanager.PdfGenerator

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityPdfGenerationBinding
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import me.kariot.invoicegenerator.data.ModelInvoiceFooter
import me.kariot.invoicegenerator.data.ModelInvoiceHeader
import me.kariot.invoicegenerator.data.ModelInvoiceInfo
import me.kariot.invoicegenerator.data.ModelInvoiceItem
import me.kariot.invoicegenerator.data.ModelInvoicePriceInfo
import me.kariot.invoicegenerator.data.ModelTableHeader
import me.kariot.invoicegenerator.utils.InvoiceGenerator
class PdfGenerationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPdfGenerationBinding
    private lateinit var customerBill: CustomerBill

    private val requestStoragePermission = requestMultiplePermissions { isGranted ->
        if(isGranted){
            createPDFFile()
        }
        else{
            toast("Permission Denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfGenerationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        customerBill = intent.getSerializableExtra("customerBill") as CustomerBill

        val fileUri = createPDFFile()
        displayPDF(fileUri)

    }

    fun generatePDF(view: View) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            createPDFFile()
            return
        }
        requestStoragePermission.launch(Constants.storagePermission)
    }

    private fun createPDFFile() : Uri {
        val invoiceAddress = ModelInvoiceHeader.ModelAddress(
            "Line 1",
            "Line 2",
            "Line 3"
        )

        val headerData = ModelInvoiceHeader(
            "Company Name",
            "Company Phone",
            "Company Email"
        )

        val customerInfo = ModelInvoiceInfo.ModelCustomerInfo(
            "Name: " + customerBill.customerName,
            "Phone: "+customerBill.phone,
            "Aadhar: " + customerBill.aadhaarNo,
            "Number of People: " + customerBill.noOfPeople
        )

        val invoiceInfo = ModelInvoiceInfo(
            customerInfo,
            "",
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
            setInvoiceLogo(R.drawable.logo)
            setInvoiceColor("#000000")
            setInvoiceHeaderData(headerData)
            setInvoiceInfo(invoiceInfo)
            setInvoiceTableHeaderDataSource(tableHeader)
            setInvoiceTableData(arrayListOf(tableData))
            setPriceInfoData(invoicePriceInfo)
            setInvoiceFooterData(footerData)
        }

        val fileUri = pdfGenerator.generatePDF("${(0..99999).random()}.pdf")

        return fileUri

    }

    private fun toast(s: String){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    }

    private fun displayPDF(fileUri: Uri){
        val pdfView = findViewById<com.github.barteksc.pdfviewer.PDFView>(R.id.pdfView)
        pdfView.fromUri(fileUri)
            .onLoad(onLoadCompleteListener)
            .scrollHandle(DefaultScrollHandle(this))
            .spacing(10)
            .load()
    }

    fun downloadPDF(view: View){
        val fileUri = createPDFFile()

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri,"application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }catch (e: ActivityNotFoundException){
            e.printStackTrace()
            toast("There is no PDF Viewer")
        }
    }

    private val onLoadCompleteListener = object : OnLoadCompleteListener{
        override fun loadComplete(nbPages: Int){
            toast("PDF Loaded Successfully")
        }
    }
}