package com.citu.ukayearn.data.models

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)
