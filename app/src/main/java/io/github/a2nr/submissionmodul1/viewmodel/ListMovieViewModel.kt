package io.github.a2nr.submissionmodul1.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.github.a2nr.submissionmodul1.repository.MovieData
import io.github.a2nr.submissionmodul1.repository.MovieDataRepository
import io.github.a2nr.submissionmodul1.repository.MovieDatabase

class ListMovieViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object{
        const val MOVIE: String = "movie"
        const val TV: String = "tv"
        fun getLinkImage(key: String): Uri {
            return "${MovieDataRepository.LINK_IMAGE}$key".toUri()
        }
    }
    private val repo: MovieDataRepository = MovieDataRepository(
        MovieDatabase
            .getInstance(application)
            .movieDao()
    )
    val listMovieData: LiveData<List<MovieData>>
        get() = repo.mutMovieData

    val isMovieExists: LiveData<Boolean>
        get() = repo.mutIdExists

    var typeTag: Int? = null
    fun doGetMovies(media_type: String, time_window: String, language: String) {
        repo.doGetMovies(media_type,time_window,language)
    }
    fun markAsFavorite(movieData: MovieData){
        repo.storeMovie(movieData)
    }
    fun unmarkAsFavorite(movieData: MovieData){
        repo.removeMovie(movieData)
    }
    fun doGetFavorite(){
        repo.doGetMoviesStorage()
    }
    fun doCheckMovieExists(key: Int){
        repo.doCheckIsMovieExists(key)
    }
}