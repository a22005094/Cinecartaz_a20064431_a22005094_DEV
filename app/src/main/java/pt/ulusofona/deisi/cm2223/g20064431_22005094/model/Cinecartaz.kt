package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.MovieSearchResultInfo

// * Disclaimer *
// Créditos: conteúdos-base disponibilizados pelos Professores, tanto a nível de slides teóricos como das fichas Práticas,
// que serviram de base e de guião para esta implementação.

// Define os comportamentos esperados na aplicação, ligados à gestão de dados.
// (devendo esta classe ser herdada para respetivas implementações de
// acessos remotos via API (ex. OMDB) e acessos locais (ex. DB Room).

abstract class Cinecartaz {


    //  --------------------------------
    //  ------------- GETS -------------
    //  --------------------------------

    // * API only | Pesquisar filmes por [Nome]
    //  - em relação ao filme, devolve um cj. muito limitado de informações (title, year, imdbID...)
    //  - daqui apenas se pretende retirar os [ImdbID]s para carregar mais detalhes de seguida.
    //  - Retorna um objeto com o NºResultados e lista de ImdbIDs (String) dos Filmes encontrados
    abstract fun getOmdbMovieIdsByName(
        movieName: String, pageNumber: Int, onFinished: (Result<MovieSearchResultInfo>) -> Unit
    )

    // * API only | Pesquisar detalhes de um filme por [ImdbId]
    //  - A partir do ImdbID do filme, devolve os seus detalhes.
    //  - esta função é usada apenas para a vertente online de dados, para carregar da API OMDB os detalhes atualizados do filme.
    //  - a vertente local já está devidamente adaptada na classe CinecartazRoom (@ "getMovieByOMDBId()"),
    //    estando a ser já usada uma respetiva versão em conjunto com a função de repository de âmbito local "getWatchedMovie()".
    abstract fun getMovieDetailsByImdbId(imdbId: String, onFinished: (Result<OMDBMovie>) -> Unit)

    // * BD only - devolve a lista de filmes vistos (registados) na App,
    // e também informações associadas a este (dados do Filme, dados do Cinema, ...)
    abstract fun getWatchedMovies(onFinished: (Result<List<WatchedMovie>>) -> Unit)

    // * BD only - devolve a lista de IMDB_IDs de filmes vistos que contenham o(s) termo(s) de pesquisa
    // (a utilizar na Pesquisa de Filmes por Voz)
    abstract fun getWatchedMoviesImdbIdsWithTitleLike(name: String, onFinished: (Result<List<String>>) -> Unit)

    // * BD only - devolve a lista de filmes vistos que contenham o(s) termo(s) de pesquisa,
    // (TODO - a usar para a Filtragem de filmes)
    // e também informações associadas a este (dados do Filme, dados do Cinema, ...)
    // abstract fun getWatchedMoviesByName(onFinished: (Result<List<WatchedMovie>>) -> Unit)

    // get a movie from DB
    abstract fun getWatchedMovie(UuiD: String, onFinished: (Result<WatchedMovie>) -> Unit)

    abstract fun getAllCustomImagesByRefId(refId: String, onFinished: (Result<List<CustomImage>>) -> Unit)


    // ---------------------------------------
    // Para o menu Lista de Filmes - filtragem
    // ---------------------------------------
    // * BD only - devolve a lista de IMDB_IDs de filmes vistos que contenham o(s) termo(s) de pesquisa
    // (a utilizar na Pesquisa de Filmes por Voz)
    abstract fun getWatchedMoviesWithTitleLike(name: String, onFinished: (Result<List<WatchedMovie>>) -> Unit)


    // *** (REMOVED) *** - Não foi desenvolvido
    // *** (BD only) Estatísticas do Dashboard
    abstract fun getWorstRatedWatchedMovie(onFinished: (Result<WatchedMovie>) -> Unit)
    abstract fun getBestRatedWatchedMovie(onFinished: (Result<WatchedMovie>) -> Unit)


    //  -----------------------------------
    //  ------------- INSERTS -------------
    //  -----------------------------------
    // Nota: todos os Inserts previstos são só em âmbito local (BD Room)

    abstract fun insertWatchedMovie(watchedMovie: WatchedMovie, onFinished: () -> Unit)


    // NOTA:
    //  Estes métodos de inserção individual foram removidos por não serem aplicáveis.
    //  A função insertWatchedMovie() já trata de toda a inserção dos modelos individualmente:
    //   - Filme visto (WatchedMovie);
    //   - Filme do IMDB vindo da API (OmdbMovie)
    //   - Imagens (imagens anexadas pelo utilizador, poster do filme e fotos do cinema selecionado)

    // * abstract fun insertOMDBMovie(movie: OMDBMovie, onFinished: () -> Unit)
    // * abstract fun insertImage(image: CustomImage, onFinished: () -> Unit)

    //  -----------------------------------
    //  ------------- DELETES -------------
    //  -----------------------------------
    // Nota: todos os Deletes previstos são só em âmbito local (BD Room)
    // Operações não aplicáveis para o projeto.

    // * abstract fun clearAllMovies(onFinished: () -> Unit)

}