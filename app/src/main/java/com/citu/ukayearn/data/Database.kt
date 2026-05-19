package com.citu.ukayearn.data

import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.data.models.User

object Database {
    // Mutable so you can demonstrate a live sign-up during your capstone defense!
    val users = mutableListOf(
        User("admin", "admin123"),
        User("buyer", "password")
    )

    // Your curated catalog
    val products = listOf(
        Product(1, "Vintage Carhartt Detroit Jacket", "Faded moss green, perfect streetwear piece. 1 of 1.", 1200.0, 4500.0, "ThriftKada"),
        Product(2, "Y2K Baggy Denim Jeans", "Wide leg, distressed hem. Size 32 waist.", 850.0, 2200.0, "CebuFinds"),
        Product(3, "Retro Nike Windbreaker", "90s colorblock design. Mint condition.", 950.0, 3100.0, "UkayBoss"),
        Product(4, "Graphic Band Tee (Nirvana)", "Washed black, cracked print for the authentic vintage look.", 450.0, 1500.0, "ThriftKada")
    )
}