package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

import android.graphics.Bitmap
import java.util.UUID

// TODO assert que só se pode inserir com Datas passadas

data class WatchedMovie(
    val uuid: String = UUID.randomUUID().toString(),
    val movie: OMDBMovie,
    val theatre: Cinema,
    val review: Int,
    val date: Long,
    val comments: String,

    val photos: List<Bitmap>? = null  // TODO Image? Bitmap?
)
