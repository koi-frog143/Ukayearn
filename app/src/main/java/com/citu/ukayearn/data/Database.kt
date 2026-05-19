package com.citu.ukayearn.data

import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.data.models.CartItem
import com.citu.ukayearn.data.models.Category
import com.citu.ukayearn.data.models.Store
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
    val categories = listOf(
        Category("all", "All Finds", ""),
        Category("tops", "Tops", ""),
        Category("bottoms", "Bottoms", ""),
        Category("outerwear", "Outerwear", "")
    )

    val stores = listOf(
        Store(
            id = "thriftkada",
            name = "ThriftKada",
            imageUrl = "images/stores/thriftstore 1.jpg",
            location = "Cebu City, PH",
            tagline = "Curated jackets, tees, and streetwear pieces refreshed weekly.",
            rating = 4.8,
            followers = 1240,
            responseRate = 98,
            joinedDate = "Joined 2024"
        ),
        Store(
            id = "cebufinds",
            name = "CebuFinds",
            imageUrl = "images/stores/thriftstore 2.jpg",
            location = "Mandaue, PH",
            tagline = "Everyday vintage basics, denim finds, and clean neutral pieces.",
            rating = 4.7,
            followers = 920,
            responseRate = 95,
            joinedDate = "Joined 2025"
        ),
        Store(
            id = "ukayboss",
            name = "UkayBoss",
            imageUrl = "images/stores/thrift store 4.png",
            location = "Lapu-Lapu, PH",
            tagline = "Sporty thrift drops, bundle picks, and rare casual staples.",
            rating = 4.9,
            followers = 1510,
            responseRate = 99,
            joinedDate = "Joined 2023"
        )
    )

    val products = listOf(
        Product(1, "Vintage Carhartt Detroit Jacket", "Faded moss green, perfect streetwear piece. 1 of 1.", 1200.0, 4500.0, "ThriftKada", "Outerwear", "images/items/jacket 1.jpg", "2026-05-19"),
        Product(2, "Y2K Baggy Denim Jeans", "Wide leg, distressed hem. Size 32 waist.", 850.0, 2200.0, "CebuFinds", "Bottoms", "images/items/baggy jeans.jfif", "2026-05-17"),
        Product(3, "Retro Nike Windbreaker", "90s colorblock design. Mint condition.", 950.0, 3100.0, "UkayBoss", "Outerwear", "images/items/jacket 2.jfif", "2026-05-20"),
        Product(4, "Graphic Band Tee (Nirvana)", "Washed black, cracked print for the authentic vintage look.", 450.0, 1500.0, "ThriftKada", "Tops", "images/items/t-shirt.jpg", "2026-05-16"),
        Product(5, "Washed Oversized Tee", "Soft cotton oversized tee for everyday vintage styling.", 380.0, 1200.0, "CebuFinds", "Tops", "images/items/shirt-clothes.jpg", "2026-05-18"),
        Product(6, "Curated Tee Rack Bundle", "Handpicked graphic tee bundle from a fresh thrift drop.", 690.0, 1800.0, "UkayBoss", "Tops", "images/items/t-shirts-shop.jpg", "2026-05-15"),
        Product(7, "Nike Statement Woven Jacket", "Lightweight woven jacket with a clean sporty thrift finish.", 980.0, 2900.0, "ThriftKada", "Outerwear", "images/items/AS+M+NSW+HBR+JKT+WVN+STMT.avif", "2026-05-14"),
        Product(8, "Cream Zip Track Jacket", "Neutral zip jacket, easy to layer with denim or tees.", 720.0, 1900.0, "CebuFinds", "Outerwear", "images/items/jacket 3.jfif", "2026-05-13"),
        Product(9, "Imported Polo Shirt", "Soft collared top with a tidy smart-casual fit.", 520.0, 1400.0, "UkayBoss", "Tops", "images/items/phgoods_62_482868_3x4.avif", "2026-05-12")
    )

    val newCollectionPreviewImage = "images/items/new collection.webp"

    fun newCollectionProducts(): List<Product> {
        return products.sortedByDescending { it.dateAdded }.take(4)
    }

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
