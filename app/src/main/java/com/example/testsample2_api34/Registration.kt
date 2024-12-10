package com.example.testsample2_api34

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registration : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var editTextUserName: TextInputEditText
    private lateinit var editTextConfirmPassword: TextInputEditText
    private lateinit var buttonReg: Button
    private lateinit var loginNow: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        editTextEmail = findViewById(R.id.registrationEmail)
        editTextPassword = findViewById(R.id.registrationPassword)
        editTextConfirmPassword = findViewById(R.id.registrationConfirmPassword)
        editTextUserName = findViewById(R.id.registrationUserName)
        buttonReg = findViewById(R.id.registrationButton)
        loginNow = findViewById(R.id.loginNow)

        // Register button click listener
        buttonReg.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()
            val userName = editTextUserName.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || confirmPassword.isEmpty()) {
                // Show an error message or toast indicating that fields cannot be empty
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                // Show an error message or toast indicating that passwords do not match
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            }
            else {
                // Perform user registration
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful
                            val currentUser = firebaseAuth.currentUser
                            currentUser?.let { user ->
                                // Update Firestore with user details
                                val userId = user.uid
                                val userMap = hashMapOf(
                                    "email" to email,
                                    "userName" to userName,
                                    "profilePicture" to "https://firebasestorage.googleapis.com/v0/b/trektrace-database.appspot.com/o/User%20Profile%20Pictures%2FDefaultProfilePicture.png?alt=media&token=33b0075f-8c12-43ee-a427-030eb2a1fb19",
                                    "collectedSouvenirs" to arrayListOf(0),
                                    "travelerTypeCityAttraction" to 0,
                                    "travelerTypeCultural" to 0,
                                    "travelerTypeHistorical" to 0,
                                    "travelerTypeNatural" to 0,
                                    "travelerTypeOther" to 0,
                                    "travelerTypePopCulture" to 0,
                                    "surveyTaken" to false
                                )
                                // Add user details to Firestore
                                firestore.collection("Users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        // Firestore update successful
                                        Toast.makeText(
                                            this,
                                            "Registration successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        openLogin()
                                    }
                                    .addOnFailureListener { e ->
                                        // Firestore update failed
                                        Log.e(
                                            "Registration",
                                            "Firestore update failed: ${e.message}",
                                            e
                                        )
                                        Toast.makeText(
                                            this,
                                            "Firestore update failed: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            // Registration failed
                            Log.e("Registration", "Registration failed", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Registration failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        loginNow.setOnClickListener {
            openLogin()
        }
    }
    private fun openLogin(){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}

