package com.example.heiwa

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import com.example.heiwa.database.DatabaseHelper
import com.example.heiwa.database.dao.UserDao
import com.example.heiwa.databinding.ActivityLoginBinding
import org.mindrot.jbcrypt.BCrypt

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        userDao = UserDao(dbHelper.readableDatabase)

        setupUI()
    }

    private fun setupUI() {
        // Set the styled text for the registration prompt
        // Using Html.fromHtml to style part of the string (the "Register" word)
        val registerText = "Don't have an account? <font color='#50a464'><b>Register</b></font>"
        binding.registerTextView.text = Html.fromHtml(registerText, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Set click listener for the Login button
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // Basic validation example
            if (username.isNotEmpty() && password.isNotEmpty()) {
                attemptLogin(username, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for the Register text
        binding.registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun attemptLogin(username: String, password: String) {
        val user = userDao.getByUsername(username)

        if (user == null) {
            Toast.makeText(this, "Wrong pass or username", Toast.LENGTH_SHORT).show()
            return
        }

        if (verifyPassword(password, user.passwordHash)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("username", user.username)
                putInt("userId", user.userId)
                apply()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Wrong pass or username", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }


}