package io.github.a2nr.jetpakcourse.repository

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import io.github.a2nr.jetpakcourse.helper.GetMoviesParams

class MovieDataDataSource(
    private val repository: MovieDataRepository,
    private val paramRequest: GetMoviesParams

) {
    inner class Factory : DataSource.Factory<Int, MovieData>() {
        override fun create(): DataSource<Int, MovieData> = PagedDataSource()
    }

    inner class PagedDataSource : PageKeyedDataSource<Int, MovieData>() {

        override fun loadInitial(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Int, MovieData>
        ) {
            repository.launchInBackground({
                try {
                    repository.fetchData(paramRequest.link(1)) { _, page, data ->
                        data?.let {
                            callback.onResult(it, page - 1, it.size, null, page + 1)
                        } ?: throw Exception("No Data Found")
                    }
                } catch (e: Exception) {
                    callback.onError(Error("$this:$e"))
                    null
                }
            }, null)
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MovieData>) {
            repository.launchInBackground({
                try {
                    repository.fetchData(paramRequest.link(params.key)) { totalPage, page, data ->
                        callback.onResult(data!!, params.key +
                                if(totalPage > page) 0 else 1)
                    }
                } catch (e: Exception) {
                    callback.onError(Error("$this:$e"))
                    null
                }
            }, null)
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MovieData>) {

        }

    }
}