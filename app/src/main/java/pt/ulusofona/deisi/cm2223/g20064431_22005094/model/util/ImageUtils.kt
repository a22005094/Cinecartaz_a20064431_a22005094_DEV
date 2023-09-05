package pt.ulusofona.deisi.cm2223.g20064431_22005094.model.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

// O objetivo é muito semelhante ao Utils, mas aqui disponibiliza apenas
// métodos relacionados com tratamento de imagens.

object ImageUtils {

    // Créditos: inspiração @ChatGPT: "How can I convert an image uri to bitmap using android api 23?"
    fun convertUriToBitmap(uri: Uri, contentResolver: ContentResolver): Bitmap? {
        val bitmapResult: Bitmap

        try {
            contentResolver.openInputStream(uri).use {
                // Decode do ficheiro @ Uri para Bitmap
                val bitmapOriginal: Bitmap = BitmapFactory.decodeStream(it)

                // Comprimir Bitmap (otimizar tamanho, diminuir qualidade)
                // Credits: https://stackoverflow.com/questions/8417034/how-to-make-bitmap-compress-without-change-the-bitmap-size
                val outStream = ByteArrayOutputStream()
                bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, 50, outStream)
                bitmapResult = BitmapFactory.decodeStream(ByteArrayInputStream(outStream.toByteArray()))

                // Libertar recursos?
                bitmapOriginal.recycle()
                // outStream.close() - oops... o close() não faz nada!
            }

            return bitmapResult
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    // Créditos: inspiração @ChatGPT: "How can I convert an image uri to bytearray using android api 23?"
    fun convertUriToByteArray(uri: Uri, contentResolver: ContentResolver): ByteArray? {
        try {
            contentResolver.openInputStream(uri)?.use {
                // Decode do ficheiro @ Uri para Bitmap (facilita a conversão)
                val uriBitmap: Bitmap = BitmapFactory.decodeStream(it)
                return convertBitmapToByteArray(uriBitmap)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun convertBitmapToByteArray(imgBitmap: Bitmap?): ByteArray? {
        if (imgBitmap == null)
            return null

        // Para transformar Bitmaps em ByteArray - formato a usar p/ armazenar imagens em BD.
        val outStream = ByteArrayOutputStream()

        // TODO testar se esta "reconversão" de novo para PNG q100 funciona bem
        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)

        return outStream.toByteArray()
    }

    fun getByteArrayFromUrl(imageUrl: String, reuseClient: OkHttpClient?, onFinished: (Result<ByteArray?>) -> Unit) {
        // (Descarrega da Internet)
        // Caso se pretenda invocar a função múltiplas vezes, aceita reutilizar um cliente OkHttp, senão instancia novo.
        val client = reuseClient ?: OkHttpClient()

        // 1. Preparar o pedido OkHttp (usar ApiKey & Url respetivos à API OMDB)
        val request: Request = Request.Builder().url(imageUrl).build()

        // 2. Executar request
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
                    val respBytes: ByteArray? = response.body?.bytes()

                    if (respBytes != null) {
                        // Ok
                        onFinished(Result.success(respBytes))
                    } else {
                        // Erro - ByteArray na resposta devia ter sido carregado
                        onFinished(Result.failure(IOException("API error - bad return data")))
                    }
                }
            }
        })
    }

}