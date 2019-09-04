package io.github.a2nr.submissionmodul1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import io.github.a2nr.submissionmodul1.R
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.viewModel.ListMovieViewModel


class ItemMovieAdapter(
    private var context: Context
) : BaseAdapter() {
    private lateinit var md: List<MovieData>
    private var callBack : ((v:View,p:Int)-> Unit)?=null

    fun submitData(m: List<MovieData>): ItemMovieAdapter {
        md = m
        return this
    }
    fun setCallBack(callBack : ((v:View,p:Int)-> Unit)?) : ItemMovieAdapter{
        this.callBack = callBack
        return this
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.item_movie, p2, false)
        }
        return ViewHolder(view).set(callBack).at(p0).bind()
    }

    override fun getItem(p0: Int): Any {
        return md[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return md.size
    }

    inner class ViewHolder(private val view: View?) {
        private var position: Int = 0
        private var titleText: TextView
        private var languageText: TextView
        private var rateText: TextView
        private var releaseDateText: TextView
        private var imagePosterMovie: ImageView
        private var layoutClickAble : LinearLayout
        private var onClickListener : View.OnClickListener
        private var onClick:((v:View,p:Int)->Unit)?=null
        init {
            titleText = view!!.findViewById(R.id.titleText)
            languageText = view.findViewById(R.id.languageText)
            rateText = view.findViewById(R.id.rateText)
            releaseDateText = view.findViewById(R.id.releaseDateText)
            imagePosterMovie = view.findViewById(R.id.imagePosterMovie)
            layoutClickAble = view.findViewById(R.id.layout_clickable)
            onClickListener= View.OnClickListener {
                onClick?.invoke(it,position)
            }
            layoutClickAble.setOnClickListener(onClickListener)
        }

        fun set(clickCallback:((v:View,p:Int)->Unit)?):ViewHolder{
            onClick = clickCallback
            return this
        }
        fun at(potition: Int): ViewHolder{
            this.position = potition
            return this
        }
        fun bind(): View {
            val md = this@ItemMovieAdapter.md[this.position]
            this.titleText.text = md.title
            this.languageText.text = md.original_language
            this.rateText.text = md.vote_average.toString()
            this.releaseDateText.text = md.release_date
            Glide.with(this@ItemMovieAdapter.context)
                .load(ListMovieViewModel.getLinkImage(md.backdrop_path))
                .placeholder(R.drawable.ic_warning_48px)
                .into(this.imagePosterMovie)
            return view!!
        }

    }
}