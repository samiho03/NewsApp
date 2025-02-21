package com.example.lastapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lastapp.News
import com.example.lastapp.R

class NewsAdapter(
    private var newsList: List<News>,  // List of news items to display
    private val onNewsClickListener: OnNewsClickListener   // Listener for news item clicks
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    // Interface for handling news item clicks
    interface OnNewsClickListener {
        fun onNewsClick(newsId: String, title: String, description: String, category: String, location: String, reporterName: String, dateTime: String, image: String)
    }

    // ViewHolder class to hold references to views in the news item layout
    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val newsImage: ImageView = view.findViewById(R.id.newsImage)
        val newsTitle: TextView = view.findViewById(R.id.newsTitle)
        val newsCategory: TextView = view.findViewById(R.id.newsCategory)
        val newsReporter: TextView = view.findViewById(R.id.newsReporter)
        val newsDate: TextView = view.findViewById(R.id.newsDate)
        val newsStatus: TextView = view.findViewById(R.id.newsStatus)
    }

    // Inflates the news item layout and creates a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item_layout, parent, false)
        return NewsViewHolder(view)
    }

    // Binds data to the views in the ViewHolder
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position] // Get the news item at the current position

        // Set text for title, category, reporter, date, and status
        holder.newsTitle.text = news.title
        holder.newsCategory.text = news.category
        holder.newsReporter.text = "By ${news.reporterName}"
        holder.newsDate.text = news.dateTime
        holder.newsStatus.text = news.status

        // Set the text color for the status based on its value
        when (news.status) {
            "Approved", "Published" -> holder.newsStatus.setTextColor(android.graphics.Color.GREEN)
            "Rejected" -> holder.newsStatus.setTextColor(android.graphics.Color.RED)
            "Pending" -> holder.newsStatus.setTextColor(android.graphics.Color.BLUE)
        }

        // Load the news image if available
        if (!news.imageBase64.isNullOrEmpty()) {
            try {
                // Decode the Base64 image string into a Bitmap
                val imageBytes = android.util.Base64.decode(news.imageBase64, android.util.Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.newsImage.setImageBitmap(bitmap) // Set the decoded image to the ImageVie
            } catch (e: Exception) {
                e.printStackTrace()   // Log any errors during image decoding
            }
        } else {
            // If no image is available, set a placeholder image
            holder.newsImage.setImageResource(R.drawable.placeholder_image)
        }

        // Set a click listener for the news item
        holder.itemView.setOnClickListener {
            // Notify the listener when the news item is clicked
            onNewsClickListener.onNewsClick(
                news.id,
                news.title,
                news.description ?: "",
                news.category,
                news.location ?: "",
                news.reporterName,
                news.dateTime,
                news.imageBase64 ?: ""
            )
        }
    }

    override fun getItemCount(): Int = newsList.size

    // Updates the news list and notifies the adapter of data changes
    fun updateNews(newList: List<News>) {
        this.newsList = newList
        notifyDataSetChanged()  // Refresh the RecyclerView
    }
}