package com.citu.ukayearn.data.models

data class User(
    var username: String,
    var name: String = "",
    var pass: String,
    var profileImageUri: String? = null,
    var deliveryAddress: String = "",
    var phoneNumber: String = "",
    var landmark: String = ""
)
