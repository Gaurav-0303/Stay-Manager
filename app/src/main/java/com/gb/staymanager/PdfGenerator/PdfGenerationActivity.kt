package com.gb.staymanager.PdfGenerator

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.R
import com.gb.staymanager.databinding.ActivityPdfGenerationBinding

class PdfGenerationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPdfGenerationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfGenerationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        val customerBill = intent.getSerializableExtra("customerBill") as CustomerBill
    }
}