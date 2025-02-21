package com.example.lastapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Create2 : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var categorySpinner: Spinner
    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var progressDialog: ProgressDialog
    private var base64Image: String? = null
    private lateinit var newsId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create2)

        newsId = intent.getStringExtra("newsId") ?: ""

        database = FirebaseDatabase.getInstance().getReference("News")
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Updating...")

        // Initialize views
        val backBtn = findViewById<ImageButton>(R.id.back)
        val title = findViewById<EditText>(R.id.uploadTopic)
        val description = findViewById<EditText>(R.id.uploadDesc)
        val location = findViewById<EditText>(R.id.location)
        val reporterName = findViewById<EditText>(R.id.reporterName)
        val dateTime = findViewById<EditText>(R.id.dateTimePicker)
        val newsImage = findViewById<ImageView>(R.id.uploadImage)
        val rejectButton = findViewById<Button>(R.id.rejectNews)
        val publishButton = findViewById<Button>(R.id.publishNews)
        val imageUploadBtn = findViewById<ImageView>(R.id.uploadImage)

        // Get news data from intent
        val newsId = intent.getStringExtra("newsId")
        val titleText = intent.getStringExtra("title")
        val descriptionText = intent.getStringExtra("description")
        val categoryText = intent.getStringExtra("category")
        val locationText = intent.getStringExtra("location")
        val reporterNameText = intent.getStringExtra("reporterName")
        val dateTimeText = intent.getStringExtra("dateTime")
        val base64ImageText = intent.getStringExtra("image")

        title.setText(titleText)
        description.setText(descriptionText)
        location.setText(locationText)
        reporterName.setText(reporterNameText)
        dateTime.setText(dateTimeText)

        reporterName.setText(intent.getStringExtra("reporterName"))

        imageView = findViewById(R.id.uploadImage)


        // Populate Spinner with Categories
        val categories = arrayOf("Politics", "Sports", "Technology", "Entertainment", "Health","Crime","World","Weather")
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


        // Reject News Button
        rejectButton.setOnClickListener {
            updateNewsStatus("Rejected")
        }

        // Publish News Button
        publishButton.setOnClickListener {
            updateNewsStatus("Published")
        }

        // Back Button functionality
        backBtn.setOnClickListener {
            startActivity(Intent(this, EditorNewsList::class.java))
            finish()
        }
        imageUploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

    }

    private fun updateNewsStatus(status: String) {
        if (newsId.isEmpty()) {
            Toast.makeText(this, "News ID is missing", Toast.LENGTH_SHORT).show()
            return
        }
        progressDialog.show()
        database.child(newsId).child("status").setValue(status)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "News status updated to $status", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update status: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}