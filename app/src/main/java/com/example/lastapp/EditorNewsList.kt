package com.example.lastapp

import com.example.lastapp.NewsAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditorNewsList : AppCompatActivity(), NewsAdapter.OnNewsClickListener {
    // RecyclerView and Adapter for displaying news items
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    // List to store news items fetched from Firebase
    private val newsList = mutableListOf<News>()

    // Firebase Database reference
    private lateinit var databaseReference: DatabaseReference

    // Filter buttons for filtering news by status
    private lateinit var filterAll: Button
    private lateinit var filterPending: Button
    private lateinit var filterApproved: Button
    private lateinit var filterRejected: Button
    private lateinit var filterButtons: List<Button>

    // Firebase Authentication instance
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_news_list)

        // Initialize RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with the news list and set it to the RecyclerView
        newsAdapter = NewsAdapter(newsList, this)
        recyclerView.adapter = newsAdapter

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("News")
        fetchNewsData()  // Fetch all news on start

        // Initialize Buttons
        filterAll = findViewById(R.id.filterAll)
        filterPending = findViewById(R.id.filterPending)
        filterApproved = findViewById(R.id.filterApproved)
        filterRejected = findViewById(R.id.filterRejected)

        // List of filter buttons for easy iteration
        filterButtons = listOf(filterAll, filterPending, filterApproved, filterRejected)

        // Set Click Listeners
        filterAll.setOnClickListener { selectFilter(filterAll, "All") }
        filterPending.setOnClickListener { selectFilter(filterPending, "Pending") }
        filterApproved.setOnClickListener { selectFilter(filterApproved, "Published") }
        filterRejected.setOnClickListener { selectFilter(filterRejected, "Rejected") }

        // Set the default filter to "All"
        selectFilter(filterAll, "All")

        // Initialize the search bar and set a text change listener
        val searchBar = findViewById<EditText>(R.id.searchBar)
        searchBar.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                // Filter news based on the search query
                filterNewsBySearch(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Initialize the bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationn)
        bottomNavigationView.setOnItemSelectedListener { item ->
            try {
                when (item.itemId) {
                    R.id.bottom_list -> true
                    R.id.bottom_create2 -> {
                        startActivity(Intent(this, Create2::class.java))
                        finish()
                        true
                    }
                    R.id.bottom_profile2 -> {
                        startActivity(Intent(this, Editor::class.java))
                        finish()
                        true
                    }
                    else -> false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    // Handle news item click
    override fun onNewsClick(newsId: String, title: String, description: String, category: String, location: String, reporterName: String, dateTime: String, image: String) {
        // Navigate to Create2 activity with news data
        val intent = Intent(this, Create2::class.java).apply {
            putExtra("newsId", newsId)
            putExtra("title", title)
            putExtra("description", description)
            putExtra("category", category)
            putExtra("location", location)
            putExtra("reporterName", reporterName)
            putExtra("dateTime", dateTime)
            putExtra("image", image)
        }
        startActivity(intent)
    }

    // Select a filter and update the UI
    private fun selectFilter(selectedButton: Button, status: String) {
        // Mark the selected button as selected and others as unselected
        filterButtons.forEach { button ->
            button.isSelected = (button == selectedButton)
        }
        // Filter the news list based on the selected status
        filterNews(status)
    }

    // Filter the news list by status
    private fun filterNews(status: String) {
        val filteredList = if (status == "All") {
            newsList    // Show all news if "All" is selected
        } else {
            newsList.filter { it.status.equals(status, ignoreCase = true) }
        }
        // Update the adapter with the filtered list
        newsAdapter.updateNews(filteredList)
    }

    // Filter the news list by search query
    private fun filterNewsBySearch(query: String) {
        val filteredList = newsList.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.reporterName.contains(query, ignoreCase = true)
        }
        // Update the adapter with the filtered list
        newsAdapter.updateNews(filteredList)
    }


    // Fetch news data from Firebase
    private fun fetchNewsData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newsList.clear()

                // Iterate through the snapshot and add news items to the list
                for (newsSnapshot in snapshot.children) {
                    val newsItem = newsSnapshot.getValue(News::class.java)
                    if (newsItem != null) {
                        newsList.add(newsItem)
                    }
                }

                // Sort the newsList by dateTime in descending order (newest first)
                val sortedList = newsList.sortedByDescending { it.dateTime }

                // Update the adapter with the sorted list
                newsAdapter.updateNews(sortedList)  // Update RecyclerView
            }

            override fun onCancelled(error: DatabaseError) {
                // Log database errors
                println("Database Error: ${error.message}")
            }
        })
    }
}