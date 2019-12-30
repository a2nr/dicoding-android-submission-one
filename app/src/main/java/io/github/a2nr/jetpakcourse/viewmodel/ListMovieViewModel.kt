package io.github.a2nr.jetpakcourse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository

class ListMovieViewModel(val repository: MovieDataRepository) : ViewModel() {

    var typeTag: Int? = null

    val listMovieData: LiveData<PagedList<MovieData>> =
        Transformations.switchMap(repository.pageListData) { it }


    fun doGetMovies(mediaType: String, time_window: String, language: String) =
        repository.doGetMovies(mediaType, time_window, language)

    fun doSearchMovie(mediaType: String, queryTitle: String, language: String) =
        repository.doSearchMovies(mediaType, queryTitle, language)

    fun doGetFavorite() = repository.doGetFavoriteMovie()

    fun doGetReleaseMovie(date: String) = repository.doGetReleaseMovie(date)

}