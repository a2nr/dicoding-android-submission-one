package io.github.a2nr.jetpakcourse


import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import io.github.a2nr.jetpakcourse.adapter.ItemMovieViewHolder
import io.github.a2nr.jetpakcourse.repository.MovieData
import io.github.a2nr.jetpakcourse.utils.EspressoIdlingResource
import io.github.a2nr.jetpakcourse.utils.childAtPosition
import io.github.a2nr.jetpakcourse.utils.waitUntilVisible
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/* Pengujian untuk modul satu
 * Before : Akan dilakukan pengambilan data terlebih dahulu untuk memverifikasi tampilan data
 * 2) Pengujian tampilan Tv Show
 *      a. Tunggu loading view menghilang pertanda data telah diterima
 *      b. click stack ke n dan periksa kesesuaian data yang diterima
 */

@LargeTest
@RunWith(AndroidJUnit4::class)
class ListMovieFragmentTestModul2 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.esspressoTestIdlingResource)
    }

    @After
    fun tearsDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.esspressoTestIdlingResource)
    }


    @Test
    fun test_2_a() {
        val textView = onView(
            allOf(
                withId(R.id.type_menu), withText("Movie"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Movie")))

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(300)

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.type_menu), withText("Movie"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val textView2 = onView(
            allOf(
                withId(R.id.title), withText("TV Show"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("TV Show")))

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(250)

        val appCompatTextView = onView(
            allOf(
                withId(R.id.title), withText("TV Show"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatTextView.perform(click())

        val textView3 = onView(
            allOf(
                withId(R.id.type_menu), withText("TV Show"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("TV Show")))

        lateinit var adapter: PagedListAdapter<MovieData, ItemMovieViewHolder>
        onView(withId(R.id.listMovie)).also { waitUntilVisible(withId(R.id.listMovie)) }
            .check(matches(isDisplayed()))
            .check { view, _ ->
                @Suppress("UNCHECKED_CAST")
                adapter =
                    ((view as RecyclerView).adapter as PagedListAdapter<MovieData, ItemMovieViewHolder>)
                assert(adapter.currentList!!.toList().let { !it.isNullOrEmpty() })
            }
        onView(withText(adapter.currentList!!.toList()[0].title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_2_b() {
        val textView = onView(
            allOf(
                withId(R.id.type_menu), withText("Movie"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Movie")))

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(300)

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.type_menu), withText("Movie"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val textView2 = onView(
            allOf(
                withId(R.id.title), withText("TV Show"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("TV Show")))

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(250)

        val appCompatTextView = onView(
            allOf(
                withId(R.id.title), withText("TV Show"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatTextView.perform(click())

        lateinit var adapter: PagedListAdapter<MovieData, ItemMovieViewHolder>
        for (i in 0..2) {
            val textView3 = onView(
                allOf(
                    withId(R.id.type_menu), withText("TV Show"),
                    childAtPosition(
                        childAtPosition(
                            withId(R.id.toolbar),
                            1
                        ),
                        0
                    ),
                    isDisplayed()
                )
            )
            textView3.check(matches(withText("TV Show")))

            onView(withId(R.id.listMovie)).also { waitUntilVisible(withId(R.id.listMovie)) }
                .check(matches(isDisplayed()))
                .check { view, _ ->
                    @Suppress("UNCHECKED_CAST")
                    adapter =
                        ((view as RecyclerView).adapter as PagedListAdapter<MovieData, ItemMovieViewHolder>)
                    assert(adapter.currentList!!.toList().let { !it.isNullOrEmpty() })
                }
                .perform(RecyclerViewActions.scrollToPosition<ItemMovieViewHolder>(i))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<ItemMovieViewHolder>(
                        i,
                        click()
                    )
                )
            onView(withText(adapter.currentList!![i]!!.title))
                .check(matches(isDisplayed()))
                .perform(ViewActions.pressBack())
        }
    }
}
