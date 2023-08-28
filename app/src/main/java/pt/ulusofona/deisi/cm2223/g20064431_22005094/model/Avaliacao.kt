package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

import android.graphics.Bitmap
import java.util.UUID

// TODO assert que só se pode inserir com Datas passadas!
// > Data Class?... Class?...

data class Avaliacao(
    val uuid: String = UUID.randomUUID().toString(),
    val filme: OMDBMovie, // TODO ?
    val cinema: Cinema,   // TODO ?
    val avaliacao: Int,
    val data: Long,
    val observacoes: String,
    val fotos: List<Bitmap>? = null  // TODO Image? Bitmap?
)
