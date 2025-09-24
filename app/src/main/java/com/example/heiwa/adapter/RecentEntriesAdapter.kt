package com.example.heiwa.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.heiwa.R
import com.example.heiwa.databinding.ItemJournalBinding
import com.example.heiwa.databinding.ItemPlaceHorizontalBinding
import com.example.heiwa.model.JournalEntry

class RecentEntriesAdapter (private val context: Context, private val journalList: List<JournalEntry>, private val onItemClick: (JournalEntry) -> Unit) : RecyclerView.Adapter<RecentEntriesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPlaceHorizontalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaceHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentEntriesAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = journalList[position]
        Log.d("RecentEntriesAdapter", "Binding item at position $position: ${item.placeName}, mediaUri: ${item.mediaUri}")

        holder.binding.placeNameTextView.text = item.placeName
        holder.binding.dateTextView.text = item.date
        holder.binding.ratingBar.rating = item.rating

        if (item.mediaUri != null) {
            Log.d("RecentEntriesAdapter", "Loading image URI: ${item.mediaUri}")
            Glide.with(context)
                .load(item.mediaUri)
                .placeholder(R.drawable.manga_3d)
                .into(holder.binding.placeImageView)
        } else {
            Log.w("RecentEntriesAdapter", "No media URI for item: ${item.placeName}")
            // Optionally set a default placeholder
            holder.binding.placeImageView.setImageResource(R.drawable.manga_3d)
        }

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

}