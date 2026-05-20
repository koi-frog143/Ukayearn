package com.citu.ukayearn.data.models

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double,
    val seller: String,
    val category: String,
    val imageUrl: String,
    val dateAdded: String,
    val categories: List<String> = listOf(category),
    var isLocked: Boolean = false, // Changed to var for checkout locks
    var lockedUntil: Long? = null, // Added for 7-minute rule
    var lockedBy: String? = null,  // Added to track who locked it
    var stock: Int = 1             // Changed to var for dynamic depletion
)