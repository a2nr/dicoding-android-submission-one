package io.github.a2nr.jetpakcourse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.a2nr.jetpakcourse.databinding.ItemMovieBinding
import io.github.a2nr.jetpakcourse.repository.MovieData


class ItemMovieAdapter(
    private var context: Context,
    var listMovieData: List<MovieData>? = null,
    var callBack: ((v: View, data: MovieData) -> Unit)
) : RecyclerView.Adapter<ItemMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMovieViewHolder =
        ItemMovieBinding.inflate(LayoutInflater.from(context)).let {
            ItemMovieViewHolder(it)
        }

    override fun getItemCount(): Int = listMovieData?.size ?: 0

    override fun onBindViewHolder(holder: ItemMovieViewHolder, position: Int) {
        holder.bind(context,listMovieData?.get(position),callBack)
    }
}