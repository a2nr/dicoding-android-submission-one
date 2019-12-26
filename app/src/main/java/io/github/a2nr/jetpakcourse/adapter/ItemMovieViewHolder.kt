package io.github.a2nr.jetpakcourse.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
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
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository

class ItemMovieViewHolder(private val binding: ItemMovieBinding) :
    RecyclerView.ViewHolder(binding.root) {
    inner class ImageListener : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            Log.e("ImageListener", "failed !!")
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

    fun bind(
        context: Context,
        movieData: MovieData?,
        onClick: ((v: View, data: MovieData) -> Unit)
        ): View {
        movieData?.let {
            binding.titleText.text = it.title
            binding.languageText.text = it.originalLanguage
            binding.rateText.text = it.voteAverage.toString()
            binding.releaseDateText.text = it.releaseDate
            binding.layoutClickable.setOnClickListener{ view ->
                onClick.invoke(view, movieData)
            }
            Glide.with(context).asDrawable().error(R.drawable.ic_warning_48px)
                .fitCenter().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).load(
                    MovieDataRepository.getLinkImage(
                        "300",
                        it.backdropPath
                    )
                )
                .listener(ImageListener())
                .into(binding.imagePosterMovie)

        }
        return binding.root
    }

}