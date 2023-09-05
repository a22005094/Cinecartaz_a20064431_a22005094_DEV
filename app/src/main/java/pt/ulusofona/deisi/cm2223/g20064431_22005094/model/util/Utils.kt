package pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinema
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomDate
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie
import java.util.Calendar
import java.util.Date
import java.util.UUID


// Funções de "utilitário" que poderão ser úteis no projeto.

object Utils {

    // Um objeto a utilizar nos fragmentos de "Registar Filme" e "Pesquisar Filme" (lançado a partir do primeiro),
    // para facilitar a seleção de um Filme ao regressar ao fragmento "Registar Filme".
    var currentlySelectedMovie: OMDBMovie? = null


    // TODO: Temp list of watched movies
    var watchedMovie = WatchedMovie(
        UUID.randomUUID().toString(), OMDBMovie(
            "Blade Runner",
            1982,
            "tt0083658",
            "Action, Drama, Sci-Fi",
            8.1,
            "Ridley Scott",
            "A blade runner must pursue and terminate four replicants who stole a ship in space and have returned to Earth to find their creator.",
            "https://m.media-amazon.com/images/M/MV5BNzQzMzJhZTEtOWM4NS00MTdhLTg0YjgtMjM4MDRkZjUwZDBlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg",

            // TODO - sorry, não consegui agora fornecer aqui um Bitmap diretamente p/ refletir a alteração na classe OMDBMovie
            null
            //"https://m.media-amazon.com/images/M/MV5BMTdjZTliODYtNWExMi00NjQ1LWIzN2MtN2Q5NTg5NTk3NzliL2ltYWdlXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_SX300.jpg"
        ), Cinema(
            2589,
            "Cinemas NOS Centro Comercial Colombo",
            "Cinemas NOS",
            38.75366130000005,
            -9.205767760449193,
            "Centro Comercial Colombo, Avenida Lusíada",
            "1500-392",
            "Lisboa",
            mutableListOf() // TODO colocar links para imagens se necessário
        ), 7, Date().time, "No comments"
    )

    var watchedMovies: MutableList<WatchedMovie> = mutableListOf(
        watchedMovie,
        watchedMovie,
        watchedMovie,
        watchedMovie,
        watchedMovie,
        watchedMovie,
        watchedMovie,
        watchedMovie,
        watchedMovie,
        watchedMovie,
        watchedMovie,
        watchedMovie
    )


    // Credits: ChatGPT "using android api 23, how can I compare if a date is on the same day as today?"
    fun isToday(dtCompare: CustomDate): Boolean {
        val calDate1 = Calendar.getInstance()  // coloca a data de hoje automaticamente

        return (calDate1.get(Calendar.YEAR) == dtCompare.getYear()
                && calDate1.get(Calendar.MONTH) == dtCompare.getMonth()
                && calDate1.get(Calendar.DAY_OF_MONTH) == dtCompare.getDayOfMonth())
    }


    fun getYesterdayDateInMillis(): Long {
        val cal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        return cal.timeInMillis
    }

    /*
        fun isInt(text: String): Boolean = return text.toIntOrNull() != null
        fun isDouble(text: String): Boolean = return text.toDoubleOrNull() != null
    */


    // Credits: Esconder teclado Android ao clicar num botão
    // https://stackoverflow.com/questions/55505049/how-to-close-the-soft-keyboard-from-a-fragment-using-kotlin
    fun closeKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(view.windowToken, 0)
    }

}