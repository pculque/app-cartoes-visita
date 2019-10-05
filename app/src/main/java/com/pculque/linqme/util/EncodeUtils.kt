package com.pculque.linqme.util

import android.graphics.*
import android.util.Base64
import java.io.ByteArrayOutputStream


object EncodeUtils {

    fun decodeFromBase64ToBitmap(encodedImage: String): Bitmap {

        val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun convertToBase64(bitmap: Bitmap): String {

        val byteArrayOutputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val byteArrayImage = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
    }
}