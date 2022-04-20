package com.dandy.momento

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {
    private lateinit var buttonSignIn: Button
    private lateinit var buttonRegister: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        buttonSignIn = findViewById(R.id.buttonSignInReroute)
        buttonRegister = findViewById(R.id.buttonRegister)
        auth = Firebase.auth

        buttonSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        buttonRegister.setOnClickListener {
            createAccount()
        }

    }
    private fun createAccount(){
        val fullName = editPersonName.text.toString()
        val userName = editUserName.text.toString()
        val userEmail = editEmailSignUp.text.toString()
        val userPassword = editPasswordSignUp.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "Full name is required.", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "Username is required.", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(userEmail) -> Toast.makeText(this, "Email is required.", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(userPassword) -> Toast.makeText(this, "Password is required.", Toast.LENGTH_SHORT).show()

            else -> {
                auth = FirebaseAuth.getInstance()

                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            saveUserInfo(fullName, userName, userEmail)
                            // Creation success, update UI with the new user's information
                            Log.d(TAG, "Please wait, verifying credentials...")
                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            // If creation fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Account creation failed.",
                                Toast.LENGTH_SHORT).show()
                            updateUI(null)
                            auth.signOut()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, userEmail: String) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullName"] = fullName.lowercase(Locale.getDefault())
        userMap["userName"] = userName.lowercase(Locale.getDefault())
        userMap["userEmail"] = userEmail
        userMap["bio"] = "Hello there! I am a Momento User"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/momento-d2c59.appspot.com/o/default%20images%2Fuser-profile-icon.png?alt=media&token=e514eeb4-a61e-41d1-b103-d58759a1b8ee"

        userRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "Account has been created successfully!")
                    Toast.makeText(this, "Account has been created successfully!", Toast.LENGTH_SHORT).show()

                    FirebaseDatabase.getInstance().reference
                        .child("Watch").child(currentUserID)
                        .child("Watching").child(currentUserID)
                        .setValue(true)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }
    companion object {
        private const val TAG = "EmailPassword"
    }
}