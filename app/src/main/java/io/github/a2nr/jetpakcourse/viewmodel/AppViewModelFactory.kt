package io.github.a2nr.jetpakcourse.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import io.github.a2nr.jetpakcourse.repository.MovieDatabase

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = MovieDataRepository(
            MovieDatabase
                .getInstance(application)
                .movieDao()
        )
        return when {
            modelClass.isAssignableFrom(ListMovieViewModel::class.java) ->
                ListMovieViewModel(repository)
            modelClass.isAssignableFrom(DetailMovieViewModel::class.java) ->
                DetailMovieViewModel(repository)
            else -> throw IllegalArgumentException("Unsupported ViewModel")
        } as T
    }
}