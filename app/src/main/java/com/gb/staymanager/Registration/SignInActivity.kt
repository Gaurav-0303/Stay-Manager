package com.gb.staymanager.Registration

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gb.staymanager.MainActivity
import com.gb.staymanager.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var signupbtn: Button
    private lateinit var loginbutton: com.google.android.material.card.MaterialCardView
    private lateinit var etEmail:TextInputLayout
    private lateinit var etPassword:TextInputLayout
    private lateinit var auth:FirebaseAuth
    private lateinit var forgetbutton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //transparent background
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }


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

            val progressBar = ProgressDialog(this).apply {
                setMessage("Logging in...")
                setCancelable(false)
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
            }
            progressBar.show()

            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task ->
                    progressBar.dismiss()
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