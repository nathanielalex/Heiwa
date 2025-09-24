package com.example.heiwa.database.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.heiwa.database.tables.JournalEntryTable
import com.example.heiwa.database.tables.WishlistTable
import com.example.heiwa.model.JournalEntry
import com.example.heiwa.model.Wishlist

class WishlistDao(private val db: SQLiteDatabase) {
    fun insertWishlist(entry: Wishlist, userId: Int): Long {
        val values = ContentValues().apply {
            put(WishlistTable.COL_USER_ID, userId)
            put(WishlistTable.COL_PLACE_NAME, entry.placeName)
            put(WishlistTable.COL_LATITUDE, entry.latitude)
            put(WishlistTable.COL_LONGITUDE, entry.longitude)
            put(WishlistTable.COL_NOTES, entry.notes)
            put(WishlistTable.COL_MEDIA_URI, entry.mediaUri?.toString())
        }
        return db.insert(WishlistTable.TABLE_NAME, null, values)
    }

    fun getWishlistById(id: Int): Wishlist? {
        val cursor = db.query(
            WishlistTable.TABLE_NAME,
            null,
            "${WishlistTable.COL_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        val entry = if (cursor.moveToFirst()) {
            val mediaUriString = cursor.getString(cursor.getColumnIndexOrThrow(WishlistTable.COL_MEDIA_URI))

            val entry = Wishlist(
                wishlistId = cursor.getInt(cursor.getColumnIndexOrThrow((WishlistTable.COL_ID))),
                placeName = cursor.getString(cursor.getColumnIndexOrThrow(WishlistTable.COL_PLACE_NAME)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(WishlistTable.COL_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(WishlistTable.COL_LONGITUDE)),
                notes = cursor.getString(cursor.getColumnIndexOrThrow(WishlistTable.COL_NOTES)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(WishlistTable.COL_USER_ID)),
                mediaUri = if (!mediaUriString.isNullOrBlank()) mediaUriString else null // pass String, not Uri
            )
            cursor.close()
            entry
        } else {
            cursor.close()
            null
        }
        return entry
    }

    fun getAllWishlistsForUser(userId: Int): List<Wishlist> {
        val cursor = db.query(
            WishlistTable.TABLE_NAME,
            null,
            "${WishlistTable.COL_USER_ID} = ?",
            arrayOf(userId.toString()),
            null, null,
            "${WishlistTable.COL_CREATED_AT} DESC"
        )

        val entries = mutableListOf<Wishlist>()

        while (cursor.moveToNext()) {
            val mediaUriString = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_MEDIA_URI))

            val entry = Wishlist(
                wishlistId = cursor.getInt(cursor.getColumnIndexOrThrow((WishlistTable.COL_ID))),
                placeName = cursor.getString(cursor.getColumnIndexOrThrow(WishlistTable.COL_PLACE_NAME)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(WishlistTable.COL_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(WishlistTable.COL_LONGITUDE)),
                notes = cursor.getString(cursor.getColumnIndexOrThrow(WishlistTable.COL_NOTES)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(WishlistTable.COL_USER_ID)),
                mediaUri = if (!mediaUriString.isNullOrBlank()) mediaUriString else null // pass String, not Uri
            )
            entries.add(entry)
        }
        cursor.close()
        return entries
    }

    fun getTop4RecentWishlistsForUser(userId: Int): List<Wishlist> {
        val cursor = db.query(
            WishlistTable.TABLE_NAME,
            null,
            "${WishlistTable.COL_USER_ID} = ?",
            arrayOf(userId.toString()),
            null, null,
            "${WishlistTable.COL_CREATED_AT} DESC",
            "4" // LIMIT clause
        )

        val entries = mutableListOf<Wishlist>()

        while (cursor.moveToNext()) {
            val mediaUriString = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_MEDIA_URI))

            val entry = Wishlist(
                wishlistId = cursor.getInt(cursor.getColumnIndexOrThrow(WishlistTable.COL_ID)),
                placeName = cursor.getString(cursor.getColumnIndexOrThrow(WishlistTable.COL_PLACE_NAME)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(WishlistTable.COL_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(WishlistTable.COL_LONGITUDE)),
                notes = cursor.getString(cursor.getColumnIndexOrThrow(WishlistTable.COL_NOTES)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(WishlistTable.COL_USER_ID)),
                mediaUri = if (!mediaUriString.isNullOrBlank()) mediaUriString else null
            )
            entries.add(entry)
        }
        cursor.close()
        return entries
    }
    fun deleteWishlistEntryById(wishlistId: Int): Boolean {
        val rowsDeleted = db.delete(
            WishlistTable.TABLE_NAME,
            "${WishlistTable.COL_ID} = ?",
            arrayOf(wishlistId.toString())
        )
        return rowsDeleted > 0
    }

}