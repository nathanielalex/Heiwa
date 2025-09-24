package com.example.heiwa.database.tables

import android.database.sqlite.SQLiteDatabase

object WishlistTable {
    const val TABLE_NAME = "wishlists"
    const val COL_ID = "wishlist_id"
    const val COL_USER_ID = "user_id" // Foreign key (assuming journal entries are tied to a user)

    const val COL_PLACE_NAME = "place_name"
    const val COL_LATITUDE = "latitude"
    const val COL_LONGITUDE = "longitude"
    const val COL_NOTES = "notes"
    const val COL_MEDIA_URI = "media_uri" // Serialized as comma-separated string or JSON

    const val COL_CREATED_AT = "created_at"

    fun onCreate(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_ID INTEGER NOT NULL,
                $COL_PLACE_NAME TEXT NOT NULL,
                $COL_LATITUDE REAL NOT NULL,
                $COL_LONGITUDE REAL NOT NULL,
                $COL_NOTES TEXT,
                $COL_MEDIA_URI TEXT,
                $COL_CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COL_USER_ID) REFERENCES ${UserTable.TABLE_NAME}(${UserTable.COL_ID}) ON DELETE CASCADE
            );
        """.trimIndent()
        db.execSQL(sql)
    }

    fun onUpgrade(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}