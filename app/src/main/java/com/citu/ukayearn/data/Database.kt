package com.citu.ukayearn.data

import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.data.models.CartItem
import com.citu.ukayearn.data.models.Category
import com.citu.ukayearn.data.models.Store
import com.citu.ukayearn.data.models.User

object Database {

    val users = mutableListOf(
        User("admin", "Admin", "admin123"),
        User("buyer", "Buyer", "password"),
        User("thriftkada", "Thrift Kada", "seller123"),
        User("cebufinds", "Cebu Finds", "seller123"),
        User("ukayboss", "Ukay Boss", "seller123")
    )

    var currentUsername: String = ""

    // Your curated catalog
    val categories = listOf(
        Category("all", "All Finds", ""),
        Category("tops", "Tops", ""),
        Category("bottoms", "Bottoms", ""),
        Category("outerwear", "Outerwear", ""),
        Category("children", "Children", ""),
        Category("teen", "Teen", ""),
        Category("adult", "Adult", "")
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

    var products = mutableListOf(
        Product(1, "Vintage Carhartt Detroit Jacket", "Faded moss green, perfect streetwear piece. 1 of 1.", 1200.0, 4500.0, "ThriftKada", "Outerwear", "images/items/jacket 1.jpg", "2026-05-19", listOf("Outerwear", "Adult")),
        Product(2, "Y2K Baggy Denim Jeans", "Wide leg, distressed hem. Size 32 waist.", 850.0, 2200.0, "CebuFinds", "Bottoms", "images/items/baggy jeans.jfif", "2026-05-17", listOf("Bottoms", "Teen", "Adult")),
        Product(3, "Retro Nike Windbreaker", "90s colorblock design. Mint condition.", 950.0, 3100.0, "UkayBoss", "Outerwear", "images/items/jacket 2.jfif", "2026-05-20", listOf("Outerwear", "Teen")),
        Product(4, "Graphic Band Tee (Nirvana)", "Washed black, cracked print for the authentic vintage look.", 450.0, 1500.0, "ThriftKada", "Tops", "images/items/t-shirt.jpg", "2026-05-16", listOf("Tops", "Teen", "Adult")),
        Product(5, "Washed Oversized Tee", "Soft cotton oversized tee for everyday vintage styling.", 380.0, 1200.0, "CebuFinds", "Tops", "images/items/shirt-clothes.jpg", "2026-05-18", listOf("Tops", "Adult")),
        Product(6, "Curated Tee Rack Bundle", "Handpicked graphic tee bundle from a fresh thrift drop.", 690.0, 1800.0, "UkayBoss", "Tops", "images/items/t-shirts-shop.jpg", "2026-05-15", listOf("Tops", "Teen")),
        Product(7, "Nike Statement Woven Jacket", "Lightweight woven jacket with a clean sporty thrift finish.", 980.0, 2900.0, "ThriftKada", "Outerwear", "images/items/AS+M+NSW+HBR+JKT+WVN+STMT.avif", "2026-05-14", listOf("Outerwear", "Adult")),
        Product(8, "Cream Zip Track Jacket", "Neutral zip jacket, easy to layer with denim or tees.", 720.0, 1900.0, "CebuFinds", "Outerwear", "images/items/jacket 3.jfif", "2026-05-13", listOf("Outerwear", "Teen", "Adult")),
        Product(9, "Imported Polo Shirt", "Soft collared top with a tidy smart-casual fit.", 520.0, 1400.0, "UkayBoss", "Tops", "images/items/phgoods_62_482868_3x4.avif", "2026-05-12", listOf("Tops", "Adult")),
        Product(10, "Kids Floral Play Dress", "Lightweight thrifted dress with a cheerful print for weekend wear.", 260.0, 780.0, "CebuFinds", "Tops", "images/items/cloth 1.webp", "2026-05-11", listOf("Tops", "Children")),
        Product(11, "Teen Denim Overshirt", "Relaxed denim layer with soft fading and easy streetwear styling.", 640.0, 1600.0, "ThriftKada", "Outerwear", "images/items/cloth 2.jpg", "2026-05-10", listOf("Outerwear", "Teen")),
        Product(12, "Adult Linen Button Top", "Breathable neutral top for smart casual thrift outfits.", 540.0, 1400.0, "UkayBoss", "Tops", "images/items/cloth 3.jpg", "2026-05-09", listOf("Tops", "Adult")),
        Product(13, "Children Soft Joggers", "Comfy everyday joggers with a clean elastic waist.", 300.0, 900.0, "ThriftKada", "Bottoms", "images/items/cloth 4.jfif", "2026-05-08", listOf("Bottoms", "Children")),
        Product(14, "Teen Plaid Flannel", "Cozy plaid layer, perfect over tees and denim.", 470.0, 1300.0, "CebuFinds", "Outerwear", "images/items/cloth 5.webp", "2026-05-07", listOf("Outerwear", "Teen")),
        Product(15, "Adult Straight Cut Slacks", "Office-ready thrift slacks with a relaxed straight fit.", 620.0, 1700.0, "UkayBoss", "Bottoms", "images/items/cloth 6.jfif", "2026-05-06", listOf("Bottoms", "Adult")),
        Product(16, "Children Graphic Tee", "Bright cotton tee with playful print and soft hand feel.", 220.0, 650.0, "CebuFinds", "Tops", "images/items/cloth 7.jfif", "2026-05-05", listOf("Tops", "Children")),
        Product(17, "Teen Cargo Pants", "Utility pockets, relaxed silhouette, and sturdy fabric.", 760.0, 1900.0, "UkayBoss", "Bottoms", "images/items/cloth 8.jfif", "2026-05-04", listOf("Bottoms", "Teen")),
        Product(18, "Adult Knit Cardigan", "Soft neutral cardigan for layering over thrift basics.", 820.0, 2100.0, "ThriftKada", "Outerwear", "images/items/cloth 9.jfif", "2026-05-03", listOf("Outerwear", "Adult")),
        Product(19, "Children Denim Jacket", "Mini denim jacket with gentle fading and sturdy seams.", 580.0, 1500.0, "UkayBoss", "Outerwear", "images/items/cloth 10.jpg", "2026-05-02", listOf("Outerwear", "Children"))
    )

    val newCollectionPreviewImage = "images/items/new collection.webp"

    fun newCollectionProducts(): List<Product> {
        return products.sortedByDescending { it.dateAdded }.take(4)
    }

    val cartItems = products.take(2).map { CartItem(it) }.toMutableList()

    val sellerAccounts = mutableMapOf(
        "ThriftKada" to "thriftkada",
        "CebuFinds" to "cebufinds",
        "UkayBoss" to "ukayboss"
    )

    val haggleOffers = mutableListOf<HaggleOffer>()
    val approvedHaggleVouchers = mutableMapOf<Int, Double>()
    val toReceiveItems = mutableListOf<CartItem>()
    private val receivedPromptsBySeller = mutableMapOf<String, String>()
    private val deletedConversationsByUser = mutableMapOf<String, MutableSet<String>>()
    val chatMessages = mutableListOf(
        ChatMessage(
            id = 1,
            seller = "ThriftKada",
            senderUsername = "thriftkada",
            body = "Hi Admin, this piece is still available. I can include it in today's pickup batch."
        ),
        ChatMessage(
            id = 2,
            seller = "ThriftKada",
            senderUsername = "admin",
            body = "Great, please reserve it while I check out."
        ),
        ChatMessage(
            id = 3,
            seller = "Retro Lane",
            senderUsername = "retrolane",
            body = "Your payment method is ready for checkout."
        ),
        ChatMessage(
            id = 4,
            seller = "CebuFinds",
            senderUsername = "cebufinds",
            body = "I accepted your offer for the denim jacket."
        ),
        ChatMessage(
            id = 5,
            seller = "UkayBoss",
            senderUsername = "ukayboss",
            body = "Ask about the windbreaker before checkout."
        )
    )
    private val unreadConversationsByUser = mutableMapOf(
        "admin" to mutableSetOf("ThriftKada"),
        "buyer" to mutableSetOf("ThriftKada")
    )

    const val buyerProtectionFee = 40.0
    const val deliveryFee = 80.0

    fun effectiveCartUnitPrice(product: Product): Double {
        return approvedHaggleVouchers[product.id] ?: product.price
    }


    fun calculateItemTotal(product: Product, quantity: Int): Double {
        val haggledPrice = approvedHaggleVouchers[product.id]
        return if (haggledPrice != null && quantity > 0) {
            // 1 gets the haggle discount, the remainder gets regular price
            haggledPrice + (product.price * (quantity - 1))
        } else {
            product.price * quantity
        }
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
        return currentUser()?.name?.takeIf { it.isNotBlank() } ?: currentSellerName() ?: displayNameFor(currentUsername)
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

    fun currentUser(): User? {
        return users.firstOrNull { it.username == currentUsername }
    }

    fun currentProfileImageUri(): String? {
        return currentUser()?.profileImageUri
    }

    fun updateCurrentProfile(
        name: String,
        username: String,
        password: String,
        profileImageUri: String?
    ): Boolean {
        val user = currentUser() ?: return false
        val oldUsername = user.username
        if (username != oldUsername && users.any { it.username == username }) return false

        user.name = name
        user.username = username
        user.pass = password
        user.profileImageUri = profileImageUri
        currentUsername = username

        sellerAccounts.entries.firstOrNull { it.value == oldUsername }?.setValue(username)
        unreadConversationsByUser[username] = unreadConversationsByUser.remove(oldUsername) ?: mutableSetOf()
        return true
    }

    fun currentDeliveryDetails(): DeliveryDetails? {
        val user = currentUser() ?: return null
        return DeliveryDetails(
            address = user.deliveryAddress,
            phone = user.phoneNumber,
            landmark = user.landmark
        ).takeIf { it.isComplete() }
    }

    fun saveCurrentDeliveryDetails(address: String, phone: String, landmark: String) {
        currentUser()?.apply {
            deliveryAddress = address
            phoneNumber = phone
            this.landmark = landmark
        }
    }

    fun placeOrder(items: List<CartItem>) {
        items.forEach { orderedItem ->
            // 1. Create a dynamic Order (Status: PENDING_SHIPMENT)
            orders.add(Order(
                id = (orders.maxOfOrNull { it.id } ?: 0) + 1,
                product = orderedItem.product,
                buyerUsername = currentUsername,
                sellerName = orderedItem.product.seller,
                quantity = orderedItem.quantity,
                status = OrderStatus.PENDING_SHIPMENT
            ))

            // 2. Auto-mark as sold out (Issue 4)
            markProductSoldOut(orderedItem.product.id)

            // 3. Completely unlock the item since it's bought
            orderedItem.product.isLocked = false
            orderedItem.product.lockedUntil = null
            orderedItem.product.lockedBy = null

            // 4. Remove from EVERYONE's cart (since it's a 1-of-1 thrift item)
            cartItems.removeAll { it.product.id == orderedItem.product.id }
        }
    }

    fun sendReceivedPromptToSeller(seller: String, prompt: String) {
        receivedPromptsBySeller[seller] = prompt
        addTextChatMessage(seller, currentUsername.ifBlank { "buyer" }, prompt)
        markSellerConversationUnread(seller)
    }

    fun latestReceivedPromptForSeller(seller: String): String? {
        return receivedPromptsBySeller[seller]
    }

    fun currentProfileBadge(): String {
        return if (isCurrentUserSeller()) "Seller account" else "Gold member"
    }

    fun unreadMessageCountForCurrentUser(): Int {
        return unreadConversationsForCurrentUser().size
    }

    fun isConversationUnreadForCurrentUser(seller: String): Boolean {
        return unreadConversationsForCurrentUser().contains(seller)
    }

    fun markConversationReadForCurrentUser(seller: String) {
        unreadConversationsForCurrentUser().remove(seller)
    }

    fun markConversationUnreadForUser(username: String, seller: String) {
        deletedConversationsForUser(username).remove(seller)
        unreadConversationsForUser(username).add(seller)
    }

    fun markSellerConversationUnread(seller: String) {
        sellerAccounts[seller]?.let { sellerUsername ->
            markConversationUnreadForUser(sellerUsername, seller)
        }
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

    fun conversationMessagesForSeller(seller: String): List<ChatMessage> {
        return chatMessages.filter { it.seller == seller }.sortedBy { it.id }
    }

    fun latestChatMessageForSeller(seller: String): ChatMessage? {
        return conversationMessagesForSeller(seller).lastOrNull()
    }

    fun addTextChatMessage(seller: String, senderUsername: String, body: String): ChatMessage {
        return addChatMessage(seller, senderUsername, body, null, ChatMessageType.TEXT)
    }

    fun addImageChatMessage(seller: String, senderUsername: String, imageUri: String): ChatMessage {
        return addChatMessage(seller, senderUsername, null, imageUri, ChatMessageType.IMAGE)
    }

    fun deleteChatMessage(messageId: Int) {
        chatMessages.removeAll { it.id == messageId }
    }

    fun deleteConversationForCurrentUser(seller: String) {
        chatMessages.removeAll { it.seller == seller }
        receivedPromptsBySeller.remove(seller)
        unreadConversationsForCurrentUser().remove(seller)
        deletedConversationsForCurrentUser().add(seller)
    }

    fun isConversationDeletedForCurrentUser(seller: String): Boolean {
        return deletedConversationsForCurrentUser().contains(seller)
    }

    private fun addChatMessage(
        seller: String,
        senderUsername: String,
        body: String?,
        imageUri: String?,
        type: ChatMessageType
    ): ChatMessage {
        deletedConversationsForUser(senderUsername).remove(seller)
        val message = ChatMessage(
            id = (chatMessages.maxOfOrNull { it.id } ?: 0) + 1,
            seller = seller,
            senderUsername = senderUsername,
            body = body,
            imageUri = imageUri,
            type = type
        )
        chatMessages.add(message)
        return message
    }

    private fun unreadConversationsForCurrentUser(): MutableSet<String> {
        val username = currentUsername.ifBlank { "admin" }
        return unreadConversationsForUser(username)
    }

    private fun unreadConversationsForUser(username: String): MutableSet<String> {
        return unreadConversationsByUser.getOrPut(username) { mutableSetOf() }
    }

    private fun deletedConversationsForCurrentUser(): MutableSet<String> {
        val username = currentUsername.ifBlank { "admin" }
        return deletedConversationsForUser(username)
    }

    private fun deletedConversationsForUser(username: String): MutableSet<String> {
        return deletedConversationsByUser.getOrPut(username) { mutableSetOf() }
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

    data class ChatMessage(
        val id: Int,
        val seller: String,
        val senderUsername: String,
        val body: String? = null,
        val imageUri: String? = null,
        val type: ChatMessageType = ChatMessageType.TEXT
    )

    enum class ChatMessageType {
        TEXT,
        IMAGE
    }

    data class DeliveryDetails(
        val address: String,
        val phone: String,
        val landmark: String
    ) {
        fun isComplete(): Boolean {
            return address.isNotBlank() && phone.isNotBlank() && landmark.isNotBlank()
        }
    }

    // Feature: Option to add product
    fun addProduct(name: String, description: String, price: Double, category: String, imageUrl: String, stock: Int) {
        val sellerName = currentSellerName() ?: return
        val newId = (products.maxOfOrNull { it.id } ?: 0) + 1
        val dateAdded = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

        val newProduct = Product(
            id = newId,
            name = name,
            description = description,
            price = price,
            originalPrice = price + (price * 0.2), // Rough estimate for original price
            seller = sellerName,
            category = category,
            imageUrl = imageUrl,
            dateAdded = dateAdded,
            categories = listOf(category, "Adult"),
            stock = stock
        )
        // Add to the top of the list so it appears first
        products.add(0, newProduct)
    }

    // Feature: Option to delete product
    fun deleteProduct(productId: Int) {
        products.removeAll { it.id == productId }
        cartItems.removeAll { it.product.id == productId } // Remove from carts if it was added
    }

    // Feature: Option to mark product as sold out
    fun markProductSoldOut(productId: Int) {
        val index = products.indexOfFirst { it.id == productId }
        if (index != -1) {
            val product = products[index]
            // Sets stock to 0 to trigger the "Out of Stock" logic we built in Phase 1
            products[index] = product.copy(stock = 0)
        }
    }

    enum class OrderStatus { PENDING_SHIPMENT, SHIPPED, COMPLETED }

    data class Order(
        val id: Int,
        val product: Product,
        val buyerUsername: String,
        val sellerName: String,
        val quantity: Int,
        var status: OrderStatus = OrderStatus.PENDING_SHIPMENT
    )

    val orders = mutableListOf<Order>()

    // Auto-unlocks items if the 7-minute checkout timer expires
    fun unlockExpiredProducts() {
        val currentTime = System.currentTimeMillis()
        products.forEach {
            if (it.isLocked && it.lockedUntil != null && currentTime > it.lockedUntil!!) {
                it.isLocked = false
                it.lockedUntil = null
                it.lockedBy = null
            }
        }
    }

    fun markOrderShipped(orderId: Int) {
        val order = orders.find { it.id == orderId }
        if (order != null) {
            order.status = OrderStatus.SHIPPED
        } else {
            // Log an error or handle the invalid orderId case
            println("Error: Order with ID $orderId not found.")
        }
    }

    fun markOrderCompleted(orderId: Int) {
        orders.find { it.id == orderId }?.status = OrderStatus.COMPLETED
    }
}
