package com.dandy.momento

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : AppCompatActivity() {
    private lateinit var buttonSignUp:Button
    private lateinit var buttonSignIn:Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonSignIn = findViewById(R.id.buttonLogIn)

        buttonSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        buttonSignIn.setOnClickListener {
            loginUser();
        }
    }

    private fun loginUser() {
        val userEmail = editEmailSignIn.text.toString()
        val userPassword = editPasswordSignIn.text.toString()
        auth = Firebase.auth

        when{
            TextUtils.isEmpty(userEmail) -> Toast.makeText(this, "Email is required.", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(userPassword) -> Toast.makeText(this, "Password is required.", Toast.LENGTH_SHORT).show()

            else -> {
                val progressDialog= ProgressDialog(this)
                Toast.makeText(this, "Singing in...", Toast.LENGTH_SHORT).show()

                auth = FirebaseAuth.getInstance()

                auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        progressDialog.dismiss()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}