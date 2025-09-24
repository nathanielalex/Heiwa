package com.example.heiwa

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.heiwa.database.DatabaseHelper
import com.example.heiwa.database.dao.JournalEntryDao
import com.example.heiwa.database.dao.WishlistDao
import com.example.heiwa.databinding.ActivityJournalDetailBinding
import com.example.heiwa.databinding.ActivityWishlistDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class WishlistDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityWishlistDetailBinding
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var wishlistDao: WishlistDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        binding = ActivityWishlistDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        wishlistDao = WishlistDao(dbHelper.writableDatabase)

        populateData()

        //init map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_container) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.deleteButton.setOnClickListener {
            val wishlistId = intent.getIntExtra("wishlist_id", 0)
            val success = wishlistDao.deleteWishlistEntryById(wishlistId)
            if (success) {
                Log.d("WishlistDao", "Wishlist entry deleted successfully.")
                Toast.makeText(this, "Wishlist entry deleted", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Log.d("WishlistDao", "Wishlist entry not found.")
            }
        }
    }

    private fun populateData() {
        val wishlistId = intent.getIntExtra("wishlist_id", 0)
        Log.d("WishlistDetailActivity", "wishlistId = $wishlistId")
        val result = wishlistDao.getWishlistById(wishlistId)

        // Get data from the intent extras
        if (result != null) {
            lat = result.latitude
            lng = result.longitude

            binding.placeNameTextView.text = result.placeName
            binding.notesTextView.text = result.notes
            binding.coordinatesTextView.text = String.format("%.4f, %.4f", lat, lng)

            Glide.with(this)
                .load(result.mediaUri) // This can be a file path, content URI, or URL
                .placeholder(R.drawable.manga_3d) // Optional: shown while loading
                .into(binding.placeImageView)
        } else {
            Toast.makeText(this, "entry not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        val location = LatLng(lat, lng)

        // Disable all gestures to make the map static
        p0.uiSettings.setAllGesturesEnabled(false)

        // Add a marker and move the camera
        p0.addMarker(MarkerOptions().position(location))
        p0.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

    }
}