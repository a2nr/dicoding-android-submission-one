package io.github.a2nr.submissionmodul1

import android.content.ContentResolver
import android.content.ContentUris
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
import io.github.a2nr.submissionmodul1.adapter.ItemMovieAdapter
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.repository.MovieDataProvider
import io.github.a2nr.submissionmodul1.viewmodel.ListMovieViewModel
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.coroutines.*
import okhttp3.Dispatcher

class FavoriteActivity : AppCompatActivity() {

    private val mainJob = Job()
    private val mainCoroutine = CoroutineScope(Dispatchers.Main + mainJob)
    private val muteListMovieData = MutableLiveData<List<MovieData>>()
    private val ldListMovieData: LiveData<List<MovieData>>
        get() = muteListMovieData
    private var onClickItemView: ((v: View, p: Int) -> Unit)? = null
    private var selectedIndex: Int? = null

    companion object {
        val NAME_FAV = "My Favorite"
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
                onClickItemView = { _, p ->
                    onClickItem(p)
                }
                adapter =
                    ItemMovieAdapter(this@FavoriteActivity)
                        .apply {
                            setCallBack(onClickItemView)
                            submitData(it)
                        }
            }
            it.forEach { movie ->
                Log.i("Favorite", movie.toString())
            }
        })
        fab.setOnClickListener {
            if (selectedIndex != null) {
                ldListMovieData.value?.get(selectedIndex!!)?.id?.let {
                    val content = Uri.Builder().scheme(MovieDataProvider.SCHEME)
                        .authority(MovieDataProvider.AUTHORITY)
                        .appendPath(MovieData.NAME)
                        .appendPath(it.toString())
                        .build()
                    mainCoroutine.launch {
                        withContext(Dispatchers.IO) {
                            contentResolver.delete(content, null, null)
                            getData()
                        }
                    }
                    updateFabIcon(false)
                    goToListView()
                }
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

    private fun onClickItem(index: Int) {
        layout_list_movie.apply {
            val mov = ldListMovieData.value?.get(index)
            mov?.let {
                this@FavoriteActivity.apply {
                    media_type.text = it.media_type
                    overview.text = it.overview
                    release_date.text = it.release_date
                    titleTextView.text = it.title
                    vote_average.text = it.vote_average.toString()

                    posterImageView.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(ListMovieViewModel.getLinkImage(it.poster_path))
                        .into(posterImageView)
                    layout_detail_movie.visibility = View.VISIBLE
                    toolbar_title.visibility = View.INVISIBLE
                    selectedIndex = index
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
