package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/*
// * NOTA: nas indicações das Fichas, a maioria dos métodos de continha a keyword "suspend".
// Isto lançou, no entanto, erros de compilação, que envolveram retirar a keyword posteriormente.
// Detalhes do erro e comentários sobre a resolução: https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
*/

@Dao
interface WatchedMovieDao {

    @Insert
    fun insert(watchedMovie: WatchedMovieRoom)

    @Query("SELECT * FROM watched_movies")
    fun getAll(): List<WatchedMovieRoom>

    @Query("SELECT * FROM watched_movies WHERE uuid = :uuid")
    fun getByUuid(uuid: String): WatchedMovieRoom?

    // Credits pela concatenação:
    // https://stackoverflow.com/questions/44184769/android-room-select-query-with-like
    @Query(
        "SELECT watched_movies.uuid FROM watched_movies " +
                "INNER JOIN omdb_movies ON watched_movies.movie_imdb_id = omdb_movies.imdb_id " +
                "WHERE omdb_movies.title LIKE '%' || :name || '%'"
    )
    fun getAllUuidsWithOmdbMovieTitleLike(name: String): List<String>


    //@Query("SELECT * FROM watched_movies INNER JOIN omdb_movies ON watched_movies.movie_imdb_id = omdb_movies.imdb_id ORDER BY watched_movies.review LIMIT 1")
    @Query("SELECT * FROM watched_movies ORDER BY watched_movies.review LIMIT 1")
    fun getWorstRated(): WatchedMovieRoom?


    //@Query("SELECT * FROM watched_movies INNER JOIN omdb_movies ON watched_movies.movie_imdb_id = omdb_movies.imdb_id ORDER BY watched_movies.review DESC LIMIT 1")
    @Query("SELECT * FROM watched_movies ORDER BY watched_movies.review DESC LIMIT 1")
    fun getBestRated(): WatchedMovieRoom?


    @Query("DELETE FROM watched_movies")
    fun deleteAll()

}