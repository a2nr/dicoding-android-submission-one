package io.github.a2nr.jetpakcourse.viewmodel

/* DetailMovieViewModel
 * 1) markAsFavorite()
 *      Pengujian dilakukan dengan bantuan method doSearchMovie() untuk mendapatkan data MovieData
 *      lalu dilakukan markAsFavorite() untuk menyimpannnya, lalu dilakukan doGetFavorite()
 *      untuk mendapatkan MovieData dari database dan dilakukan assertEquals() terhadap title
 *
 * 2) unMarkAsFavorite()
 *      Pengujian dilakukan dengan bantuan method doSearchMovie() untuk mendapatkan data MovieData
 *      lalu dilakukan markAsFavorite() untuk menyimpannnya, lalu dilakukan doGetFavorite()
 *      untuk mendapatkan MovieData dari database dan dilakukan assertEquals() terhadap title
 *      setelah itu dilakukan unMarkAsFavorite() untuk menghilangkan MovieData dari daftar
 *      favorite. Setelah itu dilakukan doGetFavorite() dengan ekspektasi balikan data
 *      isNullOrEmpty()
 *
 * 3) doCheckMovieExists()
 *      Pengujian dilakukan dengan bantuan method doSearchMovie() untuk mendapatkan data MovieData
 *      lalu dilakukan markAsFavorite() untuk menyimpannnya, lalu dilakukan doGetFavorite()
 *      untuk mendapatkan MovieData dari database dan dilakukan doCheckMovieExists() untuk
 *      mendapatkan kepastian keberadaaan data dalam database. Setelah itu dilakukan
 *      unMarkAsFavorite() untuk menghapus data dari daftar favorited di database
 *      lalu dilakukan pengecekan kembali untuk memastikan data telah tidak ada.
 *
 */
import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.github.a2nr.jetpakcourse.repository.MovieDataAccess
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

@RunWith(RobolectricTestRunner::class) //this library used to make test easier for mock my repository
@Config(sdk = [Build.VERSION_CODES.O_MR1]) //https://stackoverflow.com/questions/56808485/robolectric-and-android-sdk-29
class DetailMovieViewModelTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var repository: MovieDataRepository
    lateinit var viewModel: DetailMovieViewModel
    lateinit var listViewModel: ListMovieViewModel
    lateinit var dao: MovieDataAccess

    val context: Context = ApplicationProvider.getApplicationContext<Context>()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        dao = Room.databaseBuilder(
            context.applicationContext,
            MovieDatabase::class.java,
            "movie_database"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build().movieDao()
        repository = MovieDataRepository(dao, mainCoroutineRule.testDispatcher)
        viewModel = DetailMovieViewModel(repository)
        listViewModel = ListMovieViewModel(repository)
    }

    @Test
    fun markAsFavorite() {
        listViewModel.run {
            doSearchMovie("movie", "evangelion", "en")
            listMovieData.observeForTesting {
                listMovieData.value?.get(0)?.let {
                    viewModel.markAsFavorite(it)
                }
            }
            doGetFavorite()
            listMovieData.observeForTesting {
                Assert.assertEquals(
                    listMovieData.value!![0]!!.title,
                    "Neon Genesis Evangelion: The End of Evangelion"
                )
            }
        }
    }

    @Test
    fun unMarkAsFavorite() {
        listViewModel.run {
            doSearchMovie("movie", "evangelion", "en")
            listMovieData.observeForTesting {
                listMovieData.value?.get(0)?.let {
                    viewModel.markAsFavorite(it)
                }
            }
            doGetFavorite()
            listMovieData.observeForTesting {
                Assert.assertEquals(
                    listMovieData.value!![0]!!.title,
                    "Neon Genesis Evangelion: The End of Evangelion"
                )
            }
            viewModel.unMarkAsFavorite(listMovieData.value!![0]!!)
            doGetFavorite()
            listMovieData.observeForTesting {
                assert(listMovieData.value.isNullOrEmpty())
            }
        }
    }

    @Test
    fun doCheckMovieExists() {
        var id = 0
        listViewModel.run {
            doSearchMovie("movie", "evangelion", "en")
            listMovieData.observeForTesting {
                listMovieData.value?.get(0)?.let {
                    viewModel.markAsFavorite(it)
                }
            }
            doGetFavorite()
            listMovieData.observeForTesting {
                Assert.assertEquals(
                    listMovieData.value!![0]!!.title,
                    "Neon Genesis Evangelion: The End of Evangelion"
                )
                id = listMovieData.value!![0]!!.id
            }
            viewModel.run {
                doCheckMovieExists(id)
                isMovieExists.observeForever {
                    assert(it)
                }
            }
            viewModel.unMarkAsFavorite(listMovieData.value!![0]!!)
            doGetFavorite()
            listMovieData.observeForTesting {
                assert(listMovieData.value.isNullOrEmpty())
            }
            viewModel.run {
                doCheckMovieExists(id)
                isMovieExists.observeForever {
                    assert(!it)
                }
            }
        }

    }
}