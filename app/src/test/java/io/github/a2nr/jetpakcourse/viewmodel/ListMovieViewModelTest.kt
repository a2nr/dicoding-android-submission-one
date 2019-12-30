package io.github.a2nr.jetpakcourse.viewmodel

/* ListMovieViewModel test scope
 * 1) doGetMovies()
 *      Pengujian akan dilakukan dengan menggunakan doGetMovies dengan parameter
 *      media_type="movie"; time_window ="day": language="en"
 *      dengan harapan Server akan mengirim array data sejumlah 20
 *      dan dilakukan pengujian media_type dengan ekspektasi "movie"
 *
 * 2) doSearchMovie()
 *      Pengujian akan dilakukan dengan memberikan parameter
 *      media_type="movie"; quaryTitle ="interstellar": language="en"
 *      dengan harapan Server akan mengirim data array
 *      dan dilakukan pengujian title dengan ekspektasi"Interstellar: Nolan's Odyssey"
 *
 * 3) doGetFavorite()
 *      Pengujian akan dilakukan dengan memanfaatkan method doSearchMovie() dan markAsFavorite()
 *      untuk mendapatkan data MovieData lalu disimpan kedalam database.
 *      Pengujian dilakukan dengan memasukkan MovieData sebanyak 4 dengan title
 *       "Neon Genesis Evangelion"
 *       "Steins;Gate"
 *       "Steins;Gate 0"
 *       "Interstellar"
 *      lalu dilakukan pengujian size dan assertEquals() terhadap title
 *
 * 4) doGetReleaseMovie()
 *      Pengujian akan dilakukan dengan mendapatkan memberikan parameter tanggal hari ini
 *      pada methode dengan harapan Server akan memberikan data dengan jumlah tertentu lalu dilakukan
 *      pengujian terhadap tanggal data yang dikirim oleh Server dengan hari ini
 */


import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import io.github.a2nr.jetpakcourse.repository.MovieDatabase
import io.github.a2nr.jetpakcourse.util.MainCoroutineRule
import io.github.a2nr.jetpakcourse.util.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.*

@RunWith(RobolectricTestRunner::class) //this library used to make test easier for mock my repository
@Config(sdk = [Build.VERSION_CODES.O_MR1]) //https://stackoverflow.com/questions/56808485/robolectric-and-android-sdk-29
class ListMovieViewModelTest {
    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var repository: MovieDataRepository
    lateinit var viewModel: ListMovieViewModel

    val context: Context = ApplicationProvider.getApplicationContext<Context>()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        repository = MovieDataRepository(null, mainCoroutineRule.testDispatcher)
        viewModel = ListMovieViewModel(repository)
    }


    @Test
    fun doGetMovies() {
        viewModel.doGetMovies("movie", "day", "en")

        viewModel.listMovieData.observeForTesting {
            assert(!viewModel.listMovieData.value.isNullOrEmpty())
            Assert.assertEquals(viewModel.listMovieData.value?.size, 20)
            viewModel.listMovieData.value?.get(1)?.let {
                Assert.assertEquals(it.mediaType, "movie")
                Assert.assertEquals(it.originalLanguage, "en")
            }
        }
    }

    @Test
    fun doSearchMovie() {
        val title = "interstellar"
        viewModel.doSearchMovie("movie", title, "en")

        viewModel.listMovieData.observeForTesting {
            assert(!viewModel.listMovieData.value.isNullOrEmpty())
            viewModel.listMovieData.value?.get(1)?.let {
                Assert.assertEquals(it.title, "Interstellar: Nolan's Odyssey")
                Assert.assertEquals(it.originalLanguage, "en")
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun doGetFavorite() {

        val dao = Room.databaseBuilder(
            context.applicationContext,
            MovieDatabase::class.java,
            "movie_database"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build().movieDao()

        val repo = MovieDataRepository(dao, mainCoroutineRule.testDispatcher)
        val detailViewModel = DetailMovieViewModel(repo)

        viewModel = ListMovieViewModel(MovieDataRepository(dao, mainCoroutineRule.testDispatcher))
        viewModel.run {
            doSearchMovie("movie", "interstellar", "en")
            listMovieData.observeForTesting {
                listMovieData.value?.get(0)?.let {
                    detailViewModel.markAsFavorite(it)
                }
            }

            viewModel.doGetFavorite()
            listMovieData.observeForTesting {
                listMovieData.value?.let {
                    Assert.assertEquals(it.size, 1)
                }
            }
            doSearchMovie("tv", "stein gate", "en")
            listMovieData.observeForTesting {
                listMovieData.value?.let {
                    detailViewModel.markAsFavorite(it[0]!!)
                    detailViewModel.markAsFavorite(it[1]!!)
                }
            }
            viewModel.doGetFavorite()
            listMovieData.observeForTesting {
                listMovieData.value?.let {
                    Assert.assertEquals(it.size, 3)
                }
            }
            doSearchMovie("tv", "evangelion", "en")
            listMovieData.observeForTesting {
                listMovieData.value?.get(0)?.let {
                    detailViewModel.markAsFavorite(it)
                }
            }
            viewModel.doGetFavorite()
            listMovieData.observeForTesting {
                listMovieData.value?.let {
                    Assert.assertEquals(it.size, 4)
                }
            }
        }
        Assert.assertEquals(viewModel.listMovieData.value!!.size, 4)
        Assert.assertEquals(viewModel.listMovieData.value!![0]!!.title, "Neon Genesis Evangelion")
        Assert.assertEquals(viewModel.listMovieData.value!![0]!!.originalLanguage, "ja")
        Assert.assertEquals(viewModel.listMovieData.value!![1]!!.title, "Steins;Gate")
        Assert.assertEquals(viewModel.listMovieData.value!![1]!!.originalLanguage, "ja")
        Assert.assertEquals(viewModel.listMovieData.value!![2]!!.title, "Steins;Gate 0")
        Assert.assertEquals(viewModel.listMovieData.value!![2]!!.originalLanguage, "ja")
        Assert.assertEquals(viewModel.listMovieData.value!![3]!!.title, "Interstellar")
        Assert.assertEquals(viewModel.listMovieData.value!![3]!!.originalLanguage, "en")
    }

    @Test
    fun doGetReleaseMovie() {
        val cal = SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault()
        ).format(Calendar.getInstance().time)
        viewModel.run {
            doGetReleaseMovie(cal)
            listMovieData.observeForTesting {
                assert(!listMovieData.value.isNullOrEmpty())
                listMovieData.value?.forEach {
                    Assert.assertEquals(it.releaseDate, cal)
                }
            }
        }

    }
}