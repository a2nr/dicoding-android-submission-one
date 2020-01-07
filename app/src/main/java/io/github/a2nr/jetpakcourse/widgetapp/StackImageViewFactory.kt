package io.github.a2nr.jetpakcourse.widgetapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.github.a2nr.jetpakcourse.R
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataProvider
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository

internal class StackImageViewFactory(
    private val context: Context
) :
    RemoteViewsService.RemoteViewsFactory {
    private val widgetImage = ArrayList<Bitmap>()
    private val content = Uri.Builder().scheme(MovieDataProvider.SCHEME)
        .authority(MovieDataProvider.AUTHORITY)
        .appendPath(MovieData.NAME)
        .build()
    private lateinit var uriFav: Uri

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun onDataSetChanged() {
        widgetImage.clear()

        uriFav = Uri.parse(content.toString())
        context.contentResolver.let { contentResolver ->
            val cr = contentResolver.query(
                uriFav,
                arrayOf(MovieData.TITLE, MovieData.POSTER_PATH),
                null, null, null
            )
            cr?.run {
                this.moveToFirst()
                for (i in 1..this.count) {
                    var isDone = false
                    val index = this.getColumnIndexOrThrow(MovieData.POSTER_PATH)
                    val path = this.getString(index)
                    val widthPoster = 100
                    val heightPoster = 150
                    Glide.with(context.applicationContext)
                        .asBitmap().fitCenter().override(widthPoster, heightPoster)
                        .load(
                            MovieDataRepository
                                .getLinkImage(
                                    "200",
                                    path
                                )
                        )
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onLoadCleared(placeholder: Drawable?) {
                                Log.i("onLoadCleared", " images cleared ?")
                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                widgetImage.add(resource)
                                isDone = true
                            }
                        })
                    while (!isDone)
                        Thread.sleep(100)
                    this.moveToNext()
                }
            }
            cr?.close()
        }
    }

    override fun getViewAt(p0: Int): RemoteViews {
        return RemoteViews(
            context.packageName,
            R.layout.item_widget_movie
        )
            .apply {
                setImageViewBitmap(R.id.item_widget_image, widgetImage[p0])
            }
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    override fun getCount(): Int = widgetImage.size

    override fun getViewTypeCount(): Int = 1

}