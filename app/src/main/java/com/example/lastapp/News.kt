package com.example.lastapp

data class News(
    val id: String = "",
    val title: String = "",
    val dateTime: String = "",
    val location: String? = null,
    val category: String = "",
    val description: String? = null,
    val imageBase64: String? = "",
    val status: String = "",
    val reporterName: String = "",
    val reporterId: String = ""
)
