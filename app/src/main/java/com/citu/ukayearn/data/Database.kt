package com.citu.ukayearn.data

import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.data.models.CartItem
import com.citu.ukayearn.data.models.User

object Database {
    // Mutable so you can demonstrate a live sign-up during your capstone defense!
    val users = mutableListOf(
        User("admin", "admin123"),
        User("buyer", "password"),
        User("thriftkada", "seller123"),
        User("cebufinds", "seller123"),
        User("ukayboss", "seller123")
    )

    var currentUsername: String = ""

    // Your curated catalog
    val products = listOf(
        Product(1, "Vintage Carhartt Detroit Jacket", "Faded moss green, perfect streetwear piece. 1 of 1.", 1200.0, 4500.0, "ThriftKada"),
        Product(2, "Y2K Baggy Denim Jeans", "Wide leg, distressed hem. Size 32 waist.", 850.0, 2200.0, "CebuFinds"),
        Product(3, "Retro Nike Windbreaker", "90s colorblock design. Mint condition.", 950.0, 3100.0, "UkayBoss"),
        Product(4, "Graphic Band Tee (Nirvana)", "Washed black, cracked print for the authentic vintage look.", 450.0, 1500.0, "ThriftKada")
    )

    val cartItems = products.take(2).map { CartItem(it) }.toMutableList()

    val sellerAccounts = mapOf(
        "ThriftKada" to "thriftkada",
        "CebuFinds" to "cebufinds",
        "UkayBoss" to "ukayboss"
    )

    val haggleOffers = mutableListOf<HaggleOffer>()
    val approvedHaggleVouchers = mutableMapOf<Int, Double>()

    const val buyerProtectionFee = 40.0
    const val deliveryFee = 80.0

    fun effectiveCartUnitPrice(product: Product): Double {
        return approvedHaggleVouchers[product.id] ?: product.price
    }

    fun isCurrentUserSellerFor(seller: String): Boolean {
        return sellerAccounts[seller] == currentUsername
    }

    fun isCurrentUserSeller(): Boolean {
        return sellerAccounts.containsValue(currentUsername)
    }

    fun currentSellerName(): String? {
        return sellerAccounts.entries.firstOrNull { it.value == currentUsername }?.key
    }

    fun currentDisplayName(): String {
        return currentSellerName() ?: displayNameFor(currentUsername)
    }

    fun displayNameFor(username: String): String {
        return when (username) {
            "admin" -> "Admin"
            "buyer" -> "Buyer"
            else -> username.replaceFirstChar { it.uppercase() }
        }
    }

    fun currentEmail(): String {
        return if (currentUsername.isBlank()) {
            "admin@ukayearn.com"
        } else {
            "$currentUsername@ukayearn.com"
        }
    }

    fun currentProfileBadge(): String {
        return if (isCurrentUserSeller()) "Seller account" else "Gold member"
    }

    fun haggleOffersForCurrentSeller(): List<HaggleOffer> {
        val seller = currentSellerName() ?: return emptyList()
        return haggleOffers.filter { it.seller == seller }
    }

    fun latestHaggleForSeller(seller: String): HaggleOffer? {
        return haggleOffers.lastOrNull { it.seller == seller }
    }

    fun latestHaggleForProduct(productId: Int): HaggleOffer? {
        return haggleOffers.lastOrNull { it.product.id == productId }
    }

    data class HaggleOffer(
        val id: Int,
        val product: Product,
        val seller: String,
        val offerPrice: Double,
        val buyerUsername: String = "buyer",
        var status: HaggleStatus = HaggleStatus.PENDING
    )

    enum class HaggleStatus {
        PENDING,
        APPROVED,
        DECLINED
    }
}
