package io.github.a2nr.jetpakcourse.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.a2nr.jetpakcourse.BuildConfig
import io.github.a2nr.jetpakcourse.utils.EspressoIdlingResource
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*

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
            time_window: String,
            language: String,
            page: Int = 1
        ): Uri =
            ("https://api.themoviedb.org/3/trending/" +
                    "$media_type/$time_window?api_key=$API_KEY" +
                    "&language=$language" +
                    "&page=$page").toUri()

        fun getLinkSearchMovie(
            media_type: String,
            queryTittle: String,
            language: String,
            page: Int = 1
        ): Uri =
            ("https://api.themoviedb.org/3/search/" +
                    "$media_type?api_key=$API_KEY" +
                    "&language=$language" +
                    "&query=$queryTittle" +
                    "&page=$page").toUri()

        fun getLinkReleaseToday(date: String, page: Int = 1): Uri =
            ("https://api.themoviedb.org/3/discover/movie?api_key=$API_KEY" +
                    "&primary_release_date.gte=$date" +
                    "&primary_release_date.lte=$date" +
                    "&page=$page").toUri()

        fun getLinkImage(width: String = "500", key: String): Uri {
            return "https://image.tmdb.org/t/p/w$width$key".toUri()
        }
    }

    private val repoJob = Job()
    val repoCoroutine = CoroutineScope(Dispatchers.Main + repoJob)
    private val client = OkHttpClient()

    val mutIdExists = MutableLiveData<Boolean>()

    fun storeFavorite(movieData: MovieData) {
        EspressoIdlingResource.increment()
        movieDao?.let {
            repoCoroutine.launch {
                withContext(dispatcher) {
                    it.insert(movieData)
                    EspressoIdlingResource.decrement()
                }
            }
        }
    }

    fun removeFavorite(movieData: MovieData) {
        EspressoIdlingResource.increment()
        movieDao?.let {
            repoCoroutine.launch {
                withContext(dispatcher) {
                    it.delete(movieData.id)
                    EspressoIdlingResource.decrement()
                }
            }
        }
    }


    fun doCheckIsMovieExists(key: Int) {
        EspressoIdlingResource.increment()
        repoCoroutine.launch {
            mutIdExists.value = withContext(dispatcher) {
                val i = movieDao?.getMovieFromId(key)
                Log.i("doCheckIsMovieExists", "$i")
                (i?.id == key).also {
                    EspressoIdlingResource.decrement()
                }
            }
        }
    }

    fun doGetFavoriteMovie() {
        movieDao?.let {
            buildPageListOffline(it.getDataSource())
        }
    }

    fun doGetMovies(mediaType: String, time_window: String, language: String) {
        link = { getLinkTrendingMovie(mediaType, time_window, language, it) }
        buildPageList()
    }

    fun doSearchMovies(mediaType: String, queryTittle: String, language: String) {
        link = { getLinkSearchMovie(mediaType, queryTittle, language, it) }
        buildPageList()
    }

    fun doGetReleaseMovie(date: String) {
        link = { getLinkReleaseToday(date, it) }
        buildPageList()
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

    fun parse2MovieData(JSON: String?): List<MovieData>? = JSON?.let { jsonString ->
        /* decode .js into list variable  */
        JSONObject(jsonString).getJSONArray("results").let { jsonArray ->
            try {
                /* store in List with bloating decoder code*/
                List(jsonArray.length()) { iii ->
                    jsonArray.getJSONObject(iii).let { o ->

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
                            releaseDate = arrayOf("release_date", "first_air_date").run {
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
                            this.id = "id".run {
                                if (o.has(this))
                                    o.getInt(this)
                                else
                                    -1
                            }

                            this.timeInsert = Calendar.getInstance().time.time
                        }
                    }
                }
            }
            /* if catch invoked, maybe format of .js is changed */
            catch (e: java.lang.Exception) {
                Log.e("ViewModel", e.toString())
                null
            }
        }
    }


    /* pull data from server, wait until done */
    suspend fun fetchData(uri: Uri): List<MovieData>? = withContext(dispatcher) {
        getJSONData(uri)?.let {
            parse2MovieData(it)
        }

    }

    private suspend fun fetchDataUtil(
        uri: Uri, doneCallback: ((dataInfo: DataInfo, data: List<MovieData>) -> Unit)
    ) = withContext(dispatcher) {
        val jsonData = getJSONData(uri)
        val totalPage = jsonData?.let { JSONObject(it).getInt("total_pages") } ?: 0
        val page = jsonData?.let { JSONObject(it).getInt("page") } ?: 0
        val data = jsonData?.let { parse2MovieData(it) } ?: emptyList()
        doneCallback(DataInfo(totalPage, page), data)
    }


    private class DataInfo(val totalPage: Int, val page: Int)

    private var link: ((page: Int) -> Uri)? = null

    val pageListData = MutableLiveData<LiveData<PagedList<MovieData>>>()

    private fun buildPageListOffline(dataSource: DataSource.Factory<Int, MovieData>) =
        repoCoroutine.launch {
            pageListData.value = withContext(dispatcher) {
                LivePagedListBuilder(
                    dataSource, PagedList.Config.Builder()
                        .setPageSize(20)
                        .setEnablePlaceholders(true)
                        .setPrefetchDistance(2)
                        .build()
                )
                    .build()
            }
        }

    private fun buildPageList() = repoCoroutine.launch {
        pageListData.value = withContext(dispatcher) {
            movieDao?.delete()
            LivePagedListBuilder(
                movieDao!!.getDataSource(), PagedList.Config.Builder()
                    .setPageSize(20)
                    .setEnablePlaceholders(true)
                    .setPrefetchDistance(2)
                    .build()
            )
                .setBoundaryCallback(boundary)
                .build()
        }
    }

    private val boundary = object : PagedList.BoundaryCallback<MovieData>() {

        private var onLoading = false
        private var curentInfo = DataInfo(0, 0)

        private fun getAndSaveData(
            page: Int = 1,
            dataInfo: ((pageInfo: DataInfo) -> Unit)? = null
        ) = link?.let { lll ->
            var info = DataInfo(0, 0)
            if (!onLoading) {
                onLoading = true
                EspressoIdlingResource.increment()
                repoCoroutine.launch {
                    fetchDataUtil(lll(page)) { i, d ->
                        info = i
                        movieDao?.insert(d)
                    }.also {
                        EspressoIdlingResource.decrement()
                        onLoading = false
                        dataInfo?.let {
                            dataInfo(info)
                        }
                    }
                }
            }
        }

        override fun onZeroItemsLoaded() {
            super.onZeroItemsLoaded()
            getAndSaveData {
                curentInfo = it
            }
        }

        override fun onItemAtEndLoaded(itemAtEnd: MovieData) {
            super.onItemAtEndLoaded(itemAtEnd)
            if (curentInfo.page < curentInfo.totalPage)
                getAndSaveData(curentInfo.page + 1)
                {
                    curentInfo = it
                }
        }

//        override fun onItemAtFrontLoaded(itemAtFront: MovieData) {
//            super.onItemAtFrontLoaded(itemAtFront)
//            if (curentInfo.page > 1)
//                getAndSaveData(curentInfo.page - 1) {
//                    curentInfo = it
//                }
//        }

    }
}