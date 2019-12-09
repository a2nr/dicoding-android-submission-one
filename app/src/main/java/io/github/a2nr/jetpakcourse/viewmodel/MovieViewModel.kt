package io.github.a2nr.jetpakcourse.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import io.github.a2nr.jetpakcourse.repository.MovieDatabase

class MovieViewModel(
    context: Context
//) : AndroidViewModel(application) {
): ViewModel(){
    companion object {
        const val MOVIE: String = "movie"
        const val TV: String = "tv"
        fun getLinkImage(key: String): Uri {
            return "${MovieDataRepository.LINK_IMAGE}$key".toUri()
        }
    }

    private val repo: MovieDataRepository = MovieDataRepository(
        MovieDatabase
            .getInstance(context)
            .movieDao()
    )
    val listMovieData: LiveData<List<MovieData>>
        get() = repo.mutMovieData

    val isMovieExists: LiveData<Boolean>
        get() = repo.mutIdExists

    val releaseToday: LiveData<List<MovieData>>
        get() = repo.mutMovieData

    var typeTag: Int? = null
    fun doGetMovies(media_type: String, time_window: String, language: String) =
        repo.doGetMovies(media_type, time_window, language)

    fun doSearchMovie(media_type: String, queryTitle: String, language: String) =
        repo.doSearchMovies(media_type, queryTitle, language)

    fun markAsFavorite(movieData: MovieData) = repo.storeMovie(movieData)
    fun unmarkAsFavorite(movieData: MovieData) = repo.removeMovie(movieData)
    fun doGetFavorite() = repo.doGetMoviesStorage()
    fun doCheckMovieExists(key: Int) = repo.doCheckIsMovieExists(key)
    fun doGetReleaseMovie(date: String) = repo.doGetReleaseMovie(date)
}