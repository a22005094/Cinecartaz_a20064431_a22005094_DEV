package pt.ulusofona.deisi.cm2223.g20064431_22005094.data.remote

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import pt.ulusofona.deisi.cm2223.g20064431_22005094.OMDB_API_URL_MOVIE_SEARCH
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinecartaz
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.OMDBMovie
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
        // 1. Preparar o pedido OkHttp (usar ApiKey & Url respetivos à API LOTR)
        val requestUrl = "$OMDB_API_URL_MOVIE_SEARCH$movieName&page=$pageNumber"
        val request: Request = Request.Builder().url(requestUrl)
            //.addHeader("Authorization", "Bearer $apiKey")
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
                    // ex. HTTP 401, 403, 404, 500, ...
                    //onFinished(Result.failure(IOException("Erro de comunicação com a API (${response.code}): $response")))
                    onFinished(Result.failure(IOException("API communication error, please try again later")))
                } else {
                    val body = response.body?.string()
                    if (body != null) {
                        // * Obter o JSON da resposta p/ JsonObject
                        val jsonObject = JSONObject(body)

                        if (jsonObject.getBoolean("Response")) {

                            // * Carregar o nº resultados de pesquisa
                            val nrResults = jsonObject.getInt("totalResults")

                            // * Parse dos objetos p/ JsonArray
                            val jsonMoviesList = jsonObject["Search"] as JSONArray

                            // * Parse do JsonArray p/ objetos de Classes conhecidas
                            val moviesList = mutableListOf<OMDBMovie>()
                            for (i in 0 until jsonMoviesList.length()) {
                                val jsonMovie = jsonMoviesList[i] as JSONObject

                                // * Nota: Caso seja preciso lidar com Strings opcionais, usar "optString"
                                //         (devolve NULL caso não tenha valor preenchido)

                                moviesList.add(
                                    OMDBMovie(
                                        jsonMovie.getString("Title"),
                                        jsonMovie.getString("Year").toInt(),
                                        jsonMovie.getString("imdbID"),
                                        jsonMovie.getString("Poster")
                                    )
                                )
                            }

                            // Devolver a lista de Characters obtida via API, já como objetos de classes conhecidas
                            onFinished(
                                Result.success(
                                    MovieSearchResultInfo(nrResults, moviesList)
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


    // * Estes métodos não são utilizados via API (apenas terão uso localmente via DB Room).

    override fun insertMovie(movie: OMDBMovie, onFinished: () -> Unit) {
        Log.e("APP", "web service is not able to insert Movies!")
    }

    override fun clearAllMovies(onFinished: () -> Unit) {
        Log.e("APP", "web service is not able to clear all Movies!")
    }

}