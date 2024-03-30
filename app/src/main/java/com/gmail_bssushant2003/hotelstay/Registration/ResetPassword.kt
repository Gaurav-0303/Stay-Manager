package com.gmail_bssushant2003.hotelstay.Registration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gmail_bssushant2003.hotelstay.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class ResetPassword : AppCompatActivity() {

    private lateinit var etEmail: TextInputLayout
    private lateinit var btnResetPassword: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etEmail = findViewById(R.id.til_email)
        btnResetPassword = findViewById(R.id.btn_reset_password)

        auth = FirebaseAuth.getInstance()

        btnResetPassword.setOnClickListener{
            val email = etEmail.editText?.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this,"Please enter your email",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this,"Please Check Your Email",Toast.LENGTH_SHORT).show()
                    navigatetoSignInActivity()
                }
                .addOnFailureListener{
                    Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun navigatetoSignInActivity(){
        val intent = Intent(this,SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}