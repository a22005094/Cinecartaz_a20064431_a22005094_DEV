package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "avaliacoes")
data class WatchedMovieRoom(
    @PrimaryKey val uuid: String = UUID.randomUUID().toString(),

    // ---- TODO - ? ----
    // Movie ID
    // @ColumnInfo(name = "imdb_id") val imdbId: String,
    //    val filme: OMDBMovie
    // Cinema ID
    // ...
    //    val cinema: Cinema
    // Fotos
    // ...
    //    val fotos: List<Image>? = null
    // -----------

    @ColumnInfo(name = "avaliacao") val avaliacao: Int,
    @ColumnInfo(name = "data") val data: Long,
    @ColumnInfo(name = "observacoes") val observacoes: String

)
