package com.example.testsample2_api34

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLog: Button
    private lateinit var registerNow: TextView
    private lateinit var firebaseAuth: FirebaseAuth

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
        setContentView(R.layout.activity_login)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        editTextEmail = findViewById(R.id.loginEmail)
        editTextPassword = findViewById(R.id.loginPassword)
        buttonLog = findViewById(R.id.loginButton)
        registerNow = findViewById(R.id.registerNow)
        firebaseAuth = FirebaseAuth.getInstance()

        buttonLog.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                // Show an error message or toast indicating that fields cannot be empty
                error("Please insert an email and a password")
            }
            else{
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                baseContext,
                                "Welcome!",
                                Toast.LENGTH_SHORT,
                            ).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }

        registerNow.setOnClickListener {
            // Logic to handle click on Search People image
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            finish()
        }
    }
}