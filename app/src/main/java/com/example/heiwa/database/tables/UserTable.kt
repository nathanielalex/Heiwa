package com.example.heiwa.database.tables

import android.database.sqlite.SQLiteDatabase

object UserTable {
    const val TABLE_NAME = "users"
    const val COL_ID = "user_id"
    const val COL_USERNAME = "username"
    const val COL_PASSWORD = "password_hash"
    const val COL_PHONE = "phone_number"
    const val COL_CREATED_AT = "created_at"

    fun onCreate(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_PHONE TEXT,
                $COL_CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()
        db.execSQL(sql)
    }

    fun onUpgrade(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

}