package pt.ulusofona.deisi.cm2223.g20064431_22005094.data

import android.content.Context
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinecartaz
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.MovieSearchResultInfo
import java.io.IOException


class CinecartazRepository(
    private val context: Context,
    // Objeto Room para gestão local de BD (Avaliações & OMDBMovies)
    private val local: Cinecartaz,
    // Objeto OkHttp para acessos API (OMDBMovies)
    private val remote: Cinecartaz
) : Cinecartaz() {


    override fun getOmdbMovieIdsByName(
        movieName: String, pageNumber: Int, onFinished: (Result<MovieSearchResultInfo>) -> Unit
    ) {
        // API only. Esta pesquisa é apenas de âmbito Online, e é utilizada no menu de pesquisa & seleção de um Filme.
        if (ConnectivityUtil.isDeviceOnline(context)) {
            remote.getOmdbMovieIdsByName(movieName, pageNumber) {
                onFinished(it)
            }
        } else {
            onFinished(Result.failure(IOException(context.getString(R.string.error_no_internet_connection))))
        }
    }

    override fun getMovieDetailsByImdbId(imdbId: String, onFinished: (Result<OMDBMovie>) -> Unit) {
        if (ConnectivityUtil.isDeviceOnline(context)) {
            remote.getMovieDetailsByImdbId(imdbId, onFinished)
        } else {
            // A vertente offline desta função está adaptada no CinecartazRoom,
            // já estando integrada na função para carregar os detalhes de um Filme registado (WatchedMovie).
            onFinished(Result.failure(IOException(context.getString(R.string.error_no_internet_connection))))
        }
    }

    override fun getWatchedMovies(onFinished: (Result<List<WatchedMovie>>) -> Unit) {
        // * BD only
        local.getWatchedMovies(onFinished)
    }

    override fun getWatchedMoviesImdbIdsWithTitleLike(name: String, onFinished: (Result<List<String>>) -> Unit) {
        // * BD only
        local.getWatchedMoviesImdbIdsWithTitleLike(name, onFinished)
    }

    override fun getWorstRatedWatchedMovie(onFinished: (Result<WatchedMovie>) -> Unit) {
        // * BD only
        local.getWorstRatedWatchedMovie(onFinished)
    }

    override fun getBestRatedWatchedMovie(onFinished: (Result<WatchedMovie>) -> Unit) {
        // * BD only
        local.getBestRatedWatchedMovie(onFinished)
    }

    override fun insertWatchedMovie(watchedMovie: WatchedMovie, onFinished: () -> Unit) {
        // * BD only
        local.insertWatchedMovie(watchedMovie, onFinished)
    }

    override fun getWatchedMovie(UuiD: String, onFinished: (Result<WatchedMovie>) -> Unit) {
        local.getWatchedMovie(UuiD, onFinished)
    }

    override fun getAllCustomImagesByRefId(
        refId: String,
        onFinished: (Result<List<CustomImage>>) -> Unit
    ) {
        local.getAllCustomImagesByRefId(refId, onFinished)
    }

    // * BD ONLY
    // Filtragem de resultados @ Lista de filmes
    override fun getWatchedMoviesWithTitleLike(name: String, onFinished: (Result<List<WatchedMovie>>) -> Unit) {
        local.getWatchedMoviesWithTitleLike(name, onFinished)
    }


    //
    // Removido: estas operações são asseguradas via "insertWatchedMovie()" (@ Room)
    //
    //  override fun insertOMDBMovie(movie: OMDBMovie, onFinished: () -> Unit) {
    //     // * BD only
    //     throw Exception("Illegal operation - done via insertWatchedMovie()")
    //  }
    //
    //  override fun insertImage(image: CustomImage, onFinished: () -> Unit) {
    //     // * BD only
    //     throw Exception("Illegal operation - done via insertWatchedMovie()")
    //  }
    //
    //  override fun clearAllMovies(onFinished: () -> Unit) {
    //     // * BD only
    //     throw Exception("Illegal operation - done via insertWatchedMovie()")
    //  }


    // --------------------------------------
    // * Gestão do Singleton
    // --------------------------------------
    // IMPORTANTE: executar [init] antes de [getInstance]!
    companion object {
        private var instance: CinecartazRepository? = null

        fun init(context: Context, local: Cinecartaz, remote: Cinecartaz) {
            if (instance == null) {
                instance = CinecartazRepository(context, local, remote)
            }
        }

        fun getInstance(): CinecartazRepository {
            if (instance == null) {
                // [init] já devia ter sido invocado :( ...
                throw IllegalStateException("singleton not initialized")
            }
            return instance as CinecartazRepository
        }
    }

}