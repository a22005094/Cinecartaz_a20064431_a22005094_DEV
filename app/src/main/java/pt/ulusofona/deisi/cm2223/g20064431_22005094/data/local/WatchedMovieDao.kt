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
    fun insert(avaliacao: WatchedMovieRoom)

    @Query("SELECT * FROM avaliacoes")
    fun getAll(): List<WatchedMovieRoom>

    @Query("SELECT * FROM avaliacoes WHERE uuid = :uuid")
    fun getByUuid(uuid: String): WatchedMovieRoom?

    @Query("DELETE FROM avaliacoes")
    fun deleteAll()

}