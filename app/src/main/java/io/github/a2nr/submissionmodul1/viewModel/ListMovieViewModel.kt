package io.github.a2nr.submissionmodul1.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.a2nr.submissionmodul1.BuildConfig
import io.github.a2nr.submissionmodul1.repository.MovieData
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ListMovieViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        const val MOVIE: String = "movie"
        const val TV: String = "tv"
        private const val LINK_TRENDING: String = "https://api.themoviedb.org/3/trending"
        private const val API_KEY: String = BuildConfig.TMDB_API_KEY
        private const val LINK_IMAGE: String = "https://image.tmdb.org/t/p/original"
        fun getLinkTrendingMovie(media_type: String, time_window: String, language: String): Uri {
            return "$LINK_TRENDING/$media_type/$time_window?api_key=$API_KEY&language=$language".toUri()
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

    fun fetchMovieData(media_type: String, time_window: String, language: String) {
        vmCoroutine.launch {
            val tmpString = requestMovieData(media_type, time_window, language)
            if (!tmpString.isNullOrBlank()) {
                lateinit var jsonObject: JSONObject
                val md: List<MovieData>
                try {
                    jsonObject = JSONObject(tmpString)
                    val a = jsonObject.getJSONArray("results")
                    md = List(a.length()) { iii ->
                        val o = a.getJSONObject(iii)
                        Log.i("fetchMovieData", o.toString())
                        MovieData().apply {
                            title = arrayOf("title", "name").run {
                                this.forEach {
                                    if (o.has(it)) {
                                        return@run o.getString(it)
                                    }
                                }
                                "????"
                            }
                            backdrop_path = "backdrop_path".run {
                                if (o.has(this))
                                    o.getString(this)
                                else
                                    "????"
                            }
                            original_language = "original_language".run {
                                if (o.has(this))
                                    o.getString(this)
                                else
                                    "????"
                            }
                            this.media_type = "media_type".run {
                                if (o.has(this))
                                    o.getString(this)
                                else
                                    "????"
                            }
                            this.overview = "overview".run {
                                if (o.has(this))
                                    o.getString(this)
                                else
                                    "????"
                            }
                            release_date = arrayOf("release_date", "first_air_date").run {
                                this.forEach {
                                    if (o.has(it)) {
                                        return@run o.getString(it)
                                    }
                                }
                                "????"
                            }
                            this.vote_average = "vote_average".run {
                                if (o.has(this))
                                    o.getDouble(this).toFloat()
                                else
                                    0f
                            }
                            this.poster_path = "poster_path".run {
                                if (o.has(this))
                                    o.getString(this)
                                else
                                    "????"
                            }
                        }
                    }
                    vmMovieData.value = md
                } catch (e: Exception) {
                    Log.e("ViewModel", e.toString())
                }

            }
        }
    }

    private suspend fun requestMovieData(
        media_type: String,
        time_window: String,
        language: String
    )
            : String? {
        return withContext(Dispatchers.IO) {
            "".run {
                val req = Request.Builder()
                    .url(getLinkTrendingMovie(media_type, time_window, language).toString())
                    .build()
                try {
                    val response = client.newCall(req).execute()
                    val s = response.body?.string()
                    s?.let {
                        Log.i("ViewModel", it)
                    }
                    s
                } catch (e: Exception) {
                    Log.e("ViewModel", e.toString())
                    null
                }
            }
        }
    }
}