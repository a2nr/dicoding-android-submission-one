package io.github.a2nr.submissionmodul1.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import io.github.a2nr.submissionmodul1.BuildConfig
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MovieDataRepository(private val movieDao: MovieDataAccess) {
    companion object {
        private const val LINK_TRENDING: String = "https://api.themoviedb.org/3/trending"
        private const val API_KEY: String = BuildConfig.TMDB_API_KEY
        const val LINK_IMAGE: String = "https://image.tmdb.org/t/p/original"
        fun getLinkTrendingMovie(media_type: String, time_window: String, language: String): Uri {
            return "$LINK_TRENDING/$media_type/$time_window?api_key=$API_KEY&language=$language".toUri()
        }

    }

    private val repoJob = Job()
    private val repoCoroutine = CoroutineScope(Dispatchers.Main + repoJob)
    private val client = OkHttpClient()

    val mutMovieData = MutableLiveData<List<MovieData>>()
    val mutIdExists = MutableLiveData<Boolean>()

    fun storeMovie(movieData: MovieData) {
        repoCoroutine.launch {
            withContext(Dispatchers.IO) {
                movieDao.insert(movieData)
            }
        }
    }

    fun removeMovie(movieData: MovieData) {
        repoCoroutine.launch {
            withContext(Dispatchers.IO) {
                movieDao.delete(movieData)
            }
        }
    }

    fun doCheckIsMovieExists(key : Int){
        repoCoroutine.launch {
            mutIdExists.value = withContext(Dispatchers.IO) {
                val i = movieDao.getIdfromId(key)
                Log.i("doCheckIsMovieExists","$i :: $key ==> ${i == key}")
                i == key
            }
        }
    }

    fun doGetMoviesStorage() {
        repoCoroutine.launch {
            mutMovieData.value = withContext(Dispatchers.IO) {
                movieDao.getAll()
            }
        }
    }

    fun doGetMovies(media_type: String, time_window: String, language: String) {
        repoCoroutine.launch {
            fetchMovies(media_type, time_window, language)
                ?.let {
                    mutMovieData.value = it
                }
        }
    }

    private suspend fun fetchMovies(media_type: String, time_window: String, language: String)
            : List<MovieData>? {

        /* pull data from server, wait until done */
        val tmpString = withContext(Dispatchers.IO) {
            "".run {
                /* Will be connect to server to pull data using API */
                val req = Request.Builder()
                    .url(getLinkTrendingMovie(media_type, time_window, language).toString())
                    .build()
                try {
                    val response = client.newCall(req).execute()
                    response.body?.string()
                } catch (e: Exception) {
                    Log.e("ViewModel", e.toString())
                    null
                }
            }
        }

        var md: List<MovieData>? = null

        /* decode .js into list variable  */
        if (!tmpString.isNullOrBlank()) {

            /* try to decode with fix format from server */
            try {

                /* get 'results' tag only */
                val a = JSONObject(tmpString).getJSONArray("results")

                /* store in List with bloating decoder code*/
                md = List(a.length()) { iii ->
                    val o = a.getJSONObject(iii)

                    /* print object in case you fucked */
                    Log.i("doGetMovies", o.toString())

                    /* Start annoying code */
                    MovieData().apply {
                        /* in .js the 'title' tag for movie category,
                         *            'name'  tag for tv category
                         * */
                        this.title = arrayOf("title", "name").run {
                            this.forEach {
                                if (o.has(it)) {
                                    return@run o.getString(it)
                                }
                            }
                            "????"
                        }
                        this.backdrop_path = "backdrop_path".run {
                            if (o.has(this))
                                o.getString(this)
                            else
                                "????"
                        }
                        this.original_language = "original_language".run {
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
                        /* in .js the 'release_date'    tag for movie category,
                         *            'first_air_date'  tag for tv category
                         * */
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
                        this.id = "id".run {
                            if (o.has(this))
                                o.getInt(this)
                            else
                                -1
                        }
                    }
                }
            }

            /* if catch invoked, maybe format of .js is changed */
            catch (e: Exception) {
                Log.e("ViewModel", e.toString())
            }
        }
        return md
    }

}