package com.example.votingapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// DBHelper class for managing database operations
class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "VotingApp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "users"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COL_EMAIL TEXT PRIMARY KEY, $COL_PASSWORD TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Method to validate user credentials
    fun validateUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_EMAIL = ? AND $COL_PASSWORD = ?",
            arrayOf(email, password),
            null, null, null
        )

        val valid = cursor.count > 0 // If there's a record matching email and password, the user is valid
        cursor.close() // Close the cursor after use
        return valid
    }

    // Method to check if user exists
    fun isUserExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close() // Close the cursor after use
        return exists
    }

    // Method to insert a new user into the database
    fun insertUser(email: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_EMAIL, email)
        values.put(COL_PASSWORD, password)

        val result = db.insert(TABLE_NAME, null, values)
        return result != -1L
    }
}
