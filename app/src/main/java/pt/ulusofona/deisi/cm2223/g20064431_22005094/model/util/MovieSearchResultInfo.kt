package pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util

// Simples classe para armazenar um género de "tuplo"
// com o nº de resultados de pesquisa por um filme, e a lista de IDs dos 10 primeiros resultados.
// Isto decorre da limitação que existe na API OMDB.
// (Read: https://stackoverflow.com/questions/64293281/omdbapi-returns-only-10-movies)
//
// EXEMPLO PRÁTICO:
// Se foi feita uma pesquisa por "Batman", e a API encontrou 25 filmes,
// Aqui devolve a contagem (25), e uma lista com os IDs dos 10 primeiros resultados (ex. IMDB_1, IMDB_65 ...)

data class MovieSearchResultInfo(
    val nrResults: Int,
    val movieResults: List<String>
)