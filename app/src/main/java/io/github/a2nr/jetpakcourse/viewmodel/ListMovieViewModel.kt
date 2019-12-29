package io.github.a2nr.jetpakcourse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.github.a2nr.jetpakcourse.helper.GetMoviesParams
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository

class ListMovieViewModel(val repository: MovieDataRepository) : ViewModel() {
    private val type = MutableLiveData<Int>()
    val typeTag: LiveData<Int>
        get() = this.type

    val listMovieData: LiveData<List<MovieData>>
        get() = repository.mutMovieData

    private val getMoviesParam = MutableLiveData<GetMoviesParams>()
    var pageListMovie: LiveData<PagedList<MovieData>> =
        Transformations.switchMap(getMoviesParam) {
            repository.pageListMovieBuilder(it)
        }

    fun setTypeTag(type: Int) {
        this.type.value = type
    }

    fun doGetMovies(mediaType: String, timeWindow: String, language: String) =
        getMoviesParam.postValue(GetMoviesParams(mediaType, timeWindow, language) {
            MovieDataRepository.getLinkTrendingMovie(mediaType, language, it)
        })

    fun doSearchMovie(mediaType: String, queryTitle: String, language: String) =
        getMoviesParam.postValue(GetMoviesParams(mediaType, "", language) {
            MovieDataRepository.getLinkSearchMovie(mediaType, queryTitle, language, it)
        })

    fun doGetFavorite() = repository.doGetFavoriteMovies()

    fun doGetReleaseMovie(date: String) =
        getMoviesParam.postValue(GetMoviesParams("", date, "") {
            MovieDataRepository.getLinkReleaseToday(date)
        })


}