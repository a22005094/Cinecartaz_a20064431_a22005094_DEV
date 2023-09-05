package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "images")
data class CustomImageRoom(

    // [uuid] desta instancia (automaticamente atribuido, valor aleatorio)
    @PrimaryKey val uuid: String,    // = UUID.randomUUID().toString(),

    // ID do elemento ao qual a imagem pertence.
    // As Imagens podem pertencer a:
    //  - WatchedMovies
    //  - OMDBMovies
    //  - Cinemas
    @ColumnInfo(name = "ref_id") val refId: String,

    // O nome da imagem, para distinguir entre diferentes imagens para a mesma RefId.
    @ColumnInfo(name = "image_name") val imageName: String,

    // Dados da imagem (armazenado como Base64 String)
    @ColumnInfo(name = "image_data") val imageData: String,

    // Timestamp automático
    @ColumnInfo(name = "timestamp") val timestamp: Long = Date().time
)