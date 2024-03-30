package com.gb.staymanager.Registration

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gb.staymanager.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ResetPassword : AppCompatActivity() {

    private lateinit var etEmail: TextInputLayout
    private lateinit var btnResetPassword: MaterialCardView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        etEmail = findViewById(R.id.til_email)
        btnResetPassword = findViewById(R.id.button_reset)

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