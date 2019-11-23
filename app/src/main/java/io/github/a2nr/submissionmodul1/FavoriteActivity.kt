package io.github.a2nr.submissionmodul1

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.repository.MovieDataProvider
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.coroutines.*

class FavoriteActivity : AppCompatActivity() {

    private val mainJob = Job()
    private val mainCoroutine = CoroutineScope(Dispatchers.Main + mainJob)
    private val muteListMovieData = MutableLiveData<List<MovieData>>()
    private val ldListMovieData: LiveData<List<MovieData>>
        get() = muteListMovieData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val content = Uri.Builder().scheme(MovieDataProvider.SCHEME)
            .authority(MovieDataProvider.AUTHORITY)
            .appendPath(MovieData.NAME)
            .build()
        mainCoroutine.launch {
            muteListMovieData.value = withContext(Dispatchers.IO) {
                val que = contentResolver.query(
                    Uri.parse(content.toString()),
                    arrayOf(
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
                            vote_average =
                                cursor.getFloat(cursor.getColumnIndexOrThrow(MovieData.VOTE_AVERAGE))
                            title = cursor.getString(cursor.getColumnIndexOrThrow(MovieData.TITLE))
                            release_date =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.RELEASE_DATE))
                            overview =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.OVERVIEW))
                            media_type =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.MEDIA_TYPE))
                            poster_path =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.POSTER_PATH))

                            cursor.moveToNext()
                        }
                    }
                }
                que?.close()
                mov
            }
        }
        ldListMovieData.observe(this, Observer {
            it.forEach { movie ->
                Log.i("Favorite", movie.toString())
            }
        })
    }
}
