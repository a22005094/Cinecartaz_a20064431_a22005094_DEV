package pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinema
import java.io.IOException

// Um simples Singleton que disponibiliza a lista de Cinemas a utilizar no projeto,
// permitindo também atualizá-la com recurso ao ficheiro 'cinemas.json' (na diretoria de raíz "Assets").

object CinemasManager {

    var listOfCinemas: MutableList<Cinema> = mutableListOf()
        private set

    // Créditos: https://stackoverflow.com/questions/26891943/adding-static-json-to-an-android-studio-project
    //  e pequenos ajustes c/ apoio ChatGPT para tornar os recursos mais resistentes a memory leaks (libertação devida de recursos)
    private fun loadCinemasFile(context: Context): String {
        var jsonString: String = ""

        try {
            val assetManager = context.assets
            assetManager.open("cinemas.json").reader().use { inputStreamReader ->
                val stringBuilder = StringBuilder()
                var charRead: Int
                val inputBuffer = CharArray(100)

                while (inputStreamReader.read(inputBuffer).also { charRead = it } > 0) {
                    val readString = String(inputBuffer, 0, charRead)
                    stringBuilder.append(readString)
                }

                jsonString = stringBuilder.toString()
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            jsonString = ""
        }

        return jsonString
    }

    fun updateListOfCinemas(context: Context) {
        val jsonCinemas = loadCinemasFile(context)

        // Tratar possivel situacao de erro ao ler o ficheiro (a lista de cinemas fica vazia)
        if (jsonCinemas.isEmpty()) {
            listOfCinemas = mutableListOf()
        } else {
            val jsonObj = JSONObject(jsonCinemas)
            val jsonListCinemas = jsonObj["cinemas"] as JSONArray

            // Cada objeto no Array é um cinema a carregar p/ Lista
            for (i in 0 until jsonListCinemas.length()) {
                val cinemaObj = jsonListCinemas[i] as JSONObject
                listOfCinemas.add(
                    Cinema(
                        cinemaObj.getInt("cinema_id"),
                        cinemaObj.getString("cinema_name"),
                        cinemaObj.getString("cinema_provider"),
                        cinemaObj.getDouble("latitude"),
                        cinemaObj.getDouble("longitude"),
                        cinemaObj.getString("address"),
                        cinemaObj.getString("postcode"),
                        cinemaObj.getString("county")
                    )
                )
            }
        }
    }

}