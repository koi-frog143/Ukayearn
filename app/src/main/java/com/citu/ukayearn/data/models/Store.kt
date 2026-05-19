package com.citu.ukayearn.data.models

data class Store(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val tagline: String = "",
    val rating: Double = 0.0,
    val followers: Int = 0,
    val responseRate: Int = 0,
    val joinedDate: String = ""
)
