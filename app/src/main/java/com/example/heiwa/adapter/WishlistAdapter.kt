package com.example.heiwa.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.heiwa.R
import com.example.heiwa.databinding.ItemWishlistBinding
import com.example.heiwa.model.Wishlist

class WishlistAdapter (private val context: Context, private val wishlists: List<Wishlist>, private val onItemClick: (Wishlist) -> Unit) : RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemWishlistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return wishlists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = wishlists[position]
        Log.d("WishlistAdapter", "Binding item at position $position: ${item.placeName}, mediaUri: ${item.mediaUri}")

        holder.binding.placeNameTextView.text = item.placeName

        if (item.mediaUri != null) {
            Log.d("WishlistAdapter", "Loading image URI: ${item.mediaUri}")
            Glide.with(context)
                .load(item.mediaUri)
                .placeholder(R.drawable.manga_3d)
                .into(holder.binding.placeImageView)
        } else {
            Log.w("WishlistAdapter", "No media URI for item: ${item.placeName}")
            // Optionally set a default placeholder
            holder.binding.placeImageView.setImageResource(R.drawable.manga_3d)
        }

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }

    }

}