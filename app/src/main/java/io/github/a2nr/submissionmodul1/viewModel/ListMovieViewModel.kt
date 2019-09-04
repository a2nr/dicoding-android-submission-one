package io.github.a2nr.submissionmodul1.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.github.a2nr.submissionmodul1.repository.MovieData
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class ListMovieViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        val LINK_TRANDING: String = "https://api.themoviedb.org/3/trending"
        val API_KEY: String = "a1eea6d03b1f0244d15177fec40aeb61"
        val LINK_IMAGE: String = "https://image.tmdb.org/t/p/original"
        fun getLinkTrandingMovie(media_type: String, time_window: String): Uri {
            return "$LINK_TRANDING/$media_type/$time_window?api_key=$API_KEY".toUri()
        }

        fun getLinkImage(key: String): Uri {
            return "$LINK_IMAGE$key".toUri()
        }
    }

    private val vmJob = Job()
    private val vmCoroutine = CoroutineScope(Dispatchers.Main + vmJob)
    private val client = OkHttpClient()
//    private val jsonClient = Moshi.Builder().build()

    private val vmMovieData = MutableLiveData<List<MovieData>>()
    val listMovieData: LiveData<List<MovieData>>
        get() = vmMovieData

    fun fetchMovieData(media_type: String, time_window: String) {
        val type = media_type
        val time = time_window
        vmCoroutine.launch {
            val tmpString = requestMovieData(type, time)
            if (!tmpString.isNullOrBlank()) {
                lateinit var jsonObject: JSONObject
                val md: List<MovieData>
                try {
                    jsonObject = JSONObject(tmpString)
                    val a = jsonObject.getJSONArray("results")
                    md = List<MovieData>(a.length()) {
                        val o = a.getJSONObject(it)
                        Log.i("fetchMovieData",o.toString())
                        MovieData().apply {
                            this.title = o.getString("title")
                            this.backdrop_path = o.getString("backdrop_path")
                            this.media_type = o.getString("media_type")
                            this.original_language = o.getString("original_language")
                            this.overview = o.getString("overview")
                            this.release_date = o.getString("release_date")
                            this.vote_average = o.getDouble("vote_average").toFloat()
                            this.poster_path = o.getString("poster_path")
                        }
                    }
                    vmMovieData.value = md
                } catch (e: Exception) {
                    Log.e("ViewModel", e.toString())
                }

            }
        }
    }

    private suspend fun requestMovieData(media_type: String, time_window: String)
            : String? {
        return withContext(Dispatchers.IO) {
            "".run {
                val req = Request.Builder()
                    .url(getLinkTrandingMovie(media_type, time_window).toString())
                    .build()
                try {
                    val response = client.newCall(req).execute()
                    val s = response.body?.string()
                    Log.i("ViewModel", s)
                    s
                } catch (e: Exception) {
                    Log.e("ViewModel", e.toString())
                    null
                }
            }
        }
    }
}