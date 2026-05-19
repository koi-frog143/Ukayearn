package com.citu.ukayearn.ui.screens.cart

import com.citu.ukayearn.data.models.Product

data class CheckoutItem(
    val product: Product,
    val quantity: Int
)

object CheckoutDraft {
    var items: List<CheckoutItem> = emptyList()
}
