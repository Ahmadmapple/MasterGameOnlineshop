package com.example.mastergameonlineshop

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mastergameonlineshop.databinding.ActivityRegisterPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class registerPage : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        binding.registerButton.setOnClickListener {
            val username = binding.editTextTextUsername.text.toString()
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            val confirm_password = binding.editTextTextConfirmPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (password == confirm_password) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val userId = it.result.user?.uid
                            val user = hashMapOf(
                                "username" to username
                            )
                            if (userId != null) {
                                firebaseFirestore.collection("users").document(userId).set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, loginPage::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            else {
                                Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            Toast.makeText(this, it.exception?.message ?: "Registration failed", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Confirm Password not match", Toast.LENGTH_SHORT).show()

                }
            }
            else {
                Toast.makeText(this, "Please Fill in the Blank", Toast.LENGTH_SHORT).show()
            }
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}