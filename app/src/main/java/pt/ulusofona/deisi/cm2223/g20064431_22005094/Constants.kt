package pt.ulusofona.deisi.cm2223.g20064431_22005094

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

// Inspirado na demo de LOTR publicada em Github pelos Professores...
// De facto, um ficheiro "geral" para constantes é útil e simplifica um pouco mais ;)

const val OMDB_API_TOKEN = "2d3455ee"
const val OMDB_API_BASE_URL = "https://www.omdbapi.com/?apikey=$OMDB_API_TOKEN"

// Predefine já o formato esperado para a pesquisa de Filmes.
const val OMDB_API_URL_MOVIE_TITLE_SEARCH = "$OMDB_API_BASE_URL&type=movie&s="
const val OMDB_API_URL_MOVIE_DETAILS = "$OMDB_API_BASE_URL&i="
const val IMDB_BASE_URL = "https://www.imdb.com/title/"  // for opening IMDB links on browser (@ WatchedMovie details)

// A default asset for missing pictures.
const val ASSET_PLACEHOLDER_NO_IMAGE = "movie_no_image.png"

// Global constant with the maximum rating value
const val MAX_RATING_VALUE = 10

// map bound box
val MAP_BOUNDS_NE = LatLng(42.1303759027, -5.8242740865)
val MAP_BOUNDS_SW = LatLng(36.9142841075, -9.76836588338)
val MAP_BOUNDS    = LatLngBounds(MAP_BOUNDS_SW, MAP_BOUNDS_NE)

// bundled argument constants
val ARG_WATCHED_MOVIE_UUID = "ARG_WATCHED_MOVIE_UUID"

// Tags for Fragments (Navigation purposes)
val TAG_DASHBOARD_FRAGMENT = "DASHBOARD_FRAGMENT"
val TAG_REGISTER_WATCHED_MOVIE_FRAGMENT = "REGISTER_WATCHED_MOVIE_FRAGMENT"
val TAG_PICK_MOVIE_FRAGMENT = "PICK_MOVIE_FRAGMENT"
val TAG_WATCHED_MOVIE_LIST_FRAGMENT = "WATCHED_MOVIE_LIST_FRAGMENT"
val TAG_MAP_FRAGMENT = "MAP_FRAGMENT"
val TAG_WATCHED_MOVIE_DETAILS_FRAGMENT = "WATCHED_MOVIE_DETAILS_FRAGMENT"

// Accelerometer: Constant for threshold and time between further Accelerometer shake detections
val TIME_MILLIS_BETWEEN_SHAKE_DETECTIONS: Long = 3000  // 3 segundos de timeout até permitir a funcionalidade de novo
val THRESHOLD_ACCELERATION_DETECT_SHAKE: Int = 11