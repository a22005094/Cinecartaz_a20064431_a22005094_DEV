package pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util

import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie

// Simples classe para armazenar um género de "tuplo"
// com o nº de resultados de uma pesquisa por um filme, e a lista dos 10 primeiros resultados.
// Isto surge por limitação da API OMDB.
// (Read: https://stackoverflow.com/questions/64293281/omdbapi-returns-only-10-movies)
//
// EXEMPLO PRÁTICO:
// Se foi feita uma pesquisa por "Batman", e a API encontrou 25 filmes,
// Aqui devolve a contagem (25), e uma lista com os 10 primeiros resultados de pesquisa.

data class MovieSearchResultInfo(
    val nrResults: Int,
    val movieResults: List<OMDBMovie>
)