package io.github.a2nr.jetpakcourse

import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import io.github.a2nr.jetpakcourse.adapter.ItemMovieViewHolder
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.utils.EspressoIdlingResource
import io.github.a2nr.jetpakcourse.utils.waitUntilVisible
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
 */

@LargeTest
@RunWith(AndroidJUnit4::class)
class ListMovieFragmentTestModul1 {
    /* https://github.com/android/testing-samples/blob/master/ui/espresso/BasicSample/app/src/androidTest/java/com/example/android/testing/espresso/BasicSample/ChangeTextBehaviorKtTest.kt
     * Katanya ActivityTestRule diganti ActivityScenarioRule
     * */
    @JvmField
    @Rule
    val activityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.esspressoTestIdlingResource)
    }

    @After
    fun tearsDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.esspressoTestIdlingResource)
    }

    @Test
    fun test_1_a() {
        onView(withId(R.id.progressBarDataReady)).check { view, noViewFoundException ->
            assert(view.isVisible)
        }
    }

    @Test
    fun test_1_b() {
        onView(withId(R.id.listMovie)).also { waitUntilVisible(withId(R.id.listMovie)) }
            .check(matches(isDisplayed()))
            .check { view, _ ->
                @Suppress("UNCHECKED_CAST")
                val adapter =
                    ((view as RecyclerView).adapter as PagedListAdapter<MovieData, ItemMovieViewHolder>)
                assert(adapter.currentList!!.toList().let { !it.isNullOrEmpty() })
            }
    }

    @Test
    fun test_1_c() {
        lateinit var adapter: PagedListAdapter<MovieData, ItemMovieViewHolder>
        for (i in 0..2) {
            onView(withId(R.id.listMovie)).also { waitUntilVisible(withId(R.id.listMovie)) }
                .check(matches(isDisplayed()))
                .check { view, _ ->
                    @Suppress("UNCHECKED_CAST")
                    adapter =
                        ((view as RecyclerView).adapter as PagedListAdapter<MovieData, ItemMovieViewHolder>)
                    assert(adapter.currentList!!.toList().let { !it.isNullOrEmpty() })
                }
                .perform(scrollToPosition<ItemMovieViewHolder>(i))
                .perform(actionOnItemAtPosition<ItemMovieViewHolder>(i, click()))
            onView(withText(adapter.currentList!![i]!!.title))
                .check(matches(isDisplayed()))
                .perform(pressBack())
        }
    }
}
