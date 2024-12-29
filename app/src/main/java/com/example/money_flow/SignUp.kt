package com.example.SignUp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.SignIn.SignIn
import com.example.money_flow.R
import com.example.money_flow.User1

import com.example.money_flow.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp: AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnsignup.setOnClickListener {
            val email = binding.etmail.text.toString()
            val name = binding.etname.text.toString()
            val username = binding.etuname.text.toString()
            val password = binding.etpass.text.toString()

            val user1= User1(name,email,username,password)

            database= FirebaseDatabase.getInstance().getReference("Users")
            database.child(username).setValue(user1).addOnSuccessListener {
                // Check if the user is authenticated after registration
                FirebaseAuth.getInstance().currentUser?.let { user ->
                    // User is authenticated, proceed to the next screen
                    val intent = Intent(this@SignUp, SignIn::class.java) // Or HomeActivity as needed
                    startActivity(intent)
                } ?: run {
                    // If there is no authenticated user
                    Toast.makeText(this, "Authentication failed, please try again.", Toast.LENGTH_SHORT).show()
                }
                binding.etmail.text?.clear()
                binding.etname.text?.clear()
                binding.etuname.text?.clear()
                binding.etpass.text?.clear()

                Toast.makeText(this@SignUp,"User Successfully Registered", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this@SignUp,"Failed", Toast.LENGTH_LONG).show()
            }

        }
        binding.textView2.setOnClickListener {
            val intent= Intent(this@SignUp,SignIn::class.java)
            startActivity(intent)
        }
    }
}