package com.example.heiwa.database.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.example.heiwa.database.tables.JournalEntryTable
import com.example.heiwa.model.JournalEntry

class JournalEntryDao(private val db: SQLiteDatabase) {
    fun insertJournalEntry(entry: JournalEntry, userId: Int): Long {
        val values = ContentValues().apply {
            put(JournalEntryTable.COL_USER_ID, userId)
            put(JournalEntryTable.COL_PLACE_NAME, entry.placeName)
            put(JournalEntryTable.COL_LATITUDE, entry.latitude)
            put(JournalEntryTable.COL_LONGITUDE, entry.longitude)
            put(JournalEntryTable.COL_NOTES, entry.notes)
            put(JournalEntryTable.COL_RATING, entry.rating)
            put(JournalEntryTable.COL_DATE, entry.date)
            put(JournalEntryTable.COL_MEDIA_URI, entry.mediaUri?.toString())
        }
        return db.insert(JournalEntryTable.TABLE_NAME, null, values)
    }

    fun getJournalEntryById(id: Int): JournalEntry? {
        val cursor = db.query(
            JournalEntryTable.TABLE_NAME,
            null,
            "${JournalEntryTable.COL_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        val entry = if (cursor.moveToFirst()) {
            val mediaUriString = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_MEDIA_URI))

            val entry = JournalEntry(
                journalId = cursor.getInt(cursor.getColumnIndexOrThrow((JournalEntryTable.COL_ID))),
                placeName = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_PLACE_NAME)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_LONGITUDE)),
                notes = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_NOTES)),
                rating = cursor.getFloat(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_RATING)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_DATE)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_USER_ID)),
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

    fun getAllJournalEntriesForUser(userId: Int): List<JournalEntry> {
        val cursor = db.query(
            JournalEntryTable.TABLE_NAME,
            null,
            "${JournalEntryTable.COL_USER_ID} = ?",
            arrayOf(userId.toString()),
            null, null,
            "${JournalEntryTable.COL_CREATED_AT} DESC"
        )

        val entries = mutableListOf<JournalEntry>()

        while (cursor.moveToNext()) {
            val mediaUriString = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_MEDIA_URI))
//            val mediaUri = if (!mediaUriString.isNullOrBlank()) Uri.parse(mediaUriString) else null

            val entry = JournalEntry(
                journalId = cursor.getInt(cursor.getColumnIndexOrThrow((JournalEntryTable.COL_ID))),
                placeName = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_PLACE_NAME)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_LONGITUDE)),
                notes = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_NOTES)),
                rating = cursor.getFloat(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_RATING)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_DATE)),
                userId = userId,
                mediaUri = if (!mediaUriString.isNullOrBlank()) mediaUriString else null // pass String, not Uri
            )
            entries.add(entry)
        }
        cursor.close()
        return entries
    }

    fun getTop4RecentJournalEntriesForUser(userId: Int): List<JournalEntry> {
        val cursor = db.query(
            JournalEntryTable.TABLE_NAME,
            null,
            "${JournalEntryTable.COL_USER_ID} = ?",
            arrayOf(userId.toString()),
            null, null,
            "${JournalEntryTable.COL_CREATED_AT} DESC",
            "4" // LIMIT to top 4 entries
        )

        val entries = mutableListOf<JournalEntry>()

        while (cursor.moveToNext()) {
            val mediaUriString = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_MEDIA_URI))

            val entry = JournalEntry(
                journalId = cursor.getInt(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_ID)),
                placeName = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_PLACE_NAME)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_LONGITUDE)),
                notes = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_NOTES)),
                rating = cursor.getFloat(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_RATING)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(JournalEntryTable.COL_DATE)),
                userId = userId,
                mediaUri = if (!mediaUriString.isNullOrBlank()) mediaUriString else null
            )
            entries.add(entry)
        }
        cursor.close()
        return entries
    }
    fun deleteJournalEntryById(journalId: Int): Boolean {
        val rowsDeleted = db.delete(
            JournalEntryTable.TABLE_NAME,
            "${JournalEntryTable.COL_ID} = ?",
            arrayOf(journalId.toString())
        )
        return rowsDeleted > 0
    }

}