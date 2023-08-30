package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import androidx.room.*

/*
    * NOTA *:
    - Nas indicações das Fichas, a maioria dos métodos do Dao continha a keyword "suspend".
    - Isto lançou, no entanto, erros de compilação, que envolveram retirar a keyword posteriormente.
    - Detalhes do erro e comentários sobre isto: https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
*/

@Dao
interface OMDBMovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movie: OMDBMovieRoom)

    // @Insert(onConflict = OnConflictStrategy.REPLACE)
    // fun insertAll(movies: List<OMDBMovieRoom>)

    @Query("SELECT * FROM omdb_movies ORDER BY title ASC")
    fun getAll(): List<OMDBMovieRoom>

    @Query("SELECT * FROM omdb_movies WHERE imdb_id = :imdbId")
    fun getByImdbId(imdbId: String): OMDBMovieRoom?

    @Query("DELETE FROM omdb_movies")
    fun deleteAll()

}