package com.example.heiwa

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.example.heiwa.databinding.ActivityMainBinding
import com.example.heiwa.fragment.HomeFragment
import com.example.heiwa.fragment.JournalFragment
import com.example.heiwa.fragment.MapJournalFragment
import com.example.heiwa.fragment.WishlistFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener  { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> selectedFragment = HomeFragment()
                R.id.nav_journal -> selectedFragment = JournalFragment()
                R.id.nav_wishlist -> selectedFragment = WishlistFragment()
                R.id.nav_map -> selectedFragment = MapJournalFragment()

//                R.id.nav_journal -> {
//                    val intent = Intent(this, JournalActivity::class.java)
//                    startActivity(intent)
//                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }
            true
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }
}