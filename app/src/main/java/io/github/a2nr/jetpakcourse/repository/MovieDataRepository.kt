package io.github.a2nr.jetpakcourse.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    val dao: MovieDataAccess,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        const val MOVIE: String = "movie"
        const val TV: String = "tv"
        private const val API_KEY: String = BuildConfig.TMDB_API_KEY
        fun getLinkMovie(mediaType: String, key: Long, language: String): Uri =
            ("https://api.themoviedb.org/3/$mediaType/$key?api_key=$API_KEY" +
                    "&language=$language").toUri()

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
    private val client = OkHttpClient()
    private var link: ((page: Int) -> Uri)? = null

    val repoCoroutine = CoroutineScope(Dispatchers.Main + repoJob)
    val pageListData = MutableLiveData<LiveData<PagedList<MovieData>>>()
    val mutIdExists = MutableLiveData<Boolean>()

    private class DataInfo(val totalPage: Int, val page: Int)

    fun storeFavorite(movieData: MovieData) {
        EspressoIdlingResource.increment()
        repoCoroutine.launch {
            withContext(dispatcher) {
                dao.insertFavorite(FavoriteMovieData().apply {
                    this.idFavorite = movieData.id
                    this.mediaType = movieData.mediaType
                    this.originalLanguage = movieData.originalLanguage
                })
                EspressoIdlingResource.decrement()
            }
        }
    }

    fun removeFavorite(movieData: MovieData) {
        EspressoIdlingResource.increment()
        repoCoroutine.launch {
            withContext(dispatcher) {
                dao.deleteFavorite(movieData.id)
                EspressoIdlingResource.decrement()
            }
        }
    }


    fun doCheckIsMovieExists(key: Int) {
        EspressoIdlingResource.increment()
        repoCoroutine.launch {
            mutIdExists.value = withContext(dispatcher) {
                dao.getFavorite(key)?.let { i ->
                    Log.i("doCheckIsMovieExists", "$i")
                    (i.idFavorite == key).also {
                        EspressoIdlingResource.decrement()
                    }
                } ?: false
            }
        }
    }

    fun doGetFavoriteMovie() {
        link = { "//favorite".toUri() }

        EspressoIdlingResource.increment()
        repoCoroutine.launch {
            pageListData.value = withContext(dispatcher) {
                buildPageList(listSingleDataBuilder).also {
                    EspressoIdlingResource.decrement()
                }
            }
        }
    }

    fun doGetMovies(mediaType: String, time_window: String, language: String) {
        link = { getLinkTrendingMovie(mediaType, time_window, language, it) }
        EspressoIdlingResource.increment()
        repoCoroutine.launch {
            pageListData.value = withContext(dispatcher) {
                buildPageList(listDataBuilder).also {
                    EspressoIdlingResource.decrement()
                }
            }
        }
    }

    fun doSearchMovies(mediaType: String, queryTittle: String, language: String) {
        link = { getLinkSearchMovie(mediaType, queryTittle, language, it) }
        EspressoIdlingResource.increment()
        repoCoroutine.launch {
            pageListData.value = withContext(dispatcher) {
                buildPageList(listDataBuilder).also {
                    EspressoIdlingResource.decrement()
                }
            }
        }
    }

    fun doGetReleaseMovie(date: String) {
        link = { getLinkReleaseToday(date, it) }
        EspressoIdlingResource.increment()
        repoCoroutine.launch {
            pageListData.value = withContext(dispatcher) {
                buildPageList(listDataBuilder).also {
                    EspressoIdlingResource.decrement()
                }
            }
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

    private fun parse2MovieData(jObject: JSONObject): MovieData = jObject.let { o ->
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

    private fun checkData(jObjet: JSONObject): JSONObject {
        if (jObjet.has("media_type"))
            return jObjet
        else {
            val id = "id".run {
                if (jObjet.has(this))
                    jObjet.getInt(this)
                else
                    -1
            }
            val originalLanguage = "original_language".run {
                if (jObjet.has(this))
                    jObjet.getString(this)
                else
                    "unknown"
            }
            return let {
                try {
                    getJSONData(
                        getLinkMovie(
                            "movie",
                            id.toLong(),
                            originalLanguage
                        )
                    )?.let {
                        JSONObject(it)
                    }.also {
                        if (it?.has("status_code") == true)
                            throw Exception("tv")
                    }
                } catch (e: Exception) {
                    getJSONData(
                        getLinkMovie(
                            "tv",
                            id.toLong(),
                            originalLanguage
                        )
                    )?.let {
                        JSONObject(it)
                    }
                }?:JSONObject()
            }
        }
    }

    fun parse2ListMovieData(JSON: String?): List<MovieData>? = JSON?.let { jsonString ->
        /* decode .js into list variable  */
        JSONObject(jsonString).getJSONArray("results").let { jsonArray ->
            try {
                /* store in List with bloating decoder code*/
                List(jsonArray.length()) { iii ->
                    jsonArray.getJSONObject(iii).let { o ->
                        /* print object in case you fucked */
                        Log.i("doGetMovies", o.toString())
                        parse2MovieData(checkData(o))
                    }

                }
            }
            /* if catch invoked, maybe format of .js is changed */
            catch (e: Exception) {
                Log.e("ViewModel", e.toString())
                emptyList()
            }
        }
    }


    /* pull data from server, wait until done */
    suspend fun fetchData(uri: Uri): List<MovieData>? = withContext(dispatcher) {
        getJSONData(uri)?.let {
            parse2ListMovieData(it)
        }

    }

    private
    val listDataBuilder
            : (suspend (Uri, ((DataInfo, List<MovieData>) -> Unit)) -> Unit) =
        { uri, doneCallback ->
            EspressoIdlingResource.increment()
            withContext(dispatcher) {
                val jsonData = getJSONData(uri)
                val totalPage = jsonData?.let { JSONObject(it).getInt("total_pages") } ?: 0
                val page = jsonData?.let { JSONObject(it).getInt("page") } ?: 0
                val data = jsonData?.let { parse2ListMovieData(it) } ?: emptyList()
                doneCallback(DataInfo(totalPage, page), data)
                EspressoIdlingResource.decrement()
            }
        }
    private
    val listSingleDataBuilder
            : (suspend (Uri, ((DataInfo, List<MovieData>) -> Unit)) -> Unit) =
        { _, doneCallback ->
            EspressoIdlingResource.increment()
            withContext(dispatcher) {
                val favAllID = dao.getFavorite()
                val data = List(favAllID.size) { i ->
                    val jObject: JSONObject?
                    if (favAllID[i].mediaType == "unknown") {
                        jObject = try {
                            getJSONData(
                                getLinkMovie(
                                    MOVIE,
                                    favAllID[i].idFavorite.toLong(),
                                    favAllID[i].originalLanguage
                                )
                            )?.let {
                                JSONObject(it)
                            }.also {
                                if (it?.has("status_code") == true)
                                    throw Exception("tv")
                            }
                        } catch (e: Exception) {
                            getJSONData(
                                getLinkMovie(
                                    TV,
                                    favAllID[i].idFavorite.toLong(),
                                    favAllID[i].originalLanguage
                                )
                            )?.let {
                                JSONObject(it)
                            }
                        }
                    } else {
                        jObject = getJSONData(
                            getLinkMovie(
                                favAllID[i].mediaType,
                                favAllID[i].idFavorite.toLong(),
                                favAllID[i].originalLanguage
                            )
                        )?.let {
                            JSONObject(it)
                        }
                    }
                    jObject?.let {
                        parse2MovieData(it)
                    } ?: MovieData()
                }
                doneCallback(DataInfo(1, 1), data)
                EspressoIdlingResource.decrement()
            }
        }

    private fun buildPageList(builder: (suspend (Uri, ((DataInfo, List<MovieData>) -> Unit)) -> Unit))
            : LiveData<PagedList<MovieData>> =
        LivePagedListBuilder(
            dao.getDataSource(),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setPrefetchDistance(2)
                .build()
        )
            .setBoundaryCallback(RepoBoundaryCallback(builder))
            .build()

    private inner class RepoBoundaryCallback(
        private val callBuilder: (suspend (Uri, doneCallback: ((DataInfo, List<MovieData>) -> Unit)) -> Unit)
    ) : PagedList.BoundaryCallback<MovieData>() {

        private var onLoading = false
        private var curentInfo = DataInfo(0, 0)

        init {
            EspressoIdlingResource.increment()
            repoCoroutine.launch(dispatcher) {
                dao.delete()
                EspressoIdlingResource.decrement()
            }
        }

        private fun getAndSaveData(
            page: Int = 1,
            dataInfo: ((pageInfo: DataInfo) -> Unit)? = null
        ) = link?.let { lll ->
            var info = DataInfo(0, 0)
            if (!onLoading) {
                onLoading = true
                EspressoIdlingResource.increment()
                repoCoroutine.launch {
                    callBuilder(lll(page)) { i, d ->
                        info = i
                        dao.insert(d)
                    }.also {
                        onLoading = false
                        dataInfo?.let {
                            dataInfo(info)
                        }
                        EspressoIdlingResource.decrement()
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
    }
}
