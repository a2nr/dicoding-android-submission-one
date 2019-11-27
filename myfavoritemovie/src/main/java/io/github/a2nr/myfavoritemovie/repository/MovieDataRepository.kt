package io.github.a2nr.myfavoritemovie.repository


import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class MovieDataRepository(private val context: Context) {

    companion object {

        const val LINK_IMAGE: String = "https://image.tmdb.org/t/p/original"

        const val AUTHORITY = "io.github.a2nr.smm5"
        const val SCHEME = "content"

    }

    private val repoJob = Job()
    private val repoCoroutine = CoroutineScope(Dispatchers.Main + repoJob)

    val mutMovieData = MutableLiveData<List<MovieData>>()

    fun removeMovie(id: Int) {
        repoCoroutine.launch {
            val content = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(MovieData.NAME)
                .appendPath(id.toString())
                .build()
            withContext(Dispatchers.IO) {
                context.contentResolver.delete(content, null, null)
            }
        }
    }

    fun doGetMovies() {
        repoCoroutine.launch {
            mutMovieData.value = withContext(Dispatchers.IO){
                doGetData()
            }
        }

    }


    private fun doGetData():List<MovieData>? {
        val content = Uri.Builder().scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(MovieData.NAME)
            .build()
        val que = context.contentResolver.query(
            Uri.parse(content.toString()),
            arrayOf(
                "id",
                MovieData.VOTE_AVERAGE,
                MovieData.TITLE,
                MovieData.RELEASE_DATE,
                MovieData.BACKDROP_PATH,
                MovieData.OVERVIEW,
                MovieData.POSTER_PATH,
                MovieData.MEDIA_TYPE
            ), null, null, null
        )

        val mov: List<MovieData>? = que?.let { cursor ->
            cursor.moveToFirst()
            List(cursor.count) {
                MovieData().apply {
                    id =
                        cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    vote_average =
                        cursor.getFloat(cursor.getColumnIndexOrThrow(MovieData.VOTE_AVERAGE))
                    title =
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieData.TITLE))
                    release_date =
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieData.RELEASE_DATE))
                    overview =
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieData.OVERVIEW))
                    media_type =
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieData.MEDIA_TYPE))
                    poster_path =
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieData.POSTER_PATH))
                    backdrop_path =
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieData.BACKDROP_PATH))
                    cursor.moveToNext()
                }
            }
        }
        que?.close()
        return mov
    }
}
