package com.example.heiwa.model

import android.net.Uri

class JournalEntry (var journalId: Int = 0,
                    var placeName: String,
                    var userId: Int,
                    var latitude: Double,
                    var longitude: Double,
                    var notes: String,
                    var rating: Float,
                    var date: String,
                    var mediaUri: String? = null) {
}