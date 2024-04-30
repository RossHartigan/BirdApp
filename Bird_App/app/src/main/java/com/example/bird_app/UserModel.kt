package com.example.bird_app

data class UserModel(
    var username: String = "",
    var password: String = "",
    var lat: Double = 0.0,
    var long: Double = 0.0,
    var birdName: String = "",
    val birdNumber: Int = 0,
)



