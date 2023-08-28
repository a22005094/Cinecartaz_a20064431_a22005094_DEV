package pt.ulusofona.deisi.cm2223.g20064431_22005094

// Inspirado na demo de LOTR publicada em Github pelos Professores...
// De facto, um ficheiro "geral" para constantes é útil e simplifica um pouco mais ;)

const val OMDB_API_TOKEN = "2d3455ee"
const val OMDB_API_BASE_URL = "http://www.omdbapi.com/?apikey=$OMDB_API_TOKEN"

// Predefine já o formato esperado para a pesquisa de Filmes.
const val OMDB_API_URL_MOVIE_SEARCH = "$OMDB_API_BASE_URL&type=movie&s="

// ---------------------------------------------------------
// Apenas para alguns testes iniciais... :)
//const val LOTR_API_BASE_URL = "https://the-one-api.dev/v2"
//const val LOTR_API_TOKEN = "MjYzaqO6T8sLTg_3R75N"
// ---------------------------------------------------------
