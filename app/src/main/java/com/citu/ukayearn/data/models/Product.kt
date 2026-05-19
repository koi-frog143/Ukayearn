package com.citu.ukayearn.data.models

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double, // Added this specifically for your Feature 7 (Thrift History/Relief)
    val seller: String,
    val isLocked: Boolean = false // For Feature 6: The 15-minute cart lock
)