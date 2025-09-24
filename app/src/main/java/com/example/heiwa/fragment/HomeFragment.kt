package com.example.heiwa.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.heiwa.JournalActivity
import com.example.heiwa.JournalDetailActivity
import com.example.heiwa.R
import com.example.heiwa.WishlistActivity
import com.example.heiwa.WishlistDetailActivity
import com.example.heiwa.adapter.JournalAdapter
import com.example.heiwa.adapter.RecentEntriesAdapter
import com.example.heiwa.adapter.RecentWishlistAdapter
import com.example.heiwa.database.DatabaseHelper
import com.example.heiwa.database.dao.JournalEntryDao
import com.example.heiwa.database.dao.WishlistDao
import com.example.heiwa.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var journalEntryDao: JournalEntryDao
    private lateinit var recentEntriesAdapter: RecentEntriesAdapter
    private lateinit var wishlistDao: WishlistDao
    private lateinit var recentWishlistAdapter: RecentWishlistAdapter

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        if (userId == -1) {
            Log.e("JournalFragment", "Invalid user ID")
            return
        }

        dbHelper = DatabaseHelper(requireContext())
        journalEntryDao = JournalEntryDao(dbHelper.readableDatabase)
        wishlistDao = WishlistDao(dbHelper.readableDatabase)

        val username = sharedPref.getString("username", "Guest")
        binding.welcomeTextView.text = "Welcome back, $username"

        setupQuickActions()
        setupRecentEntries(userId)
        setupWishlist(userId)
    }

    private fun setupQuickActions() {
        binding.newEntryButton.setOnClickListener {
            startActivity(Intent(activity, JournalActivity::class.java))
        }
        binding.viewWishlistButton.setOnClickListener {
            startActivity((Intent(activity, WishlistActivity::class.java)))
        }
    }

    private fun setupWishlist(userId: Int) {
        try {
            val items = wishlistDao.getTop4RecentWishlistsForUser(userId)

            for (item in items) {
                Log.d("HomeFragment", "Wishlist Entry: $item")
            }

            recentWishlistAdapter = RecentWishlistAdapter(requireContext(), items) { selectedItem ->
                val intent = Intent(requireContext(), WishlistDetailActivity::class.java)
                Log.d("HomeFragment", "wishlistId = ${selectedItem.wishlistId}")
                intent.putExtra("wishlist_id", selectedItem.wishlistId)
                startActivity(intent)
            }
            binding.wishlistRecyclerView.adapter = recentWishlistAdapter
            binding.wishlistRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        } catch (e: Exception) {
            Log.e("HomeFragment", "Error setting up RecyclerView for recent wishlists", e)
        }
    }

    private fun setupRecentEntries(userId: Int) {
        try {
            val items = journalEntryDao.getTop4RecentJournalEntriesForUser(userId)

            for (item in items) {
                Log.d("HomeFragment", "Journal Entry: $item")
            }

            recentEntriesAdapter = RecentEntriesAdapter(requireContext(), items) { selectedItem ->
                val intent = Intent(requireContext(), JournalDetailActivity::class.java)
                Log.d("HomeFragment", "journalId = ${selectedItem.journalId}")
                intent.putExtra("journal_id", selectedItem.journalId)
                startActivity(intent)
            }
            binding.recentEntriesRecyclerView.adapter = recentEntriesAdapter
            binding.recentEntriesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        } catch (e: Exception) {
            Log.e("HomeFragment", "Error setting up RecyclerView for recent entries", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}