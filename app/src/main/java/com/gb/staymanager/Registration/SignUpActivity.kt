package com.gb.staymanager.Registration

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gb.staymanager.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var tvEmail: TextInputLayout
    private lateinit var tvPassword: TextInputLayout
    private lateinit var tvName:TextInputLayout
    private lateinit var tvPhoneNo: TextInputLayout
    private lateinit var tvcompanyname: TextInputLayout
    private lateinit var registerbtn: com.google.android.material.card.MaterialCardView
    private lateinit var alreadyuser:Button

    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }


        auth = Firebase.auth

        tvEmail = findViewById(R.id.email)
        tvPassword = findViewById(R.id.password)
        registerbtn = findViewById(R.id.btnRegister)
        tvName = findViewById(R.id.name)
        tvPhoneNo = findViewById(R.id.phoneno)
        alreadyuser = findViewById(R.id.userpresent)
        tvcompanyname = findViewById(R.id.company_name)

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null && user.isEmailVerified) {
                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        registerbtn.setOnClickListener{

            val email = tvEmail.editText?.text.toString()
            val password = tvPassword.editText?.text.toString()
            val name = tvName.editText?.text.toString()
            val phoneNo = tvPhoneNo.editText?.text.toString()
            val companyname = tvcompanyname.editText?.text.toString()

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    baseContext, "Invalid email format",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                Toast.makeText(
                    baseContext, "Please enter your name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (phoneNo.isEmpty()) {
                Toast.makeText(
                    baseContext, "Please enter your phone number",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if(companyname.isEmpty()){
                Toast.makeText(baseContext,"Please enter Company name",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        val user = auth.currentUser
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(this,"A Verification Email has been sent to the entered email",Toast.LENGTH_SHORT).show()
                                updateUI()
                            }
                            ?.addOnFailureListener{
                                Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        Toast.makeText(
                            baseContext, "Authentication Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        //updateUI(null)
                    }
                }


        }

        alreadyuser.setOnClickListener{
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun updateUI() {
        val intent = Intent(this,SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}