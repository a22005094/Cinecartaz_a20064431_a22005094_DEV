package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

import android.app.Application
import android.util.Log
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.CinecartazRepository
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local.CinecartazDatabase
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local.CinecartazRoom
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.remote.CinecartazOkHttp

class CinecartazApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CinecartazRepository.init(this, initCinecartazRoom(), initCinecartazOkHttp())
        Log.i("APP", "Initialized Cinecartaz repository.")

        // Initialize location listener
        FusedLocation.start(this)
    }

    private fun initCinecartazRoom(): CinecartazRoom {
        return CinecartazRoom(
            CinecartazDatabase.getInstance(applicationContext).watchedMovieDao(),
            CinecartazDatabase.getInstance(applicationContext).omdbMoviesDao(),
            CinecartazDatabase.getInstance(applicationContext).imagesDao()
        )
    }

    private fun initCinecartazOkHttp(): CinecartazOkHttp {
        // Cliente "self-managed". Não requer parâmetros, pois já tem acesso aos dados necessários
        return CinecartazOkHttp()
    }

}