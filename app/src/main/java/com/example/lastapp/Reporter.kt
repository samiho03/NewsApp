package com.example.lastapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.lastapp.databinding.ActivityEditorBinding
import com.example.lastapp.databinding.ActivityReporterBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class Reporter : AppCompatActivity() {
    // ViewBinding for the activity
    private lateinit var binding: ActivityReporterBinding

    // Firebase instances for authentication, Firestore, and storage
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // URI for the selected profile image
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding
        binding = ActivityReporterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Get the current user's ID
        val userId = firebaseAuth.currentUser?.uid

        // Fetch and display the user's profile data if the user is logged in
        if (userId != null) {
            firestore.collection("Users").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.emailTv.text = document.getString("email")
                    binding.roleTv.text = document.getString("role")
                    binding.usernameEt.setText(document.getString("username") ?: "")

                    // Load profile image using Glide
                    val profileUrl = document.getString("profileImageUrl")
                    if (!profileUrl.isNullOrEmpty()) {
                        Glide.with(this).load(profileUrl).into(binding.profileImage)
                    }
                }
            }
        }

        binding.profileImage.setOnClickListener {
            // Open gallery to pick image
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 100)
        }

        binding.updateBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString().trim()
            if (username.isNotEmpty()) {
                val userUpdate = hashMapOf("username" to username)

                // Update the user profile in Firestore
                userId?.let {
                    firestore.collection("Users").document(it).update(userUpdate as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            // Upload profile image if changed
            imageUri?.let { uri ->
                userId?.let { id ->
                    uploadImage(uri, id)
                }
            }
        }

        binding.changePasswordBtn.setOnClickListener {
            val newPassword = binding.newPasswordEt.text.toString().trim()
            if (newPassword.length >= 6) {
                firebaseAuth.currentUser?.updatePassword(newPassword)?.addOnSuccessListener {
                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                }?.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_profile
        bottomNavigationView.setOnItemSelectedListener { item ->
            try {
                when (item.itemId) {
                    R.id.bottom_subs -> {
                        startActivity(Intent(this, MySubmissionActivity::class.java))
                        finish()
                        true
                    }
                    R.id.bottom_create -> {
                        startActivity(Intent(this, CreateActivity::class.java))
                        finish()
                        true
                    }
                    R.id.bottom_profile -> true

                    else -> false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.profileImage.setImageURI(imageUri)  // Display selected image
        }
    }

    // Upload the selected profile image to Firebase Storage
    private fun uploadImage(uri: Uri, userId: String) {
        // Create a reference to the storage location for the profile image
        val storageRef = storage.reference.child("profile_images/$userId.jpg")

        // Convert the image URI to a Bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val baos = ByteArrayOutputStream()

        // Compress the Bitmap to JPEG format
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Upload the image to Firebase Storage
        storageRef.putBytes(data).addOnSuccessListener {
            // Get the image URL and update Firestore
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                firestore.collection("Users").document(userId).update("profileImageUrl", downloadUri.toString())
            }
        }
    }
}