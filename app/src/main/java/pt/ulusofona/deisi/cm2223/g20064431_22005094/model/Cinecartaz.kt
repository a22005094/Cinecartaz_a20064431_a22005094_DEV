package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

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

    abstract fun getMoviesByName(movieName: String, onFinished: (Result<List<OMDBMovie>>) -> Unit)

    // * Estes métodos não vão ter uso via API.
    abstract fun insertMovie(movie: OMDBMovie, onFinished: () -> Unit)
    abstract fun clearAllMovies(onFinished: () -> Unit)

    //Para as operações remote que não sejam feitas - considerar colocar isto:
    // >> throw Exception("Illegal operation")
}