package com.gmail_bssushant2003.hotelstay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gmail_bssushant2003.hotelstay.Registration.SignInActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var btnlogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnlogout = findViewById(R.id.signout)

        btnlogout.setOnClickListener{
            Firebase.auth.signOut()
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

    }
}