package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.MovieSearchResultInfo

// * Disclaimer *
// Créditos: conteúdos-base disponibilizados pelos Professores, tanto a nível de slides teóricos como das fichas Práticas,
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

    // TODO assert que todos os Gets devolvem Result<T>, não apenas o T diretamente
    // TODO assert que todas as operações não suportadas (@API/@BD) têm isto: throw Exception("Illegal operation")


    //  --------------------------------
    //  ------------- GETS -------------
    //  --------------------------------

    // * API only
    //  - 1) Pesquisar filmes por [Nome] -> devolve um cj. muito limitado de informações
    //                                      (title, year, imdbID...) - daqui apenas se vai retirar os [ImdbID]s.
    // Retorna um objeto com o NºResultados e lista de ImdbIDs (String) dos Filmes encontrados
    abstract fun getMoviesByName(
        movieName: String, pageNumber: Int, onFinished: (Result<MovieSearchResultInfo>) -> Unit
    )

    // * API & BD
    //  - 2) Pesquisar filmes por [ImdbID] -> devolve os detalhes de um filme em específico.
    //                                        já traz muitas mais informações, e é a chamada "standard" a usar.
    abstract fun getMovieDetailsByImdbId(imdbId: String, onFinished: (Result<OMDBMovie>) -> Unit)

    // * BD only - devolve a lista de filmes vistos (registados) na App,
    // e também informações associadas a este (dados do Filme, dados do Cinema, ...)
    abstract fun getWatchedMovies(onFinished: (Result<List<WatchedMovie>>) -> Unit)


    // get a movie from DB
    abstract fun getWatchedMovie(UuiD: String, onFinished: (Result<WatchedMovie>) -> Unit)


    //  -----------------------------------
    //  ------------- INSERTS -------------
    //  -----------------------------------
    // Nota: todos os Inserts são só em BD Room (local)

    abstract fun insertWatchedMovie(watchedMovie: WatchedMovie, onFinished: () -> Unit)

    abstract fun insertOMDBMovie(movie: OMDBMovie, onFinished: () -> Unit)

    // Para inserir Imagens - seja de WatchedMovies, OMDBMovies ou Cinemas.
    abstract fun insertImage(image: CustomImage, onFinished: () -> Unit)


    //  -----------------------------------
    //  ------------- DELETES -------------
    //  -----------------------------------
    // Nota: todos os Deletes são só em BD Room (local)

    // * BD only - TODO use me
    abstract fun clearAllMovies(onFinished: () -> Unit)

}