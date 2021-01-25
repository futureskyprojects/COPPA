package me.vistark.fastdroid.sqlite_db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


abstract class FastdroidDatabaseContext(
    val context: Context,
    val dbName: String,
    val version: Int = 1
) :
    SQLiteOpenHelper(
        context,
        dbName, null, version
    )