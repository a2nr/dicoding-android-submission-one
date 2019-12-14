package io.github.a2nr.jetpakcourse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository

class DetailMovieViewModel(val repository: MovieDataRepository) : ViewModel(){

    val isMovieExists: LiveData<Boolean>
        get() = repository.mutIdExists

    fun markAsFavorite(movieData: MovieData) = repository.storeMovie(movieData)
    fun unMarkAsFavorite(movieData: MovieData) = repository.removeMovie(movieData)
    fun doCheckMovieExists(key: Int) = repository.doCheckIsMovieExists(key)
}