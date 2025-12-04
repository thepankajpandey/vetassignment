package com.example.vetclinic.data.remote

import com.example.vetclinic.data.model.ConfigDto
import com.example.vetclinic.data.model.PetDto
import com.example.vetclinic.data.model.PetsDto
import com.example.vetclinic.data.model.SettingsDto
import com.example.vetclinic.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpApiService(
    private val configUrl: String,
    private val petsUrl: String
) {

    suspend fun fetchConfig(): NetworkResult<ConfigDto> =
        fetchJson(configUrl) { json ->
            val root = JSONObject(json)
            val settings = root.getJSONObject("settings")

            ConfigDto(
                settings = SettingsDto(
                    isChatEnabled = settings.optBoolean("isChatEnabled", false),
                    isCallEnabled = settings.optBoolean("isCallEnabled", false),
                    workHours = settings.optString("workHours", "")
                )
            )
        }

    suspend fun fetchPets(): NetworkResult<PetsDto> =
        fetchJson(petsUrl) { json ->
            val root = JSONObject(json)
            val arr = root.getJSONArray("pets")

            val list = mutableListOf<PetDto>()

            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    PetDto(
                        image_url = o.optString("image_url"),
                        title = o.optString("title"),
                        content_url = o.optString("content_url"),
                        date_added = o.optString("date_added")
                    )
                )
            }

            PetsDto(list)
        }

    /**
     * Generic HTTP JSON fetcher (used for both config and pets).
     */
    private suspend fun <T> fetchJson(
        urlStr: String,
        mapper: (String) -> T
    ): NetworkResult<T> = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null

        try {
            val url = URL(urlStr)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
                instanceFollowRedirects = true
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream

            val reader = InputStreamReader(BufferedInputStream(stream))
            val sb = StringBuilder()
            reader.forEachLine { sb.append(it) }
            reader.close()

            if (code in 200..299) {
                val dto = mapper(sb.toString())
                NetworkResult.Success(dto, code)
            } else {
                NetworkResult.Error(code, "HTTP $code")
            }

        } catch (e: Exception) {
            NetworkResult.Error(null, "Network error: ${e.localizedMessage}")
        } finally {
            conn?.disconnect()
        }
    }
}