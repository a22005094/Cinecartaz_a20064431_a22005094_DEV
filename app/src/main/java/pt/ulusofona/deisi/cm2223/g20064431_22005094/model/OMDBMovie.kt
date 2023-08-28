package pt.ulusofona.deisi.cm2223.g20064431_22005094.model

// Classe "pura", com dados de um Filme @ OMDB
// (sem dependências de JSON, Parcelable... etc.)

data class OMDBMovie(
    val title: String,     // ex.  "Avatar Spirits"
    val year: Int,         // ex.  2010  (NOTA: na API vem como String, "2004"...)
    val imdbId: String,    // ex.  "tt1900832"

    // * URL do poster do filme - para depois transferir a Imagem se necessário.
    // * NOTA IMPORTANTE! - existem filmes sem Poster! (o link respetivo é "N/A")
    val posterUrl: String  // ex. "https://m.media-amazon.com/images/M/MV5BMzQ4MDMxNjExNl5BMl5BanBnXkFtZTgwOTYzODI5NTE@._V1_SX300.jpg"
)
