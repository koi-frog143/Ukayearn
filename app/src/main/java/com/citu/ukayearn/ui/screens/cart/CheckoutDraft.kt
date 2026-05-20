package com.citu.ukayearn.ui.screens.cart

import com.citu.ukayearn.data.models.Product

data class CheckoutItem(
    val product: Product,
    var quantity: Int,
    var orderId: Int? = null // Added so buyers can mark specific orders as received
)

object CheckoutDraft {
    var items: List<CheckoutItem> = emptyList()
}