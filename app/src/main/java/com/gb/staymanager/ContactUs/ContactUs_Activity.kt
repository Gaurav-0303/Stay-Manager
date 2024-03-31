package com.gb.staymanager.ContactUs

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gb.staymanager.databinding.ActivityContactUsBinding

class ContactUs_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.contactCard1.setOnClickListener {
            sendEmail("gauravbodake1@gmail.com")
        }

        binding.contactCard2.setOnClickListener {
            sendEmail("bssushant2003@gmail.com")
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, "About Stay Manager")
        }

        startActivity(intent)
        
    }

}
