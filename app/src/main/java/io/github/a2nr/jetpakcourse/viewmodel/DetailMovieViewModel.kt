package io.github.a2nr.jetpakcourse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository

class DetailMovieViewModel(val repository: MovieDataRepository) : ViewModel() {

    val isMovieExists: LiveData<Boolean>
        get() = repository.mutIsFavorite

    fun markAsFavorite(movieData: MovieData) = movieData.run {
        //TODO ganti ke movieDao?
        repository.storeMovie(this.let { it.isFavorite = true;it })
    }

    fun unMarkAsFavorite(movieData: MovieData) =
        repository.movieDao?.updateFavorite(false, movieData.id)

    fun doCheckIsFavorite(key: Int) = repository.doCheckIsFavorite(key)
}