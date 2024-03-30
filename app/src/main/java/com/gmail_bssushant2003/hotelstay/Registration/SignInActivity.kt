package com.gmail_bssushant2003.hotelstay.Registration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gmail_bssushant2003.hotelstay.MainActivity
import com.gmail_bssushant2003.hotelstay.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var signupbtn: Button
    private lateinit var loginbutton: Button
    private lateinit var etEmail:TextInputLayout
    private lateinit var etPassword:TextInputLayout
    private lateinit var auth:FirebaseAuth
    private lateinit var forgetbutton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth

        signupbtn = findViewById(R.id.signup_screen)
        loginbutton = findViewById(R.id.login_btn)
        etEmail = findViewById(R.id.username)
        etPassword = findViewById(R.id.password)
        forgetbutton = findViewById(R.id.forgetbtn)

        forgetbutton.setOnClickListener{
            val intent = Intent(this,ResetPassword::class.java)
            startActivity(intent)
        }

        loginbutton.setOnClickListener{
            val email = etEmail.editText?.text.toString()
            val password = etPassword.editText?.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this@SignInActivity,"Please enter Email and Password",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        val verification = auth.currentUser?.isEmailVerified
                        if(verification == true){
                            Toast.makeText(
                                this@SignInActivity, "Authentication successful.",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,"Please Verify your Email",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(
                            this@SignInActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        signupbtn.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null && currentUser.isEmailVerified){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}