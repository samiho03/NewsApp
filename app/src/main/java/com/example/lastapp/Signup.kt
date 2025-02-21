package com.example.lastapp

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lastapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Signup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString().trim()
            val confirmPass = binding.confirmPassEt.text.toString().trim()
            val selectedRole = findViewById<RadioGroup>(R.id.roleGroup).checkedRadioButtonId
            val role = if (selectedRole == R.id.radioReporter) "reporter" else "editor"

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = firebaseAuth.currentUser?.uid
                                if (userId != null) {
                                    val user = hashMapOf(
                                        "email" to email,
                                        "role" to role
                                    )
                                    firestore.collection("Users").document(userId)
                                        .set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, SignInActivity::class.java))
                                            finish()
                                        }
                                }
                            } else {
                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are Not Allowed!", Toast.LENGTH_SHORT).show()
            }
        }
        // Add this code to handle the "Not Registered Yet?" click
        binding.textView.setOnClickListener {
            // Navigate to the Sign-Up activity (or show a relevant message)
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

}
