package io.github.a2nr.jetpakcourse.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.a2nr.jetpakcourse.BuildConfig
import io.github.a2nr.jetpakcourse.helper.GetMoviesParams
import io.github.a2nr.jetpakcourse.utils.EspressoIdlingResource
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MovieDataRepository(
    val movieDao: MovieDataAccess?,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        const val MOVIE: String = "movie"
        const val TV: String = "tv"
        private const val API_KEY: String = BuildConfig.TMDB_API_KEY
        fun getLinkTrendingMovie(
            media_type: String,
            language: String,
            page: Int = 1
        ): Uri =
            ("https://api.themoviedb.org/3/trending/" +
                    "$media_type/day?api_key=$API_KEY" +
                    "&language=$language" +
                    "&page=$page").toUri()

        fun getLinkSearchMovie(
            media_type: String,
            queryTittle: String,
            language: String,
            page: Int = 1
        ): Uri =
            ("https://api.themoviedb.org/3/search/" +
                    "$media_type?api_key=$API_KEY&language=$language&query=$queryTittle&page=$page").toUri()

        fun getLinkReleaseToday(date: String): Uri =
            ("https://api.themoviedb.org/3/discover/movie?api_key=$API_KEY" +
                    "&primary_release_date.gte=$date" +
                    "&primary_release_date.lte=$date").toUri()

        fun getLinkImage(width: String = "500", key: String): Uri {
            return "https://image.tmdb.org/t/p/w$width$key".toUri()
        }
    }

    private val repoJob = Job()
    private val repoCoroutine = CoroutineScope(Dispatchers.Main + repoJob)
    private val client = OkHttpClient()

    val mutMovieData = MutableLiveData<List<MovieData>>()
    val mutIsFavorite = MutableLiveData<Boolean>()

    fun pageListMovieBuilder(getMovieParams: GetMoviesParams): LiveData<PagedList<MovieData>> {
        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(true)
            .setPrefetchDistance(2)
            .build()
        return LivePagedListBuilder(
            MovieDataDataSource(this, getMovieParams).Factory(), config
        ).build()
    }

    fun storeMovie(movieData: MovieData) {
        launchInBackground(
            {
                movieDao?.insert(movieData)
            }, null
        )
    }

    fun removeMovie(movieData: MovieData) {
        launchInBackground(
            {
                movieDao?.delete(movieData)
            }, null
        )
    }


    fun doCheckIsFavorite(key: Int) {
        launchInBackground({
            movieDao?.let {
                it.getDataFromId(key).let {
                    Log.i("doCheckIsFavorite", "$this.isFavorite")
                    (it?.isFavorite ?: false)
                }
            }
        }, {
            mutIsFavorite.value = it
        })
    }

    fun doGetFavoriteMovies() {
        launchInBackground(
            {
                movieDao?.getFavorite()
            }, {
                mutMovieData.value = it
            }
        )
    }

    fun doGetMovies(mediaType: String, time_window: String = "day", language: String) {
        launchInBackground(
            {
                fetchData(getLinkTrendingMovie(mediaType, language))
            },
            {
                mutMovieData.value = it
            })
    }

    fun getReleaseMovie(date: String): List<MovieData>? = fetchData(getLinkReleaseToday(date))

    fun <T> launchInBackground(
        runIt: (suspend () -> T),
        returnIt: (suspend (T) -> Unit)?
    ) {

        EspressoIdlingResource.increment()
        repoCoroutine.launch {
            val ret = withContext(dispatcher) {
                runIt().also {
                    EspressoIdlingResource.decrement()
                }
            }
            returnIt?.let { it(ret) }
        }
    }

    fun getJSONData(uri: Uri): String? {
        return "".run {
            /* Will be connect to server to pull data using API */
            val req = Request.Builder()
                .url(uri.toString())
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

    /* pull data from server, wait until done */
    fun fetchData(uri: Uri): List<MovieData>? =
        getJSONData(uri)?.let { parse2MovieData(it) }

    fun fetchData(
        uri: Uri,
        dataInfoCallback: ((totalPage: Int, page: Int, data: List<MovieData>?) -> Unit)
    ) = getJSONData(uri)?.let {
        parse2MovieData(it) { totalPage, page, data ->
            dataInfoCallback(totalPage, page, data)
        }
    }

    fun parse2MovieData(
        JSON: String?,
        dataInfoCallback: ((totalPage: Int, page: Int, data: List<MovieData>?) -> Unit)? = null
    ): List<MovieData>? =
        JSON?.let { jsonString ->
            var page = 0
            var totalPage = 0
            try {
                /* decode .js into list variable  */
                JSONObject(jsonString)
                    .also {
                        if (it.has("page"))
                            page = it.getInt("page")
                        if (it.has("total_pages"))
                            totalPage = it.getInt("total_pages")
                    }.getJSONArray("results").let { jsonArray ->
                        /* store in List with bloating decoder code*/
                        List(jsonArray.length()) { iii ->
                            jsonArray.getJSONObject(iii).let { o ->

                                /* print object in case you fucked */
                                Log.i("doGetMovies", o.toString())

                                /* Start annoying code */
                                MovieData().apply {
                                    this.id = "id".run {
                                        if (o.has(this))
                                            o.getInt(this)
                                        else
                                            throw Exception(
                                                "json no \"id\" tag!!. " +
                                                        "this tag is mandatory, " +
                                                        "database id rely to this tag"
                                            )
                                    }
                                    /* in .js the 'title' tag for movie category,
                                     *            'name'  tag for tv category
                                     * */
                                    this.title = arrayOf("title", "name").run {
                                        this.forEach {
                                            if (o.has(it)) {
                                                return@run o.getString(it)
                                            }
                                        }
                                        "unknown"
                                    }
                                    this.backdropPath = "backdrop_path".run {
                                        if (o.has(this))
                                            o.getString(this)
                                        else
                                            "unknown"
                                    }
                                    this.originalLanguage = "original_language".run {
                                        if (o.has(this))
                                            o.getString(this)
                                        else
                                            "unknown"
                                    }
                                    this.mediaType = "media_type".run {
                                        if (o.has(this))
                                            o.getString(this)
                                        else
                                            "unknown"
                                    }
                                    this.overview = "overview".run {
                                        if (o.has(this))
                                            o.getString(this)
                                        else
                                            "unknown"
                                    }
                                    /* in .js the 'release_date'    tag for movie category,
                                     *            'first_air_date'  tag for tv category
                                     * */
                                    releaseDate =
                                        arrayOf("release_date", "first_air_date").run {
                                            this.forEach {
                                                if (o.has(it)) {
                                                    return@run o.getString(it)
                                                }
                                            }
                                            "unknown"
                                        }
                                    this.voteAverage = "vote_average".run {
                                        if (o.has(this))
                                            o.getDouble(this).toFloat()
                                        else
                                            0f
                                    }
                                    this.posterPath = "poster_path".run {
                                        if (o.has(this))
                                            o.getString(this)
                                        else
                                            "unknown"
                                    }
                                }
                            }
                        }
                    }.also { data ->
                        dataInfoCallback?.let { it(totalPage, page, data) }
                    }
            }
            /* if catch invoked, maybe format of .js is changed */
            catch (e: java.lang.Exception) {
                Log.e("ViewModel", e.toString())
                null
            }
        }
}

