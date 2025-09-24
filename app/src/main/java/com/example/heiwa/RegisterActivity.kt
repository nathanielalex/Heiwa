package com.example.heiwa

import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.heiwa.database.DatabaseHelper
import com.example.heiwa.database.dao.UserDao
import com.example.heiwa.databinding.ActivityRegisterBinding
import com.example.heiwa.model.User
import org.mindrot.jbcrypt.BCrypt

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        userDao = UserDao(dbHelper.writableDatabase)

        setupUI()
    }

    private fun setupUI() {
        // Set styled text for the login prompt
        val loginText = "Already have an account? <font color='#50a464'><b>Login</b></font>"
        binding.loginTextView.text = Html.fromHtml(loginText, Html.FROM_HTML_MODE_LEGACY)

        // Set click listener for the back button
        binding.backButton.setOnClickListener {
            // Finishes the current activity and returns to the previous one (LoginActivity)
            finish()
        }

        // Set click listener for the Login text view
        binding.loginTextView.setOnClickListener {
            finish() // Also returns to the LoginActivity
        }

        // Set click listener for the Register button
        binding.registerButton.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun validateAndRegister() {
        val username = binding.usernameEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

        if (username.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val currentTimeMillis = System.currentTimeMillis().toString()
        val result = userDao.insertUser(User(0, username, hashedPassword, phone, currentTimeMillis))

        if (result != -1L) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}