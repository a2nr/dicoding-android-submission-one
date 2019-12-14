package io.github.a2nr.jetpakcourse.repository

import android.os.Build
import org.junit.Test

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.*

/* Pada Repository test akan dilakukan pengujian terhadap methode getJSONData() dan parse2MovieData()
 * getJSONData()
 * 1)   Pengujian dilakukan dengan getLinkTrendingMovie() dengan kategori movie lalu dilakukan
 *      checking null/empty/blank pada JSON berupa string
 * 2)   Pengujian dilakukan dengan getLinkTrendingMovie() dengan kategori tv lalu dilakukan
 *      checking null/empty/blank pada JSON berupa string
 * 3)   Pengujian dilakukan dengan getLinkSearchMovie() dengan string que "Stein Gate" lalu dilakukan
 *      checking null/empty/blank pada JSON berupa string
 * 4)   Pengujian dilakukan dengan getLinkReleaseToday() dengan tanggal hari ini lalu dilakukan
 *      checking null/empty/blank pada JSON berupa string
 *
 * parse2MovieData()
 * 1)   Pengujian dilakukan dengan getLinkTrendingMovie() dengan kategori movie lalu dilakukan
 *      checking null/empty pada List<MovieData>
 * 2)   Pengujian dilakukan dengan getLinkTrendingMovie() dengan kategori tv lalu dilakukan
 *      checking null/empty pada List<MovieData>
 * 3)   Pengujian dilakukan dengan getLinkSearchMovie() dengan string que "Stein Gate" lalu dilakukan
 *      checking null/empty pada List<MovieData>
 * 4)   Pengujian dilakukan dengan getLinkReleaseToday() dengan tanggal hari ini lalu dilakukan
 *      checking null/empty pada List<MovieData>
 */

@RunWith(RobolectricTestRunner::class) //this library used to make test easier for mock my repository
@Config(sdk = [Build.VERSION_CODES.P]) //https://stackoverflow.com/questions/56808485/robolectric-and-android-sdk-29
class MovieDataRepositoryTest {
    @Test
    fun getJSONData() {
        val repo = MovieDataRepository(null)
        var tmpString = repo.getJSONData(
            MovieDataRepository.getLinkTrendingMovie(
                "movie",
                "day",
                "en"
            )
        )
        assert(!(tmpString.isNullOrEmpty() || tmpString.isNullOrBlank()))

        tmpString = repo.getJSONData(
            MovieDataRepository.getLinkTrendingMovie(
                "tv",
                "day",
                "en"
            )
        )
        assert(!(tmpString.isNullOrEmpty() || tmpString.isNullOrBlank()))

        tmpString = repo.getJSONData(
            MovieDataRepository.getLinkSearchMovie("movie", "Stein Gate", "en")
        )
        assert(!(tmpString.isNullOrEmpty() || tmpString.isNullOrBlank()))

        tmpString = repo.getJSONData(
            MovieDataRepository.getLinkReleaseToday(
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)
            )
        )
        assert(!(tmpString.isNullOrEmpty() || tmpString.isNullOrBlank()))
    }

    @Test
    fun parse2MovieData() {
        val repo = MovieDataRepository(null)
        var s = repo.getJSONData(
            MovieDataRepository.getLinkTrendingMovie(
                "movie",
                "day",
                "en"
            )
        )
        var data = s?.let { repo.parse2MovieData(it) }
        assert(!data.isNullOrEmpty())


        s = repo.getJSONData(
            MovieDataRepository.getLinkTrendingMovie(
                "tv",
                "day",
                "en"
            )
        )
        data = s?.let { repo.parse2MovieData(it) }
        assert(!data.isNullOrEmpty())


        s = repo.getJSONData(
            MovieDataRepository.getLinkSearchMovie("movie", "Stein Gate", "en")
        )
        data = s?.let { repo.parse2MovieData(it) }

        assert(!data.isNullOrEmpty())
        s = repo.getJSONData(
            MovieDataRepository.getLinkReleaseToday(
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)
            )
        )
        data = s?.let { repo.parse2MovieData(it) }
        assert(!data.isNullOrEmpty())
    }
}