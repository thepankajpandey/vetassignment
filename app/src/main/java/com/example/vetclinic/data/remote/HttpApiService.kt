package com.example.vetclinic.data.remote

import android.content.Context
import androidx.annotation.RawRes
import com.example.vetclinic.R
import com.example.vetclinic.data.model.ConfigDto
import com.example.vetclinic.data.model.PetDto
import com.example.vetclinic.data.model.PetsDto
import com.example.vetclinic.data.model.SettingsDto
import com.example.vetclinic.util.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class HttpApiService @Inject constructor(
    @ApplicationContext private val context: Context,

    // Optional remote URLs (if null → load from res/raw)
    private val configUrl: String? = null,
    private val petsUrl: String? = null
)  {

    suspend fun fetchConfig(): NetworkResult<ConfigDto> {
        return if (configUrl == null) {
            loadLocalConfig(R.raw.config)
        } else {
            val remote = loadRemoteConfig(configUrl)

            if (remote is NetworkResult.Error) {
                loadLocalConfig(R.raw.config)
            } else {
                remote
            }
        }
    }

    suspend fun fetchPets(): NetworkResult<PetsDto> {
        return if (petsUrl == null) {
            loadLocalPets(R.raw.pets)
        } else {
            val remote = loadRemotePets(petsUrl)

            if (remote is NetworkResult.Error) {
                loadLocalPets(R.raw.pets)
            } else {
                remote
            }
        }
    }

    private suspend fun loadLocalConfig(
        @RawRes resId: Int
    ): NetworkResult<ConfigDto> = withContext(Dispatchers.IO) {
        try {
            val json = context.resources.openRawResource(resId)
                .bufferedReader()
                .use { it.readText() }

            val root = JSONObject(json)
            val s = root.getJSONObject("settings")

            val dto = ConfigDto(
                settings = SettingsDto(
                    isChatEnabled = s.optBoolean("isChatEnabled"),
                    isCallEnabled = s.optBoolean("isCallEnabled"),
                    workHours = s.optString("workHours")
                )
            )

            NetworkResult.Success(dto, 200)

        } catch (e: Exception) {
            NetworkResult.Error(null, "Local JSON error: ${e.localizedMessage}")
        }
    }

    private suspend fun loadLocalPets(
        @RawRes resId: Int
    ): NetworkResult<PetsDto> = withContext(Dispatchers.IO) {
        try {
            val json = context.resources.openRawResource(resId)
                .bufferedReader()
                .use { it.readText() }

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

            NetworkResult.Success(PetsDto(list), 200)

        } catch (e: Exception) {
            NetworkResult.Error(null, "Local JSON error: ${e.localizedMessage}")
        }
    }

    private suspend fun loadRemoteConfig(urlStr: String):
            NetworkResult<ConfigDto> =
        fetchJson(urlStr) { json ->
            val root = JSONObject(json)
            val s = root.getJSONObject("settings")

            ConfigDto(
                settings = SettingsDto(
                    isChatEnabled = s.optBoolean("isChatEnabled"),
                    isCallEnabled = s.optBoolean("isCallEnabled"),
                    workHours = s.optString("workHours")
                )
            )
        }

    private suspend fun loadRemotePets(urlStr: String):
            NetworkResult<PetsDto> =
        fetchJson(urlStr) { json ->
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