package com.example.heiwa

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.heiwa.database.DatabaseHelper
import com.example.heiwa.database.dao.JournalEntryDao
import com.example.heiwa.database.dao.UserDao
import com.example.heiwa.databinding.ActivityJournalBinding
import com.example.heiwa.model.JournalEntry
import com.example.heiwa.model.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.util.Calendar

class JournalActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityJournalBinding
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var currLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var selectedLatLng: LatLng? = null
    private lateinit var photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private var selectedImageUri: Uri? = null

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var journalEntryDao: JournalEntryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        binding = ActivityJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dbHelper = DatabaseHelper(this)
        journalEntryDao = JournalEntryDao(dbHelper.writableDatabase)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()

        binding.dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                binding.dateEditText.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        photoPickerLauncher = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                try {
                    // SAFELY take persistable URI permission
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Log.e("PhotoPicker", "Failed to persist URI permission: $uri", e)
                }

                selectedImageUri = uri
                binding.selectedImageView.setImageURI(uri)

                Toast.makeText(this, "Selected: $uri", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.attachMediaButton.setOnClickListener {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            handleSubmit()
        }
    }

//    private fun hideSystemBars() {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        controller.hide(WindowInsetsCompat.Type.systemBars())
//    }

    private fun getCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 102)
            return
        }
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location: Location? ->
            if(location != null) {
                currLocation = location
            } else {
                currLocation = Location("").apply {
                    latitude = -6.20201
                    longitude = 106.78113
                }
            }
            mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 102) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val location = LatLng(currLocation.latitude, currLocation.longitude)
        val cameraPosition = CameraPosition.builder().target(location).zoom(16.0F).tilt(0F).build()
//        map.addMarker(MarkerOptions().position(location))
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        map.setOnCameraIdleListener {
//            selectedLatLng = map.cameraPosition.target
            val center = map.cameraPosition.target
            selectedLatLng = center
            val coordinates = "${center.latitude}, ${center.longitude}"
            binding.coordinatesEditText.setText(coordinates)
        }
//        setupConfirmButton()
    }

    private fun handleSubmit() {
        val date = binding.dateEditText.text.toString()
        val name = binding.placeNameEditText.text.toString()
        val notes = binding.notesEditText.text.toString()
        val rating = binding.ratingBar.rating
//        val imageUri = binding.selectedImageView.tag as? Uri


        if (date.isBlank() || name.isBlank() || notes.isBlank() || rating == 0f) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            Log.w("SubmitEntry", "Missing required fields: date=$date, name=$name, notes=$notes, rating=$rating")
            return
        }

        val latLng = selectedLatLng
        if (latLng == null) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show()
            Log.w("SubmitEntry", "No location selected.")
            return
        }

        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        if (userId == -1) {
            Log.e("SubmitEntry", "Invalid userId from SharedPreferences")
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri != null) {
            Log.d("SubmitEntry", "Selected image URI: $selectedImageUri")
        } else {
            Log.d("SubmitEntry", "No image URI selected (mediaUri is null)")
        }

        val entry = JournalEntry(
            journalId = 0,
            placeName = name,
            userId = userId,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            notes = notes,
            rating = rating,
            date = date,
            mediaUri = selectedImageUri?.toString()
        )

        try {
            Log.d("SubmitEntry", "Attempting to insert journal entry: $entry")
            val result = journalEntryDao.insertJournalEntry(entry, userId)
            Log.d("SubmitEntry", "Insert result: $result")

            if (result != -1L) {
                Log.i("SubmitEntry", "Insert successful. Row ID: $result")
                Toast.makeText(this, "Submitted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Log.e("SubmitEntry", "Insert failed (result = -1) for journal entry: $entry")
                Toast.makeText(this, "Failed to submit. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SubmitEntry", "Exception during insert", e)
            Toast.makeText(this, "Database error: ${e.message}", Toast.LENGTH_LONG).show()
        }


    }


//    private fun setupConfirmButton() {
//        binding.btnConfirmLocation.setOnClickListener {
//            selectedLatLng?.let { latLng ->
//                map.addMarker(
//                    MarkerOptions()
//                        .position(latLng)
//                        .title("Selected Location")
//                )
//                Toast.makeText(this, "Marker added at: ${latLng.latitude}, ${latLng.longitude}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

}