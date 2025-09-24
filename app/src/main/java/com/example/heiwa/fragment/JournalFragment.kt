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
import com.example.heiwa.adapter.JournalAdapter
import com.example.heiwa.database.DatabaseHelper
import com.example.heiwa.database.dao.JournalEntryDao
import com.example.heiwa.database.dao.UserDao
import com.example.heiwa.databinding.FragmentJournalBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [JournalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var journalEntryDao: JournalEntryDao
    private lateinit var journalAdapter: JournalAdapter

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
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_journal, container, false)
        _binding = FragmentJournalBinding.inflate(inflater, container, false)

        dbHelper = DatabaseHelper(requireContext())
        journalEntryDao = JournalEntryDao(dbHelper.readableDatabase)

        setUpRecycler()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPlaceFab.setOnClickListener {
            val intent = Intent(requireContext(), JournalActivity::class.java)
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
            val items = journalEntryDao.getAllJournalEntriesForUser(userId)

            for (item in items) {
                Log.d("JournalFragment", "Journal Entry: $item")
            }

            journalAdapter = JournalAdapter(requireContext(), items) { selectedItem ->
                val intent = Intent(requireContext(), JournalDetailActivity::class.java)
                Log.d("JournalFragment", "journalId = $selectedItem.journalId")
                intent.putExtra("journal_id", selectedItem.journalId)
                startActivity(intent)
            }
            binding.placesRecyclerView.adapter = journalAdapter
            binding.placesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        } catch (e: Exception) {
            Log.e("JournalFragment", "Error setting up RecyclerView", e)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment JournalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            JournalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}