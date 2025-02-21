package com.example.lastapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lastapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            firestore.collection("Users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    val role = document.getString("role")
                                    when (role) {
                                        "reporter" -> {
                                            startActivity(Intent(this, MySubmissionActivity::class.java))
                                            finish()
                                        }
                                        "editor" -> {
                                            startActivity(Intent(this, EditorNewsList::class.java))
                                            finish()
                                        }
                                        else -> {
                                            Toast.makeText(this, "Role not found!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are Not Allowed!", Toast.LENGTH_SHORT).show()
            }
        }
        // Add this code to handle the "Not Registered Yet?" click
        binding.textView.setOnClickListener {
            // Navigate to the Sign-Up activity (or show a relevant message)
            startActivity(Intent(this, Signup::class.java))
        }
    }
}
