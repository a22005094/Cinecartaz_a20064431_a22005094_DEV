package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.remote

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import pt.ulusofona.deisi.cm2223.g20064431_22005094.OMDB_API_URL_MOVIE_DETAILS
import pt.ulusofona.deisi.cm2223.g20064431_22005094.OMDB_API_URL_MOVIE_TITLE_SEARCH
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinecartaz
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.WatchedMovie
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util.MovieSearchResultInfo
import java.io.IOException

// TODO substituir diretamente o BaseUrl e ApiKey pelos valores nas Constants.kt

// TODO replicar este comentário por tudo onde se aplique.
// * Disclaimer *
// Créditos: conteúdos disponibilizados pelos Professores, tanto a nível de slides teóricos como das fichas Práticas,
// que serviram de base e de guião para esta implementação.

// Neste nível de funcionalidade da App, esta classe apenas é utilizada para comunicações à API da OMDB,
//  sendo os acessos focados principalmente em GETs (para obter dados remotos).
// A persistência de dados é realizada apenas em âmbito local, com recurso ao ORM Room DB.

// ------
// v1 - recebe todos os parâmetros que necessita
// class CinecartazOkHttp(private val baseUrl: String, private val apiKey: String, private val client: OkHttpClient)
// ------

class CinecartazOkHttp : Cinecartaz() {
    // v2 - Cliente independente. Gere internamente a instância OkHttp e o acesso a parâmetros (@Constants.kt)
    private val client: OkHttpClient = OkHttpClient()

    override fun getMoviesByName(
        movieName: String, pageNumber: Int, onFinished: (Result<MovieSearchResultInfo>) -> Unit
    ) {
        // 1. Preparar o pedido OkHttp (usar ApiKey & Url respetivos à API OMDB)
        val requestUrl = "$OMDB_API_URL_MOVIE_TITLE_SEARCH$movieName&page=$pageNumber"
        val request: Request = Request.Builder()
            .url(requestUrl)
            .build()

        // 2. Executar o pedido à API, e processar resultado (onFailure vs onResponse)
        //    * NOTA: "onFailure" serve para situações onde nem se conseguiu chegar a fazer o pedido (ex. Timeout).
        //    Respostas c/ código de Erro (ex: HTTP 403, 500) tb pertencem ao OnResponse, devendo ser verificado!)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFinished(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                // Processar resposta recebida
                if (!response.isSuccessful) {
                    // ex. HTTP 401, 403, 500 ...
                    onFinished(Result.failure(IOException("API communication error (code ${response.code}), please try again later")))
                } else {
                    val body = response.body?.string()
                    if (body != null) {
                        // * Obter o JSON da resposta p/ JsonObject
                        val jsonObject = JSONObject(body)

                        if (jsonObject.getBoolean("Response")) {
                            // OK! Response = true

                            // * Carregar nº resultados pesquisa
                            val nrResults = jsonObject.getInt("totalResults")

                            // * Parse dos objetos p/ JsonArray
                            val listOfJsonMovies = jsonObject["Search"] as JSONArray
                            val listOfImdbIDs = mutableListOf<String>()

                            // * Transformar os objetos em lista de Strings só com os imdbId
                            for (i in 0 until listOfJsonMovies.length()) {
                                listOfImdbIDs.add(
                                    (listOfJsonMovies[i] as JSONObject).getString("imdbID")
                                )
                            }

                            // Devolver a lista de Characters obtida via API, já como objetos de classes conhecidas
                            onFinished(
                                Result.success(
                                    MovieSearchResultInfo(nrResults, listOfImdbIDs)
                                )
                            )
                        } else {
                            // Response != "True"
                            // - ou erro de API
                            // - ou não foram encontrados resultados

                            // Devolver OK, mas sem resultados
                            onFinished(Result.success(MovieSearchResultInfo(0, mutableListOf())))
                        }
                    }
                }
            }
        })
    }

    override fun getMovieDetailsByImdbId(imdbId: String, onFinished: (Result<OMDBMovie>) -> Unit) {
        if (imdbId.isNotEmpty()) {

            // 1. Preparar o pedido OkHttp (usar ApiKey & Url respetivos à API OMDB)
            val requestUrl = "$OMDB_API_URL_MOVIE_DETAILS$imdbId"
            val request: Request = Request.Builder()
                .url(requestUrl)
                .build()

            // 2. Executar o pedido à API, e processar resultado (onFailure vs onResponse)
            //    * NOTA: "onFailure" serve para situações onde nem se conseguiu chegar a fazer o pedido (ex. Timeout).
            //    Respostas c/ código de Erro (ex: HTTP 403, 500) tb pertencem ao OnResponse, devendo ser verificado!)

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFinished(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    // Processar resposta recebida
                    if (!response.isSuccessful) {
                        // ex. HTTP 401, 403, 500 ...
                        onFinished(Result.failure(IOException("API communication error (code ${response.code}), please try again later")))
                    } else {
                        val body = response.body?.string()
                        if (body != null) {
                            // * Obter o JSON da resposta p/ JsonObject
                            val movieJsonObject = JSONObject(body)

                            if (movieJsonObject.getBoolean("Response")) {
                                // Response = true
                                // OK! Filme encontrado

                                // TODO perceber se usamos String para todos os campos, ou String, Int? e Double?.
                                // Tem de se ter atenção pois nem todos os campos vêm sempre presentes
                                // (ex. existem filmes sem rating ("N/A"), portanto não se pode fazer GetDouble diretamente)

                                val movieYearStr: String = movieJsonObject.getString("Year")
                                val movieYear: Int? = movieYearStr.toIntOrNull()

                                val movieImdbRatingStr: String = movieJsonObject.getString("imdbRating")
                                val movieImdbRating: Double? = movieImdbRatingStr.toDoubleOrNull()

                                // * Carregar objeto OMDBMovie com os detalhes do Filme.
                                val omdbMovie = OMDBMovie(
                                    movieJsonObject.getString("Title"),
                                    movieYear,
                                    movieJsonObject.getString("imdbID"),
                                    movieJsonObject.getString("Genre"),
                                    movieImdbRating,
                                    movieJsonObject.getString("Director"),
                                    movieJsonObject.getString("Plot"),
                                    movieJsonObject.getString("Poster"),

                                    // O parâmetro do Bitmap fica para já a Null.
                                    // Só será preenchido no Adapter @PickMovieFragment ao selecionar (OnClick) um Filme
                                    null
                                )

                                // * Devolver dados do Filme.
                                onFinished(Result.success(omdbMovie))

                            } else {
                                // Response != "True" --> ERRO
                                //  - ou erro de API...
                                //  - ou não foram encontrados resultados... (e aqui devia!)
                                // A mensagem de erro vem no campo "Error".

                                val errorMsg = movieJsonObject.getString("Error")
                                onFinished(Result.failure(IOException("API communication error: [$errorMsg]")))
                            }
                        }
                    }
                }
            })
        } else {
            onFinished(Result.failure(IllegalArgumentException("A Movie ID is required!")))
        }
    }


    // * Estes métodos não são utilizados via API (são de uso apenas local, via DB Room).

    override fun getWatchedMovies(onFinished: (Result<List<WatchedMovie>>) -> Unit) {
        throw Exception("Illegal operation")
    }

    override fun getWatchedMovie(UuiD: String, onFinished: (Result<WatchedMovie>) -> Unit) {
        throw Exception("Illegal operation")
    }

    override fun insertWatchedMovie(watchedMovie: WatchedMovie, onFinished: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun insertImage(image: CustomImage, onFinished: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun insertOMDBMovie(movie: OMDBMovie, onFinished: () -> Unit) {
        // Log.e("APP", "web service is not able to insert Movies!")
        throw Exception("Illegal operation")
    }

    override fun clearAllMovies(onFinished: () -> Unit) {
        // Log.e("APP", "web service is not able to clear all Movies!")
        throw Exception("Illegal operation")
    }

}