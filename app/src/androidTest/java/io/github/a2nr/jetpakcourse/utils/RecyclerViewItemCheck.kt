package io.github.a2nr.jetpakcourse.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import junit.framework.TestCase.assertNotNull

class RecyclerViewItemCheck(val size: Int) : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        assertNotNull(adapter)
        assert(adapter!!.itemCount == size)
    }


}