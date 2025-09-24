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
import com.example.heiwa.R
import com.example.heiwa.WishlistActivity
import com.example.heiwa.WishlistDetailActivity
import com.example.heiwa.adapter.WishlistAdapter
import com.example.heiwa.database.DatabaseHelper
import com.example.heiwa.database.dao.WishlistDao
import com.example.heiwa.databinding.FragmentWishlistBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WishlistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WishlistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var wishlistDao: WishlistDao
    private lateinit var wishlistAdapter: WishlistAdapter

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
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)

        dbHelper = DatabaseHelper(requireContext())
        wishlistDao = WishlistDao(dbHelper.readableDatabase)

        setUpRecycler()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPlaceFab.setOnClickListener {
            val intent = Intent(requireContext(), WishlistActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecycler(){
        val sharedPref = requireActivity().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        if (userId == -1) {
            Log.e("JournalFragment", "Invalid user ID")
            return
        }

        try {
            val items = wishlistDao.getAllWishlistsForUser(userId)

            for (item in items) {
                Log.d("WishlistFragment", "Entry: $item")
            }

            wishlistAdapter = WishlistAdapter(requireContext(), items) { selectedItem ->
                val intent = Intent(requireContext(), WishlistDetailActivity::class.java)
                Log.d("WishlistFragment", "wishlistId = ${selectedItem.wishlistId}")
                intent.putExtra("wishlist_id", selectedItem.wishlistId)
                startActivity(intent)
            }
            binding.wishRecyclerView.adapter = wishlistAdapter
            binding.wishRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        } catch (e: Exception) {
            Log.e("WishlistFragment", "Error setting up RecyclerView", e)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WishlistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WishlistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}