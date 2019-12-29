package io.github.a2nr.jetpakcourse

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import io.github.a2nr.jetpakcourse.adapter.ItemMovieViewHolder
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.repository.MovieDataRepository
import io.github.a2nr.jetpakcourse.utils.EspressoIdlingResource
import io.github.a2nr.jetpakcourse.utils.RecyclerViewItemCheck
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/* Pengujian untuk modul satu
 * Before : Akan dilakukan pengambilan data terlebih dahulu untuk memverifikasi tampilan data
 * 1) Pengujian tampilan Movie
 *      a. Check loading view tampil
 *      b. Tunggu loading view menghilang pertanda data telah diterima
 *      c. click stack ke n dan periksa kesesuaian data yang diterima
 * 2) Pengujian tampilan Tv Show
 *      a. Tunggu loading view menghilang pertanda data telah diterima
 *      b. click stack ke n dan periksa kesesuaian data yang diterima
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class ListMovieFragmentTestModul1 {
    /* https://github.com/android/testing-samples/blob/master/ui/espresso/BasicSample/app/src/androidTest/java/com/example/android/testing/espresso/BasicSample/ChangeTextBehaviorKtTest.kt
     * Katanya ActivityTestRule diganti ActivityScenarioRule
     * */
    @get:Rule
    val activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repo = MovieDataRepository(null)
    private val listType = arrayListOf(MovieDataRepository.MOVIE, MovieDataRepository.TV)
    private var listMovieData: List<MovieData>? = null
    private var listTvData: List<MovieData>? = null

    private fun getData() {
        listType.forEach {
            repo.fetchData(
                MovieDataRepository.getLinkTrendingMovie(
                    it,
                    InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(R.string.lang_code)
                )
            ) { _, _, data ->
                data?.let {
                    when (it[0].mediaType) {
                        listType[0] -> listMovieData = it
                        listType[1] -> listTvData = it
                        else -> Log.i("[TEST]", "What?")
                    }
                }
            }
        }
    }

    @Before
    fun setUp() {
        getData()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.esspressoTestIdlingResource)
    }

    @After
    fun tearsDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.esspressoTestIdlingResource)
    }

    @Test
    fun test_1_a() {
        onView(withId(R.id.progressBarDataReady)).check { view, _ ->
            assert(view.isVisible)
        }
    }

    @Test
    fun test_1_b() {
        onView(withId(R.id.progressBarDataReady)).inRoot(RootMatchers.isFocusable()).check { view, _ ->
            assert(view.isVisible)
        }
        onView(withId(R.id.listMovie)).check{ view, noViewFoundException ->
            Log.i("Chect listMovie","$noViewFoundException")
            val recyclerView = view as RecyclerView
            assert(recyclerView.adapter?.itemCount == listMovieData!!.size)
        }
//        onView(withId(R.id.listMovie)).RecyclerViewItemCheck(listMovieData!!.size))
        onView(withText(listMovieData?.get(0)?.title)).check(matches(isDisplayed()))

    }

    @Test
    fun test_1_c() {
        val MAX_SCROLL = 1
        for (i in 0..MAX_SCROLL) {
            onView(withId(R.id.listMovie))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<ItemMovieViewHolder>(
                        i,
                        click()
                    )
                )
            onView(withText(listMovieData?.get(i)?.title))
                .check(matches(isDisplayed()))
                .perform(pressBack())
        }
    }

    @Test
    fun test_2_a() {
        onView(withId(R.id.type_menu)).perform(click())
        onView(withText("TV Show")).perform(click())
        onView(withId(R.id.listMovie)).check(RecyclerViewItemCheck(listTvData!!.size))
        onView(withText(listTvData?.get(0)?.title)).check(matches(isDisplayed()))

    }

    @Test
    fun test_2_b() {
        val MAX_SCROLL = 1
        onView(withId(R.id.type_menu)).perform(click())
        onView(withText("TV Show")).perform(click())
        for (i in 0..MAX_SCROLL) {
            onView(withId(R.id.listMovie))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<ItemMovieViewHolder>(
                        i,
                        click()
                    )
                )
            onView(withText(listTvData?.get(i)?.title))
                .check(matches(isDisplayed()))
                .perform(pressBack())
        }
    }
}