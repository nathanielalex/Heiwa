package com.example.heiwa.database.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.heiwa.database.tables.UserTable
import com.example.heiwa.model.User

class UserDao (private val db: SQLiteDatabase) {
    fun insertUser(user: User): Long {
        val values = ContentValues().apply {
            put(UserTable.COL_USERNAME, user.username)
            put(UserTable.COL_PASSWORD, user.passwordHash)
            put(UserTable.COL_PHONE, user.phoneNumber)
            put(UserTable.COL_CREATED_AT, user.createdAt)
        }
        return db.insert(UserTable.TABLE_NAME, null, values)
    }

    fun getUserById(id: Int): User? {
        val cursor = db.query(
            UserTable.TABLE_NAME,
            null,
            "${UserTable.COL_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.COL_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_USERNAME)),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_PASSWORD)),
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_PHONE)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_CREATED_AT))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun getByUsername(username: String): User? {
        val cursor = db.query(
            UserTable.TABLE_NAME,
            null,
            "${UserTable.COL_USERNAME} = ?",
            arrayOf(username),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.COL_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_USERNAME)),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_PASSWORD)),
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_PHONE)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_CREATED_AT))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }


}