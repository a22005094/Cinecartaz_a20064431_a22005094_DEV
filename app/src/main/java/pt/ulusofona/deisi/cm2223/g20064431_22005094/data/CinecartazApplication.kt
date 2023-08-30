package pt.ulusofona.deisi.cm2223.g20064431_22005094.data

import android.app.Application
import android.util.Log
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local.CinecartazDatabase
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local.CinecartazRoom
import pt.ulusofona.deisi.cm2223.g20064431_22005094.data.remote.CinecartazOkHttp

class CinecartazApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CinecartazRepository.init(this, initCinecartazRoom(), initCinecartazOkHttp())
        Log.i("APP", "Initialized Cinecartaz repository.")
    }

    private fun initCinecartazRoom(): CinecartazRoom {
        return CinecartazRoom(
            CinecartazDatabase.getInstance(applicationContext).watchedMovieDao(),
            CinecartazDatabase.getInstance(applicationContext).omdbMoviesDao()
        )
    }

    private fun initCinecartazOkHttp(): CinecartazOkHttp {
        // Cliente "self-managed". Não requer parâmetros, pois já tem acesso aos dados necessários
        return CinecartazOkHttp()
    }

}