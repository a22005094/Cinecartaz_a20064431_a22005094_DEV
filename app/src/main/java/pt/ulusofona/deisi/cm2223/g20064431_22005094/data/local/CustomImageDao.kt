package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: CustomImageRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(images: List<CustomImageRoom>)

    @Query("SELECT * FROM images WHERE ref_id = :refId")
    fun getAllByRefId(refId: String): List<CustomImageRoom>?

    @Query("DELETE FROM images")
    fun deleteAll()

}