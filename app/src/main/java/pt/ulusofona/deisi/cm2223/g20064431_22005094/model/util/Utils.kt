package pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomDate
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
import java.io.IOException
import java.util.Calendar

// Funções de "utilitário" que poderão ser úteis no projeto.

object Utils {

    // Um objeto a utilizar nos fragmentos de "Registar Filme" e "Pesquisar Filme" (lançado a partir do primeiro),
    // para facilitar a seleção de um Filme ao regressar ao fragmento "Registar Filme".
    var currentlySelectedMovie: OMDBMovie? = null


    // Credits: ChatGPT "using android api 23, how can I compare if a date is on the same day as today?"
    fun isToday(dtCompare: CustomDate): Boolean {
        val calDate1 = Calendar.getInstance()  // coloca a data de hoje automaticamente

        return (calDate1.get(Calendar.YEAR) == dtCompare.getAno()
                && calDate1.get(Calendar.MONTH) == dtCompare.getMes()
                && calDate1.get(Calendar.DAY_OF_MONTH) == dtCompare.getDia())
    }


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