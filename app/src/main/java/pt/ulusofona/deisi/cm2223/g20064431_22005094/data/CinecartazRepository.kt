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

    // TODO - Tenho aqui uma dúvida sobre quando deve guardar os OMDBMovies localmente...
    //  - não me faz sentido guardar quando vamos pesquisar por um filme no Registarfilme que se vai guardar coisas localmente, não faz sentido...
    //  - o que me faz sentido é:
    //   i) quando se insere uma avaliação de um filme (guardar os detalhes dele tb localmente para depois se mostrar os detalhes se quisermos)
    //   ii) quando se abre os detalhes de um filme, aceder à API e guardar localmente...
    //  Portanto, o que acho é que o GetMoviesByName é algo feito 100% à API, sem armazenamento local.

    // TAREFAS PRETENDIDAS (até agora identificadas):
    // > pesquisar filme (API)
    // > getDetalhesFilme (API & BD)
    // > inserir avaliacao (BD)
    // > inserir filme (BD)
    // > getDetalhesAvaliacao (BD)
    // > getAllAvaliacoes (BD) (mapa...)
    //
    // Haverão outras... nomedamente buscar imagens de Filmes, etc...
    // Considerar se isso deve ser feito aqui...


    override fun getMoviesByName(
        movieName: String, pageNumber: Int, onFinished: (Result<MovieSearchResultInfo>) -> Unit
    ) {
        // (TODO REVER) Chamada 100% à API (?), não se prevê aqui guardar dados localmente pois é apenas uma "pesquisa" momentânea.

        if (ConnectivityUtil.isDeviceOnline(context)) {
            remote.getMoviesByName(movieName, pageNumber) {
                onFinished(it)
            }
        } else {
            // TODO testar output desta string
            onFinished(Result.failure(IOException(context.getString(R.string.error_no_internet_connection))))
        }
    }

    override fun getMovieDetailsByImdbId(imdbId: String, onFinished: (Result<OMDBMovie>) -> Unit) {
        if (ConnectivityUtil.isDeviceOnline(context)) {
            remote.getMovieDetailsByImdbId(imdbId, onFinished)
        } else {
            TODO("Not yet implemented")
        }
    }

    override fun getWatchedMovies(onFinished: (Result<List<WatchedMovie>>) -> Unit) {
        // * BD only
        local.getWatchedMovies(onFinished)
    }

    override fun insertWatchedMovie(watchedMovie: WatchedMovie, onFinished: () -> Unit) {
        // * BD only
        local.insertWatchedMovie(watchedMovie, onFinished)
    }

    override fun insertOMDBMovie(movie: OMDBMovie, onFinished: () -> Unit) {
        // * BD only
        // TODO...
    }

    override fun insertImage(image: CustomImage, onFinished: () -> Unit) {
        // * BD only
        TODO("Not yet implemented")
    }

    override fun clearAllMovies(onFinished: () -> Unit) {
        // * BD only
        TODO("Not yet implemented")
    }

    override fun getWatchedMovie(UuiD: String, onFinished: (Result<WatchedMovie>) -> Unit) {
        local.getWatchedMovie(UuiD, onFinished)
    }

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