package pt.ulusofona.deisi.cm2223.g20064431_22005094.data

import android.content.Context
import pt.ulusofona.deisi.cm2223.g20064431_22005094.R
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinecartaz
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
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
        TODO("Not yet implemented")
    }

    override fun insertMovie(movie: OMDBMovie, onFinished: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun clearAllMovies(onFinished: () -> Unit) {
        TODO("Not yet implemented")
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