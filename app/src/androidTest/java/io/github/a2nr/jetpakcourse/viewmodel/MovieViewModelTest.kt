package io.github.a2nr.jetpakcourse.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.a2nr.jetpakcourse.R
import io.github.a2nr.jetpakcourse.repository.MovieData
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/* Pengujian akses API
 * * Melakukan akses API dan check null atau empty
 */

@RunWith(AndroidJUnit4::class)
class MovieViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var context: Context
    private lateinit var viewModel: MovieViewModel
    private var listMovieData: List<MovieData> = emptyList()
    private var observerDone = false
    private val observer = Observer<List<MovieData>> {
        listMovieData = it
        observerDone = true
    }

    @Before
    fun setUp() {

        context = InstrumentationRegistry.getInstrumentation().targetContext
        viewModel = MovieViewModel(context)
        viewModel.listMovieData.observeForever(observer)
    }

    @After
    fun endTest() {
        viewModel.listMovieData.removeObserver(observer)
    }

    @Test
    fun doGetMovies() {
        viewModel.doGetMovies(
            MovieViewModel.MOVIE,
            "day",
            context.resources.getString(R.string.lang_code)
        )
        while (observerDone.not()) {
            Thread.sleep(100)
        }
        assertNotNull(listMovieData)
    }
}