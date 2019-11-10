package io.github.a2nr.submissionmodul1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.github.a2nr.submissionmodul1.repository.MovieDataRepository
import io.github.a2nr.submissionmodul1.repository.MovieDatabase

internal class StackImageViewFactory(
    private val context: Context
) :
    RemoteViewsService.RemoteViewsFactory {
    private val widgetImage = ArrayList<Bitmap>()
    private val dao = MovieDatabase.getInstance(context).movieDao()

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun onDataSetChanged() {
        widgetImage.clear()
        dao.getAll().let {
            it.forEach {
                Glide.with(context.applicationContext).asBitmap().fitCenter().override(110, 150)
                    .load(MovieDataRepository.LINK_IMAGE + it.poster_path)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            Log.i("onLoadCleared", " images cleared ?")
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            widgetImage.add(resource)
                        }
                    })
            }
            while (widgetImage.size != it.size)
                Thread.sleep(100)
        }
    }

    override fun getViewAt(p0: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.item_widget_movie)
            .apply {
                setImageViewBitmap(R.id.item_widget_image, widgetImage.get(p0))
            }
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    override fun getCount(): Int = widgetImage.size

    override fun getViewTypeCount(): Int = 1

}