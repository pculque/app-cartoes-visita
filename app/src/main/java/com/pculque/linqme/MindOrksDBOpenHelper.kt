package com.pculque.linqme

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MindOrksDBOpenHelper(
    context: Context,
    factory: SQLiteDatabase.CursorFactory?
) :
    SQLiteOpenHelper(
        context, DATABASE_NAME,
        factory, DATABASE_VERSION
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        val createProductsTable = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_PRIMARY_VALUE
                + " TEXT" + ")")
        db.execSQL(createProductsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addName(card: Card) {
        val values = ContentValues()
        values.put(COLUMN_PRIMARY_VALUE, card.primaryValue)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllName(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "mindorksName.db"
        const val TABLE_NAME = "name"
        const val COLUMN_ID = "_id"
        const val COLUMN_PRIMARY_VALUE = "username"
    }
}