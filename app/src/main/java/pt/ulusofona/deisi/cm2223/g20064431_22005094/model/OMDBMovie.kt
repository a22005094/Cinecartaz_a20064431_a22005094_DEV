package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

// Classe "pura", com dados de um Filme @ OMDB (sem dependências de Android, JSON, Parcelable... etc)

data class OMDBMovie(
    val title: String,         // ex.  "Avatar Spirits"
    val year: Int?,            // ex.  "2010", "N/A", ...
    val imdbId: String,        // ex.  "tt1900832"
    val genre: String,         // ex.  "Documentary, Biography, Sport"
    val ratingImdb: Double?,   // ex.  "8.2", "N/A", ...
    val director: String,      // ex.  "Christopher Nolan"
    val plotShort: String,     // ...
    //val releaseDate: Long,
    //val imdbVotes: Int,

    // * URL para o Poster do filme. NOTA: há filmes sem Poster! Para esses, o link será "N/A".
    val posterUrl: String,     // ex. "https://m.media-amazon.com/images/M/MV5BMzQ4MDMxNjExNl5BMl5BanBnXkFtZTgwOTYzODI5NTE@._V1_SX300.jpg"

    // Os dados da imagem. NULL se não tiver Poster. Preenchido ao selecionar o Filme associado @ PickMovieFragment.
    // Está como [var] para permitir reatribuições mais tarde, conforme necessário.
    var poster: CustomImage?

    // TODO falta o campo ReleaseDate (e confirmar o que mais poderá também faltar)
) {
    override fun toString(): String {
        return "$title ($year)"  // ex. Avatar (2009)
    }
}
