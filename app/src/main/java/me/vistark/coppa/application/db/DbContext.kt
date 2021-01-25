package me.vistark.coppa.application.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import me.vistark.fastdroid.sqlite_db.FastdroidDatabaseContext

class DbContext(context: Context) : FastdroidDatabaseContext(context, "COPPA_DB", 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}