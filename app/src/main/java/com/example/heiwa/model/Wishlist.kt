package com.example.heiwa.model

class Wishlist (var wishlistId: Int = 0,
                var placeName: String,
                var userId: Int,
                var latitude: Double,
                var longitude: Double,
                var notes: String,
                var mediaUri: String? = null) {
}