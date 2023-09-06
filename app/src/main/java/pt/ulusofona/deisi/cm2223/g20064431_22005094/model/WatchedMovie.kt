package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

import java.util.UUID

// TODO assert que só se pode inserir com Datas passadas

data class WatchedMovie(
    val uuid: String = UUID.randomUUID().toString(),
    val movie: OMDBMovie,
    var theatre: Cinema,
    val review: Int,
    val date: Long,
    val comments: String,

    // A lista de fotos anexadas ao filme registado.
    // Está como [var] para permitir reatribuições mais tarde, conforme necessário.
    var photos: List<CustomImage>? = null,
    var calcDistance : Double = 0.0
)
