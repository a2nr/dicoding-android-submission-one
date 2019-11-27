package io.github.a2nr.myfavoritemovie.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.github.a2nr.myfavoritemovie.repository.MovieData
import io.github.a2nr.myfavoritemovie.repository.MovieDataRepository

class ListMovieViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        fun getLinkImage(key: String): Uri {
            return "${MovieDataRepository.LINK_IMAGE}$key".toUri()
        }
    }

    private val repo: MovieDataRepository = MovieDataRepository(application.applicationContext)
    val listMovieData: LiveData<List<MovieData>>
        get() = repo.mutMovieData

    fun unmarkAsFavorite(id : Int) = repo.removeMovie(id)
    fun doGetFavorite() = repo.doGetMovies()
}