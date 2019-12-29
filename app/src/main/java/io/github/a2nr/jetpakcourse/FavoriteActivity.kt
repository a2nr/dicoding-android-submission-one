package io.github.a2nr.jetpakcourse

import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.a2nr.jetpakcourse.adapter.ItemMovieAdapter
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataProvider
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import io.github.a2nr.jetpakcourse.widgetapp.StackImageAppWidgetProvider
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.coroutines.*

class FavoriteActivity : AppCompatActivity() {

    private val mainJob = Job()
    private val mainCoroutine = CoroutineScope(Dispatchers.Main + mainJob)
    private val muteListMovieData = MutableLiveData<List<MovieData>>()
    private val ldListMovieData: LiveData<List<MovieData>>
        get() = muteListMovieData
    private var selectedIndex: Int? = null

    companion object {
        const val NAME_FAV = "My Favorite"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setSupportActionBar(toolbar)
        title = ""
        ldListMovieData.observe(this, Observer {
            this.listFavoriteMovie.apply {
                addItemDecoration(
                    ItemDecoration(
                        resources
                            .getDimension(R.dimen.activity_horizontal_margin).toInt()
                    )
                )
                layoutManager = run {
                    when (this.resources.configuration.orientation) {
                        Configuration.ORIENTATION_PORTRAIT ->
                            LinearLayoutManager(this@FavoriteActivity)
                        //Configuration.ORIENTATION_LANDSCAPE
                        else ->
                            GridLayoutManager(this@FavoriteActivity, 2)
                    }
                }
                adapter =
                    ItemMovieAdapter(this@FavoriteActivity, it) { _, data ->
                        onClickItem(data)
                    }
            }
            it.forEach { movie ->
                Log.i("Favorite", movie.toString())
            }
        })
        fab.setOnClickListener {
            ldListMovieData.value?.get(selectedIndex!!)?.id?.let {
                val content = Uri.Builder().scheme(MovieDataProvider.SCHEME)
                    .authority(MovieDataProvider.AUTHORITY)
                    .appendPath(MovieData.NAME)
                    .appendPath(it.toString())
                    .build()
                mainCoroutine.launch {
                    withContext(Dispatchers.IO) {
                        contentResolver.delete(content, null, null)
                        StackImageAppWidgetProvider.sendRefresh(this@FavoriteActivity)
                        getData()
                    }
                }
                updateFabIcon(false)
                goToListView()
            }
        }

        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            if (layout_list_movie.visibility == View.GONE) {
                goToListView()
            } else {
                this.finish()
            }
        }
        toolbar_title.text = NAME_FAV
        getData()

    }

    private fun goToListView() {
        layout_list_movie.visibility = View.VISIBLE
        layout_detail_movie.visibility = View.GONE
        posterImageView.visibility = View.GONE
        toolbar_title.visibility = View.VISIBLE
        selectedIndex = null
    }

    private fun onClickItem(data: MovieData?) {
        layout_list_movie.apply {
            data?.let {
                this@FavoriteActivity.apply {
                    media_type.text = it.mediaType
                    overview.text = it.overview
                    release_date.text = it.releaseDate
                    titleTextView.text = it.title
                    vote_average.text = it.voteAverage.toString()

                    posterImageView.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(
                            MovieDataRepository.getLinkImage(
                                posterImageView.width.toString(),
                                it.posterPath
                            )
                        )
                        .into(posterImageView)
                    layout_detail_movie.visibility = View.VISIBLE
                    toolbar_title.visibility = View.INVISIBLE
                }
                visibility = View.GONE
                updateFabIcon(true)
            }
        }
    }

    private fun getData() {
        val content = Uri.Builder().scheme(MovieDataProvider.SCHEME)
            .authority(MovieDataProvider.AUTHORITY)
            .appendPath(MovieData.NAME)
            .build()
        mainCoroutine.launch {
            muteListMovieData.value = withContext(Dispatchers.IO) {
                val que = contentResolver.query(
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
                            voteAverage =
                                cursor.getFloat(cursor.getColumnIndexOrThrow(MovieData.VOTE_AVERAGE))
                            title =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.TITLE))
                            releaseDate =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.RELEASE_DATE))
                            overview =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.OVERVIEW))
                            mediaType =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.MEDIA_TYPE))
                            posterPath =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.POSTER_PATH))
                            backdropPath =
                                cursor.getString(cursor.getColumnIndexOrThrow(MovieData.BACKDROP_PATH))
                            cursor.moveToNext()
                        }
                    }
                }
                que?.close()
                mov
            }
        }

    }

    private fun updateFabIcon(boolean: Boolean) {
        this.fab.run {
            setImageDrawable(
                this.resources.getDrawable(
                    if (boolean) R.drawable.ic_favorite_24px else R.drawable.ic_favorite_border_24px,
                    this.resources.newTheme()
                )
            )
            hide(); show()
        }

    }

    inner class ItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                val pos = parent.getChildAdapterPosition(view) + 1
                if (resources.configuration.orientation
                    == Configuration.ORIENTATION_PORTRAIT
                ) {
                    if (pos == 1) top = margin
                } else {
                    if ((pos == 1) || (pos == 2)) top = margin
                }
                left = margin
                right = margin
                bottom = margin

            }
        }

    }
}
