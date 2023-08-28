package pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.IOException
import java.util.Calendar

// Funções de "utilitário" que poderão ser úteis no projeto.

object Utils {

    fun getDataDeOntemEmMillis(): Long {
        val cal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        return cal.timeInMillis
    }

    fun isNumeric(text: String): Boolean {
        return text.toIntOrNull() != null
    }

    // Créditos: inspiração @ChatGPT: "How can I convert an image uri to bitmap using android api 23?"
    fun convertUriToBitmap(uri: Uri, contentResolver: ContentResolver): Bitmap? {
        val bitmapResult: Bitmap

        try {
            contentResolver.openInputStream(uri).use {
                bitmapResult = BitmapFactory.decodeStream(it)
            }
            return bitmapResult
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

}