package pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.Cinema
import pt.ulusofona.deisi.cm2223.g20064431_22005094.model.CustomImage
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    private suspend fun loadCinemaImages(cinemaId: Int, listOfImageUrls: List<String>?): Result<List<CustomImage>> {
        // A partir da lista de URLs das imagens de cada cinema (se existir), carregar a lista de imagens.

        // Função suspend para funcionar como bloqueante na thread que executá-la.
        // TODO terminar descrição.
        // Origem da documentação estudada:
        // - https://kotlinlang.org/docs/coroutines-and-channels.html#suspending-functions
        // - https://kotlinlang.org/docs/coroutines-and-channels.html#concurrency

        // TODO - Nota importante:
        //  Há imagens que não estão a carregar em APIs mais recentes do Android, por questão de políticas
        //  de comunicações em Cleartext (para endereços HTTP), onde é exigido comunicar via HTTPS...!
        //  Há umas possíveis soluções no Stackoverflow e que poderão merecer atenção.
        //  O erro que aparece nos links em cleartext (HTTP://) é o seguinte:
        //  "Failure(java.net.UnknownServiceException: CLEARTEXT communication to convida.pt not permitted by network security policy)"

        if (listOfImageUrls != null) {
            val listOfCinemaImages = mutableListOf<CustomImage>()

            CoroutineScope(Dispatchers.IO).launch {
                // NOTA: Deve ser executado numa thread separada.
                // Recorre tipo Deferred<T> e a corotina async{}, para que os resultados do conj. de chamadas p/ obter as imagens
                // sejam aguardados (quando todas as imagens estiverem obtidas & gravadas), antes de avançar com o resto da execução da função.
                // É algo semelhante ao que também já foi feito no Fragmento de Pesquisa por um Filme, onde era necessário esperar pelos resultados
                // de todas as chamadas à API, antes de poder avançar com o atualizar do adapter da lista de resultados obtidos.

                val deferredResults = mutableListOf<Deferred<Unit>>()

                for (imgUrl in listOfImageUrls) {
                    val deferred = async {
                        suspendCoroutine<Unit> { continuation ->
                            // -----------
                            ImageUtils.getByteArrayFromUrl(imgUrl, null) { result ->
                                if (result.isSuccess) {
                                    // Dados da imagem (Bytes)
                                    val imgBytes: ByteArray? = result.getOrNull()

                                    // Instanciar novo objeto CustomImage (se foram recebidos dados da Imagem)
                                    imgBytes?.let {
                                        listOfCinemaImages.add(
                                            CustomImage(
                                                refId = "$cinemaId", imageName = imgUrl, imageData = imgBytes
                                            )
                                        )
                                    }
                                }
                                continuation.resume(Unit)
                            }
                            // -----------
                        }
                    }
                    deferredResults.add(deferred)
                }

                // Esperar pelos resultados de todas as "tarefas deferrable".
                deferredResults.awaitAll()

                // Finalizadas todas as iterações do ForEach, devolver então a lista de Imagens resultante.
                // onFinished(Result.success(listOfCinemaImages))
            }.join()

            // Finalizadas todas as iterações do ForEach, devolver então a lista de Imagens resultante.
            return Result.success(listOfCinemaImages)

        } else {
            return Result.failure(IllegalArgumentException("No images were requested to load!"))
        }
    }

    // Credits pela documentação oficial estudada:
    // https://kotlinlang.org/docs/coroutines-and-channels.html#suspending-functions
    fun updateListOfCinemas(context: Context) {
        val jsonCinemas = loadCinemasFile(context)

        // Tratar possivel situacao de erro ao ler o ficheiro (a lista de cinemas fica vazia)
        if (jsonCinemas.isEmpty()) {
            listOfCinemas = mutableListOf()
        } else {
            val jsonObj = JSONObject(jsonCinemas)
            val jsonListCinemas = jsonObj["cinemas"] as JSONArray

            // Cada objeto no Array é um cinema a carregar p/ Lista
            if (jsonListCinemas.length() > 0) {
                // Conseguiu ler cinemas -- limpar dados atuais na lista para carregar os novos
                listOfCinemas = mutableListOf()

                for (i in 0 until jsonListCinemas.length()) {

                    val cinemaObj = jsonListCinemas[i] as JSONObject
                    val cinemaId = cinemaObj.getInt("cinema_id")

                    // Verificar se o cinema tem imagens (nem todos têm) - usar "opt" e não "get", pois é nullable
                    val listOfImagesJsonArray: JSONArray? = cinemaObj.optJSONArray("photos")
                    val listOfImageUrls = mutableListOf<String>()

                    if (listOfImagesJsonArray != null) {
                        for (j in 0 until listOfImagesJsonArray.length()) {
                            listOfImageUrls.add(listOfImagesJsonArray[j] as String)
                        }
                    }

                    listOfCinemas.add(
                        Cinema(
                            cinemaId,
                            cinemaObj.getString("cinema_name"),
                            cinemaObj.getString("cinema_provider"),
                            cinemaObj.getDouble("latitude"),
                            cinemaObj.getDouble("longitude"),
                            cinemaObj.getString("address"),
                            cinemaObj.getString("postcode"),
                            cinemaObj.getString("county"),
                            listOfImageUrls,
                            null  // neste ponto são carregados sem imagens (carregar as imagens depois se aplicável)
                        )
                    )

                }
            }
        }
    }

    suspend fun getCinemasImages() {
        // TODO - atualmente desativado (out of scope)
        // Com a lista de cinemas carregada, percorrer cada cinema da lista e
        // a partir dos URLs de imagens, carregar respetivas imagens para Memória

        listOfCinemas.forEach { cinema ->
            var listOfCinemaImages: List<CustomImage>? = null
            val result: Result<List<CustomImage>> = loadCinemaImages(cinema.id, cinema.imageUrls)

            if (result.isSuccess) {
                listOfCinemaImages = result.getOrNull()
            }

            cinema.photos = listOfCinemaImages
        }
    }

    fun getCinemaById(cinemaId: Int): Cinema? {
        // NOTA: Assume que existe SEMPRE um Cinema com o ID especificado.
        // (o que neste cenário vai ser garantido, pois esta função só vai ser chamada quando se lêem registos da BD...
        //  ... e os dados no JSON são estáticos e sempre estarão presentes.
        //  Naturalmente, mecanismos de fallback teriam de existir caso a natureza dos dados não fosse estática.)

        if (listOfCinemas.isEmpty()) {
            throw NullPointerException("List of cinemas should have already been initialized!")
        }

        return listOfCinemas.find { it.id == cinemaId }
    }

}