package com.example.lastapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CreateActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var categorySpinner: Spinner
    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var progressDialog: ProgressDialog
    private var base64Image: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create)

        database = FirebaseDatabase.getInstance().getReference("News")
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading...")

        val backBtn = findViewById<ImageButton>(R.id.back)
        val postBtn = findViewById<Button>(R.id.postNews)
        val imageUploadBtn = findViewById<ImageView>(R.id.uploadImage)
        val title = findViewById<EditText>(R.id.uploadTopic)
        val description = findViewById<EditText>(R.id.uploadDesc)
        val location = findViewById<EditText>(R.id.location)
        val reporterName = findViewById<EditText>(R.id.reporterName)
        val dateTime = findViewById<EditText>(R.id.dateTimePicker)
        val deleteBtn = findViewById<ImageButton>(R.id.deleteButton)
        val editBtn = findViewById<Button>(R.id.editNews)

        // Retrieve data passed from previous activity (news item to edit)
        val newsId = intent.getStringExtra("newsId")
        val titleText = intent.getStringExtra("title")
        val descriptionText = intent.getStringExtra("description")
        val categoryText = intent.getStringExtra("category")
        val locationText = intent.getStringExtra("location")
        val reporterNameText = intent.getStringExtra("reporterName")
        val dateTimeText = intent.getStringExtra("dateTime")
        val base64ImageText = intent.getStringExtra("image")

        // Pre-fill the fields if data is passed
        title.setText(titleText)
        description.setText(descriptionText)
        location.setText(locationText)
        reporterName.setText(reporterNameText)
        dateTime.setText(dateTimeText)

        imageView = findViewById(R.id.uploadImage)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            firestore.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username")
                        reporterName.setText(username ?: "")
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to load username: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Auto-fill Date & Time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        dateTime.setText(sdf.format(Date()))


        // Populate Spinner with Categories
        val categories = arrayOf("Politics", "Sports", "Technology", "Entertainment", "Health","Crime", "World","Weather")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner = findViewById(R.id.categorySpinner)
        categorySpinner.adapter = adapter
        if (categoryText != null) {
            val categoryIndex = categories.indexOf(categoryText)
            categorySpinner.setSelection(categoryIndex)
        }

        // Handle image if passed as base64 string
        if (base64ImageText != null) {
            base64Image = base64ImageText
            val decodedBytes = Base64.decode(base64ImageText, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imageView = findViewById(R.id.uploadImage)
            imageView.setImageBitmap(bitmap)
        }

        // Post News
        postBtn.setOnClickListener {
            saveToFirebase(status = "Pending")
        }

        // Edit functionality
        editBtn.setOnClickListener {
            saveToFirebase(newsId = newsId, status = "Pending")
        }

        // Delete functionality
        deleteBtn.setOnClickListener {
            if (newsId != null) {
                deleteNews(newsId)
            }
        }

        // Back Button functionality
        backBtn.setOnClickListener {
            startActivity(Intent(this, MySubmissionActivity::class.java))
            finish()
        }

        // Image Upload Functionality
        imageUploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

    }

    private fun saveToFirebase(newsId: String? = null, status: String = "Pending") {
        progressDialog.show()

        val title = findViewById<EditText>(R.id.uploadTopic).text.toString()
        val description = findViewById<EditText>(R.id.uploadDesc).text.toString()
        val location = findViewById<EditText>(R.id.location).text.toString()
        val reporterName = findViewById<EditText>(R.id.reporterName).text.toString()
        val dateTime = findViewById<EditText>(R.id.dateTimePicker).text.toString()
        val category = categorySpinner.selectedItem.toString()

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || reporterName.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            return
        }

        val finalNewsId = newsId ?: database.push().key ?: UUID.randomUUID().toString()
        val reporterId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val news = News(
            finalNewsId,
            title,
            dateTime,
            location,
            category,
            description,
            base64Image,
            status,
            reporterName,
            reporterId
        )

        database.child(finalNewsId).setValue(news)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "News $status Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Failed to save news: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


    }

    private fun deleteNews(newsId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete News")
            .setMessage("Are you sure you want to delete this news?")
            .setPositiveButton("Yes") { _, _ ->
                database.child(newsId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "News deleted successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to delete news: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)

            // Convert image to Base64 string
            selectedImageUri?.let { uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                base64Image = encodeImageToBase64(bitmap)
            }
        }
    }


    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // Reduce quality to save space
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}

