package io.github.a2nr.submissionmodul1.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.github.a2nr.submissionmodul1.R
import io.github.a2nr.submissionmodul1.databinding.ItemMovieBinding
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.viewModel.ListMovieViewModel


class ItemMovieAdapter(
    private var context: Context
) : RecyclerView.Adapter<ItemMovieAdapter.ViewHolder>() {
    private var md: List<MovieData>? =null
    private var callBack: ((v: View, p: Int) -> Unit)? = null

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

    override fun getItemCount(): Int {
        return md.let {
            if (it.isNullOrEmpty())
                0
            else
                it.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.at(position).bind()
    }

    inner class ViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        private var pos: Int = 0
        private var onClickListener: View.OnClickListener
        private var onClick: ((v: View, p: Int) -> Unit)? = null

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

        fun at(potition: Int): ViewHolder {
            this.pos = potition
            return this
        }

        fun bind(): View {
            //.md object selalu ada instance nya. lihat ListMovieFragment:70 dan file ini line 47
            val md = this@ItemMovieAdapter.md!![this.pos]
            binding.titleText.text = md.title
            binding.languageText.text = md.original_language
            binding.rateText.text = md.vote_average.toString()
            binding.releaseDateText.text = md.release_date
            Glide.with(this@ItemMovieAdapter.context)
                .load(ListMovieViewModel.getLinkImage(md.backdrop_path))
                .placeholder(R.drawable.ic_warning_48px)
                .fitCenter()
                .into(binding.imagePosterMovie)
            return binding.root
        }

    }
}