package com.binariamente.conectatea.utils

import android.util.Log
import com.binariamente.conectatea.models.Pictograma
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object ArasaacApi {
    suspend fun buscarPictogramas(palavra: String): List<Pictograma> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<Pictograma>()
            try {
                // Endpoint oficial do Arasaac em português
                val url = URL("https://api.arasaac.org/api/pictograms/pt/search/$palavra")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(response)

                    // Pega no máximo os 10 primeiros resultados para não pesar a tela
                    val maxItems = if (jsonArray.length() > 10) 10 else jsonArray.length()

                    for (i in 0 until maxItems) {
                        val item = jsonArray.getJSONObject(i)
                        val id = item.getInt("_id")

                        // Monta a URL oficial da imagem baseada no ID retornado
                        val urlImagem = "https://static.arasaac.org/pictograms/$id/${id}_300.png"

                        lista.add(
                            Pictograma(
                                nome = palavra.replaceFirstChar { it.uppercase() },
                                imagem = urlImagem,
                                tipoImagem = "padrao"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ArasaacApi", "Erro ao buscar na API: ${e.message}")
            }
            lista
        }
    }
}