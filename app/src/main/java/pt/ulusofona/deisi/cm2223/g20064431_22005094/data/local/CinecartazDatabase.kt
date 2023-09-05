package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// (Aqui declara-se um array c/ todas as entidades (Tabelas) a usar no modelo de dados da DB)
@Database(entities = [WatchedMovieRoom::class, OMDBMovieRoom::class, CustomImageRoom::class], version = 2)
//@TypeConverters(ExampleConverters::class)
abstract class CinecartazDatabase : RoomDatabase() {

    abstract fun watchedMovieDao(): WatchedMovieDao
    abstract fun omdbMoviesDao(): OMDBMovieDao
    abstract fun imagesDao(): CustomImageDao

    companion object {
        private var instance: CinecartazDatabase? = null

        fun getInstance(applicationContext: Context): CinecartazDatabase {
            synchronized(this) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        applicationContext, CinecartazDatabase::class.java, "cinecartaz_db"
                    ).fallbackToDestructiveMigration().build()
                }
                return instance as CinecartazDatabase
            }
        }
    }

}