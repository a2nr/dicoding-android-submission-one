package io.github.a2nr.jetpakcourse.adapter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemMovieDecoration(
    private val context: Context,
    private val margin: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            val pos = parent.getChildAdapterPosition(view) + 1
            if (context.resources.configuration.orientation
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