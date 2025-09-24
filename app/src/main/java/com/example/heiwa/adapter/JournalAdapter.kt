package com.example.heiwa.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.heiwa.R
import com.example.heiwa.databinding.ItemJournalBinding
import com.example.heiwa.model.JournalEntry

class JournalAdapter (private val context: Context, private val journalList: List<JournalEntry>, private val onItemClick: (JournalEntry) -> Unit) : RecyclerView.Adapter<JournalAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemJournalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = journalList[position]
        Log.d("JournalAdapter", "Binding item at position $position: ${item.placeName}, mediaUri: ${item.mediaUri}")

        holder.binding.placeNameTextView.text = item.placeName
        holder.binding.dateTextView.text = item.date

        if (item.mediaUri != null) {
            Log.d("JournalAdapter", "Loading image URI: ${item.mediaUri}")
            Glide.with(context)
                .load(item.mediaUri)
                .placeholder(R.drawable.manga_3d)
                .into(holder.binding.placeImageView)
        } else {
            Log.w("JournalAdapter", "No media URI for item: ${item.placeName}")
            // Optionally set a default placeholder
            holder.binding.placeImageView.setImageResource(R.drawable.manga_3d)
        }

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }

//        holder.binding.cvItemCard.setOnClickListener {
//            val intent = Intent(context, DetailActivity::class.java).apply {
//                putExtra("item_name", item.name)
//                putExtra("item_description", item.description)
//            }
//            context.startActivity(intent)
//        }
    }


}