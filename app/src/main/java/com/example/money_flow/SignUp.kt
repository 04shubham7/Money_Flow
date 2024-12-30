package com.example.SignUp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.SignIn.SignIn
import com.example.money_flow.HomeActivity // Import HomeActivity
import com.example.money_flow.R
import com.example.money_flow.User1
import com.example.money_flow.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase (consider moving to Application class)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnsignup.setOnClickListener {
            val email = binding.etmail.text.toString()
            val name = binding.names.text.toString()
            val username = binding.etuname.text.toString()
            val password = binding.etpass.text.toString()

            val user1 = User1(name, email, username, password)

            // Input validation
            if (email.isNotEmpty() && isValidEmail(email)) {
                if (name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                    registerUser(email, password)
                } else {
                    // Show error message if other fields are empty
                    Toast.makeText(this@SignUp, "Please fill all fields", Toast.LENGTH_LONG).show()
                }
            } else {
                // Show error message if email is invalid
                Toast.makeText(this@SignUp, "Please enter a valid email address", Toast.LENGTH_LONG).show()
            }
        } // Closing curly brace moved here

        binding.textView2.setOnClickListener {
            val intent = Intent(this@SignUp, SignIn::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "name" to binding.names.text.toString(),
                        "email" to user?.email,
                        "username" to binding.etuname.text.toString()
                    )
                    firestore.collection("users").document(user?.uid ?: "").set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User Successfully Registered", Toast.LENGTH_LONG).show()
                            // Navigate to HomeActivity after successful registration
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }.addOnFailureListener { exception ->
                            // Handle data saving error
                            Toast.makeText(this, "Error saving user data: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    // Handle registration errors
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        Toast.makeText(this, "Weak Password", Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid Email", Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "User Already Exists", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    // Function to validate email format
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}