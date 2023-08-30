package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.MovieSearchResultInfo

// * Disclaimer *
// Créditos: conteúdos disponibilizados pelos Professores, tanto a nível de slides teóricos como das fichas Práticas,
// que serviram de base e de guião para esta implementação.

// Define os comportamentos esperados na aplicação, ligados à gestão de dados.
// (devendo esta classe ser herdada para respetivas implementações de
// acessos remotos via API (ex. OMDB) e acessos locais (ex. DB Room).

abstract class Cinecartaz {

    // -------------------------------------------------------
    // * Originais @ Ficha OkHttp
    // abstract fun getCharacters(onFinished: (Result<List<LOTRCharacter>>) -> Unit)
    // abstract fun insertCharacters(characters: List<LOTRCharacter>, onFinished: () -> Unit)
    // abstract fun clearAllCharacters(onFinished: () -> Unit)
    // -------------------------------------------------------

    //  * NOTA IMPORTANTE:
    //  Em relação à pesquisa de Filmes na API do OMDB, há 2 formas possíveis:
    //  - 1) Pesquisar filmes por [Nome]   ->  devolve um cj. muito limitado de informações
    //                                        (title, year, imdbID...) - daqui apenas se vai retirar os [ImdbID]s.
    //  - 2) Pesquisar filmes por [ImdbID] ->  devolve os detalhes de um filme em específico.
    //                                        já traz muitas mais informações, e é a chamada "standard" a usar.

    // * API only
    abstract fun getMoviesByName(
        movieName: String, pageNumber: Int, onFinished: (Result<MovieSearchResultInfo>) -> Unit
    )

    // * API & BD
    abstract fun getMovieDetailsByImdbId(imdbId: String, onFinished: (Result<OMDBMovie>) -> Unit)

    // * BD only
    abstract fun insertMovie(movie: OMDBMovie, onFinished: () -> Unit)

    // * BD only
    abstract fun clearAllMovies(onFinished: () -> Unit)

    // ------------------------------------------------
    //  Em análise
    // ------------------------------------------------
    // abstract fun getMoviePosterFromUrl(url: String, onFinished: (Result<Bitmap>) -> Unit)

    // ------------------------------------------------
    //  NOTA
    // ------------------------------------------------
    // Para as operações (API/DB) a não ser permitidas, considerar colocar isto:
    // > throw Exception("Illegal operation")

}