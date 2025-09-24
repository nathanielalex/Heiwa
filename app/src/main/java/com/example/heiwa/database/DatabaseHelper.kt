package com.example.heiwa.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.heiwa.database.tables.JournalEntryTable
import com.example.heiwa.database.tables.UserTable
import com.example.heiwa.database.tables.WishlistTable

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "app_database.db"
        const val DATABASE_VERSION = 6
    }

    override fun onCreate(db: SQLiteDatabase) {
        UserTable.onCreate(db)
        JournalEntryTable.onCreate(db)
        WishlistTable.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        UserTable.onUpgrade(db)
        JournalEntryTable.onUpgrade(db)
        WishlistTable.onUpgrade(db)
    }
}