package com.example.heiwa

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.heiwa.databinding.ActivityJournalBinding
import com.example.heiwa.databinding.ActivityJournalListBinding
import com.example.heiwa.databinding.ActivityLoginBinding

class JournalListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJournalListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityJournalListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addPlaceFab.setOnClickListener {
            val intent = Intent(this, JournalActivity::class.java)
            startActivity(intent)
        }
    }
}