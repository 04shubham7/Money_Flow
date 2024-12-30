package com.example.SignIn

import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.SignUp.SignUp
import com.example.money_flow.HomeActivity
import com.example.money_flow.R

import com.example.money_flow.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    companion object {
        const val KEY1 = "com.example.database.SigninActivity.mail"
        const val KEY2 = "com.example.database.SigninActivity.name"
        const val KEY3 = "com.example.database.SigninActivity.username"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignInBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        firestore=FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.signin.setOnClickListener {
//            Take ref till node "Users"
            val pass = binding.etpass.text.toString()
            val email=binding.etmail.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                readData(email,pass)
            } else {
                Toast.makeText(this@SignIn, "Please fill all fields", Toast.LENGTH_LONG)
                    .show()
            }

        }
        binding.tv1.setOnClickListener {
            val intent1 = Intent(this,SignUp::class.java)
            startActivity(intent1)
        }
    }

    private fun readData(email: String,pass:String) {
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this) { task->
            if (task.isSuccessful) {
                auth.currentUser?.let { user->
                    firestore.collection("users").document(user.uid).get()
                        .addOnSuccessListener { document->
                            if (document!=null && document.exists()){
                                val name=document.getString("name")
                                val email=document.getString("email")
                                val username=document.getString("username")
                                com.example.money_flow.UserManager.setCurrentUser(user)
                                Toast.makeText(this, "Welcome ${user.email}", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this,HomeActivity::class.java))
                                finish()
                            }else{
                                Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
                            }
                        }
                        .addOnFailureListener { exception->
                            Toast.makeText(this, "Error retrieving user data: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }else{
                Toast.makeText(this, "Authentication Failed:${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}