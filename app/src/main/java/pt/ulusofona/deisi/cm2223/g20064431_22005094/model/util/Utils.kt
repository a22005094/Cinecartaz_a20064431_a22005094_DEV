package pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomDate
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie
import java.util.Calendar
import kotlin.math.*


// Funções de "utilitário" que poderão ser úteis no projeto.

object Utils {

    // Um objeto a utilizar nos fragmentos de "Registar Filme" e "Pesquisar Filme" (lançado a partir do primeiro),
    // para facilitar a seleção de um Filme ao regressar ao fragmento "Registar Filme".
    var currentlySelectedMovie: OMDBMovie? = null

    var lastKnownLocation : LatLng = LatLng(0.0, 0.0)


    /*
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
            Date().time,
            3,
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

    var watchedMovie2 = WatchedMovie(
        UUID.randomUUID().toString(),
        OMDBMovie(
            "Blade Runner",
            1982,
            "tt0083658",
            "Action, Drama, Sci-Fi",
            8.1,
            "Ridley Scott",
            "A blade runner must pursue and terminate four replicants who stole a ship in space and have returned to Earth to find their creator.",
            Date().time,
            50,
            "https://m.media-amazon.com/images/M/MV5BNzQzMzJhZTEtOWM4NS00MTdhLTg0YjgtMjM4MDRkZjUwZDBlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg",
            //"https://m.media-amazon.com/images/M/MV5BMTdjZTliODYtNWExMi00NjQ1LWIzN2MtN2Q5NTg5NTk3NzliL2ltYWdlXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_SX300.jpg"
            null
        ),
        Cinema(
            1357, "UCI Cinemas Ubbo", "UCI Cinemas",
            38.77588027958963, -9.22224658418028,
            "Avenida Cruzeiro Seixas, 7", "2650-504", "Amadora",
            mutableListOf()
        ),
        7,
        Date().time,
        "No comments"
    )

    /
    var watchedMovies: MutableList<WatchedMovie> = mutableListOf(
        watchedMovie,
        watchedMovie2,
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
    */

    var currentLocation : LatLng = LatLng(0.0,0.0);

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


    // Function to decode rating into a string value
    fun decodeSatisfactionString(context: Context, rating: Int) {
        when (rating) {
            1, 2 -> context.getString(R.string.rating_too_weak)
            3, 4 -> context.getString(R.string.rating_weak)
            5, 6 -> context.getString(R.string.rating_avg)
            7, 8 -> context.getString(R.string.rating_good)
            9, 10 -> context.getString(R.string.rating_xl)
        }
    }

    // method to define a color, based on rating.
    // specific colors are used, based on available google maps marker colors
    // https://developers.google.com/android/reference/com/google/android/gms/maps/model/BitmapDescriptorFactory#constant-summary
    fun decodeSatisfactionColor(rating: Int): Float {
        return when (rating) {
            1, 2 -> BitmapDescriptorFactory.HUE_RED
            3, 4 -> BitmapDescriptorFactory.HUE_ORANGE
            5, 6 -> BitmapDescriptorFactory.HUE_YELLOW
            7, 8 -> BitmapDescriptorFactory.HUE_BLUE
            9, 10 -> BitmapDescriptorFactory.HUE_GREEN
            else -> BitmapDescriptorFactory.HUE_CYAN
        }
    }

    fun calculateDistance(coord1: LatLng, coord2: LatLng): Double {
        val earthRadius = 6371 // Radius of the Earth in kilometers

        // Convert latitude and longitude from degrees to radians
        val lat1 = Math.toRadians(coord1.latitude)
        val lon1 = Math.toRadians(coord1.longitude)
        val lat2 = Math.toRadians(coord2.latitude)
        val lon2 = Math.toRadians(coord2.longitude)

        // Haversine formula
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1
        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        // Calculate the distance
        return earthRadius * c * 1000
    }

    fun calcDistancesOnMoviesList(watchedMovies: MutableList<WatchedMovie>, point: LatLng): MutableList<WatchedMovie>{
        var newList = watchedMovies

        newList.forEach{
            it.calcDistance = calculateDistance(
                LatLng(it.theatre.latitude, it.theatre.longitude),point) }

        return newList
    }

    fun filterMoviesList(watchedMovies: MutableList<WatchedMovie>, keywords: String): MutableList<WatchedMovie> {
        var newList = mutableListOf<WatchedMovie>()

        watchedMovies.filterTo(newList,  {
            keywords in it.movie.title
        })

        return newList
    }
}