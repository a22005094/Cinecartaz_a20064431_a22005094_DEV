package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "omdb_movies")
data class OMDBMovieRoom(
    @PrimaryKey @ColumnInfo(name = "imdb_id") val imdbId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "year") val year: Int?,
    @ColumnInfo(name = "genre") val genre: String,
    @ColumnInfo(name = "rating_imdb") val ratingImdb: Double?,
    @ColumnInfo(name = "director") val director: String,
    @ColumnInfo(name = "plot_short") val plotShort: String,
    @ColumnInfo(name = "poster_url") val posterUrl: String

    // val plotLong: String,  // TODO use?
)