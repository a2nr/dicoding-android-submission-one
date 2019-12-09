package io.github.a2nr.jetpakcourse.adapter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.github.a2nr.jetpakcourse.R
import io.github.a2nr.jetpakcourse.databinding.ItemMovieBinding
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.viewmodel.MovieViewModel


class ItemMovieAdapter(
    private var context: Context
) : RecyclerView.Adapter<ItemMovieAdapter.ViewHolder>() {

    private var md: List<MovieData>? = null
    private var callBack: ((v: View, p: Int) -> Unit)? = null
    private val imageReq = Glide.with(this.context)
        .asDrawable()
        .error(R.drawable.ic_warning_48px)
        .fitCenter()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    fun submitData(m: List<MovieData>): ItemMovieAdapter {
        md = m
        return this
    }

    fun setCallBack(callBack: ((v: View, p: Int) -> Unit)?): ItemMovieAdapter {
        this.callBack = callBack
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(this.context))
        return ViewHolder(binding).set(callBack)
    }

    override fun getItemCount(): Int = md?.size ?:0

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        Log.i("onViewAttached","Holder number${holder.pos.toString()}")
        super.onViewAttachedToWindow(holder)
    }
    override fun onViewRecycled(holder: ViewHolder) {
        Log.i("onViewRecycled","Holder number${holder.pos.toString()}")
        super.onViewRecycled(holder)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.at(position).bind()
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
    inner class ViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var pos: Int = 0
        private var onClickListener: View.OnClickListener
        private var onClick: ((v: View, p: Int) -> Unit)? = null
        private val imageListener = ImageListener()

        init {
            onClickListener = View.OnClickListener {
                onClick?.invoke(it, pos)
            }
            binding.layoutClickable.setOnClickListener(onClickListener)
        }


        fun set(clickCallback: ((v: View, p: Int) -> Unit)?): ViewHolder {
            onClick = clickCallback
            return this
        }

        fun at(position: Int): ViewHolder {
            this.pos = position
            return this
        }

        inner class ImageListener : RequestListener<Drawable>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                Log.e("ImageListener","failed !!")
                binding.layoutClickable.visibility = LinearLayout.VISIBLE
                binding.progressBar.visibility = ProgressBar.INVISIBLE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                binding.layoutClickable.visibility = LinearLayout.VISIBLE
                binding.progressBar.visibility = ProgressBar.INVISIBLE
                return false
            }

        }

        fun bind(): View {
            this@ItemMovieAdapter.md?.let {
                val movieData = it[this@ViewHolder.pos]
                binding.titleText.text = movieData.title
                binding.languageText.text = movieData.original_language
                binding.rateText.text = movieData.vote_average.toString()
                binding.releaseDateText.text = movieData.release_date
                imageReq.load(MovieViewModel.getLinkImage(movieData.backdrop_path))
                    .listener(imageListener)
                    .into(binding.imagePosterMovie)

            }
            return binding.root
        }

    }
}