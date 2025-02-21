package com.example.lastapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Start : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)
        val beerIcon: ImageView = findViewById(R.id.beer_icon)
        beerIcon.setOnClickListener {
            // Redirect to LoginActivity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}


