package com.pculque.linqme.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDBOpenHelper(
    context: Context,
    factory: SQLiteDatabase.CursorFactory?
) :
    SQLiteOpenHelper(
        context, DATABASE_NAME,
        factory, DATABASE_VERSION
    ) {
    private var database: SQLiteDatabase? = null
    override fun onCreate(db: SQLiteDatabase) {
        val createProductsTable = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_LOGO + " INTEGER," +
                COLUMN_IMAGE_STRING + " TEXT," +
                COLUMN_TYPE + " INTEGER," +
                COLUMN_THUMBNAIL + " INTEGER," +
                COLUMN_BACKGROUND_COLOR + " TEXT," +
                COLUMN_QR_CODE + " TEXT," +
                COLUMN_LABEL_COLOR + " TEXT," +
                COLUMN_VALUE_COLOR + " TEXT," +
                COLUMN_PRIMARY_VALUE + " TEXT," +
                COLUMN_SECONDARY_VALUE + " TEXT," +
                COLUMN_SECONDARY_LABEL + " TEXT," +
                COLUMN_AUXILIARY_VALUE + " TEXT," +
                COLUMN_AUXILIARY_LABEL + " TEXT" + ")")
        db.execSQL(createProductsTable)
        database = db
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "pickme.db"
        const val TABLE_NAME = "card"
        const val COLUMN_ID = "_id"
        const val COLUMN_LOGO = "logo"
        const val COLUMN_QR_CODE = "qrcode"
        const val COLUMN_IMAGE_STRING = "image"
        const val COLUMN_TYPE = "type"
        const val COLUMN_THUMBNAIL = "thumbnail"
        const val COLUMN_BACKGROUND_COLOR = "backgroundColor"
        const val COLUMN_LABEL_COLOR = "labelColor"
        const val COLUMN_VALUE_COLOR = "valueColor"
        const val COLUMN_PRIMARY_VALUE = "primaryValue"
        const val COLUMN_SECONDARY_VALUE = "secondaryValue"
        const val COLUMN_SECONDARY_LABEL = "secondaryLabel"
        const val COLUMN_AUXILIARY_VALUE = "auxiliaryValue"
        const val COLUMN_AUXILIARY_LABEL = "auxiliaryLabel"
    }
}