package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.local

import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinecartaz
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie

class CinecartazRoom(private val avaliacoesDao: AvaliacaoDao, private val omdbMoviesDao: OMDBMovieDao) : Cinecartaz() {

    // TODO
    // Esta vai ser UMA ÚNICA classe para aglomerar todas as transações locais de dados,
    // seja para dados de Avaliações, seja para dados de OMDBMovies.
    // A utilizar como parâmetro "local" no CinecartazRepository.

    override fun getMoviesByName(movieName: String, onFinished: (Result<List<OMDBMovie>>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun insertMovie(movie: OMDBMovie, onFinished: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun clearAllMovies(onFinished: () -> Unit) {
        TODO("Not yet implemented")
    }

}