package io.github.a2nr.jetpakcourse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import io.github.a2nr.jetpakcourse.databinding.ItemMovieBinding
import io.github.a2nr.jetpakcourse.repository.MovieData


class ItemMovieAdapter(
    private var context: Context,
    var callBack: ((v: View, data: MovieData) -> Unit)
) : PagedListAdapter<MovieData, ItemMovieViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieData>() {
            override fun areItemsTheSame(oldItem: MovieData, newItem: MovieData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MovieData, newItem: MovieData): Boolean {
                return oldItem.title == oldItem.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMovieViewHolder =
        ItemMovieBinding.inflate(LayoutInflater.from(context)).let {
            ItemMovieViewHolder(it)
        }

    override fun onBindViewHolder(holder: ItemMovieViewHolder, position: Int) {
        holder.bind(context, getItem(position), callBack)
    }

    override fun onViewRecycled(holder: ItemMovieViewHolder) {
        holder.clear(context)
    }
}