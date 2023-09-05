package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "watched_movies")
data class WatchedMovieRoom(

    // [uuid] desta instancia (atribuido aleatoriamente quando é criada)
    @PrimaryKey val uuid: String,   // = UUID.randomUUID().toString(),

    // Movie ID - valor nominal.
    // Refere-se ao ImdbId de um [val movie: OMDBMovie] na classe original
    @ColumnInfo(name = "movie_imdb_id") val movieImdbId: String,

    // Cinema ID - valor inteiro.
    // Refere-se a um [val theatre: Cinema] na classe original
    @ColumnInfo(name = "cinema_id") val cinemaId: Int,

    @ColumnInfo(name = "review") val review: Int,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "comments") val comments: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = Date().time
)