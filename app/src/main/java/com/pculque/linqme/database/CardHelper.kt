package com.pculque.linqme.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.pculque.linqme.ui.home.adapter.Card


class CardHelper(context: Context) {
    private val dbHandler = AppDBOpenHelper(context, null)
    //private var database: SQLiteDatabase = dbHandler.readableDatabase

    fun addCard(card: Card): Long {
        Log.d("Add", "\n")
        val values = ContentValues()
        values.put(AppDBOpenHelper.COLUMN_TYPE, card.type)
        values.put(AppDBOpenHelper.COLUMN_LOGO, card.logo)
        values.put(AppDBOpenHelper.COLUMN_IMAGE_STRING, card.image)
        values.put(AppDBOpenHelper.COLUMN_THUMBNAIL, card.thumbnail)
        values.put(AppDBOpenHelper.COLUMN_BACKGROUND_COLOR, card.backgroundColor)
        values.put(AppDBOpenHelper.COLUMN_LABEL_COLOR, card.labelColor)
        values.put(AppDBOpenHelper.COLUMN_VALUE_COLOR, card.valueColor)
        values.put(AppDBOpenHelper.COLUMN_QR_CODE, card.qrCode)
        values.put(AppDBOpenHelper.COLUMN_PRIMARY_VALUE, card.primaryValue)
        values.put(AppDBOpenHelper.COLUMN_SECONDARY_LABEL, card.secondaryLabel)
        values.put(AppDBOpenHelper.COLUMN_SECONDARY_VALUE, card.secondaryValue)
        values.put(AppDBOpenHelper.COLUMN_AUXILIARY_LABEL, card.auxiliaryLabel)
        values.put(AppDBOpenHelper.COLUMN_AUXILIARY_VALUE, card.auxiliaryValue)

        val db = dbHandler.writableDatabase
        val id = db.insert(AppDBOpenHelper.TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun isSetupDB(): Boolean {
        return !getAllCards().isNullOrEmpty()
    }

    fun size(): Int {
        return if (getAllCards().isNullOrEmpty())
            0
        else getAllCards().size
    }

    fun clear() {
        val db = dbHandler.writableDatabase
        db.delete(AppDBOpenHelper.TABLE_NAME, null, null)
    }

    fun updateCard(card: Card) {
        val cv = ContentValues()
        cv.put(
            AppDBOpenHelper.COLUMN_PRIMARY_VALUE,
            card.primaryValue
        )
        cv.put(AppDBOpenHelper.COLUMN_SECONDARY_LABEL, card.secondaryLabel)
        cv.put(AppDBOpenHelper.COLUMN_SECONDARY_VALUE, card.secondaryValue)
        cv.put(AppDBOpenHelper.COLUMN_AUXILIARY_VALUE, card.auxiliaryValue)
        cv.put(AppDBOpenHelper.COLUMN_IMAGE_STRING, card.image)
        cv.put(AppDBOpenHelper.COLUMN_QR_CODE, card.qrCode)

        val db = dbHandler.writableDatabase

        db.update(AppDBOpenHelper.TABLE_NAME, cv, "${AppDBOpenHelper.COLUMN_ID}=" + card.id, null)
    }

    fun getCard(id: Int): Card? {
        val card = Card()
        val query =
            "SELECT * FROM ${AppDBOpenHelper.TABLE_NAME} WHERE ${AppDBOpenHelper.COLUMN_ID} = ?"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            card.id = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_ID))
            card.type = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_TYPE))
            card.logo = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_LOGO))
            card.image =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_IMAGE_STRING))
            card.thumbnail =
                cursor.getInt(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_THUMBNAIL))
            card.backgroundColor =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_BACKGROUND_COLOR))
            card.labelColor =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_LABEL_COLOR))
            card.valueColor =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_VALUE_COLOR))
            card.primaryValue =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_PRIMARY_VALUE))
            card.qrCode =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_QR_CODE))
            card.secondaryLabel =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_SECONDARY_LABEL))
            card.secondaryValue =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_SECONDARY_VALUE))
            card.auxiliaryLabel =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_AUXILIARY_LABEL))
            card.auxiliaryValue =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_AUXILIARY_VALUE))
        } else {
            return null
        }
        cursor.close()
        return card
    }

    private fun getAllCardCursor(): Cursor? {
        val db = dbHandler.readableDatabase
        return db.rawQuery("SELECT * FROM ${AppDBOpenHelper.TABLE_NAME}", null)
    }

    fun getAllCards(): List<Card> {
        var card: Card
        val list = mutableListOf<Card>()
        val cursor = getAllCardCursor()
        //cursor!!.moveToFirst()
        while (cursor!!.moveToNext()) {
            card = Card()
            card.id = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_ID))
            card.type = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_TYPE))
            card.logo = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_LOGO))
            card.thumbnail =
                cursor.getInt(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_THUMBNAIL))
            card.backgroundColor =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_BACKGROUND_COLOR))
            card.labelColor =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_LABEL_COLOR))
            card.valueColor =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_VALUE_COLOR))
            card.qrCode =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_QR_CODE))
            card.primaryValue =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_PRIMARY_VALUE))
            card.secondaryLabel =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_SECONDARY_LABEL))
            card.secondaryValue =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_SECONDARY_VALUE))
            card.auxiliaryLabel =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_AUXILIARY_LABEL))
            card.auxiliaryValue =
                cursor.getString(cursor.getColumnIndexOrThrow(AppDBOpenHelper.COLUMN_AUXILIARY_VALUE))

            list.add(card)
        }
        cursor.close()

        return list
    }
}