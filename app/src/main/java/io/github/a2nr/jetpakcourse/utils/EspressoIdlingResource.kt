package io.github.a2nr.jetpakcourse.utils

import androidx.test.espresso.idling.CountingIdlingResource

class EspressoIdlingResource {
    companion object {
        private const val RESOURCE = "GLOBAL"
        val esspressoTestIdlingResource = CountingIdlingResource(RESOURCE)
        fun increment() = esspressoTestIdlingResource.increment()
        fun decrement() = with(esspressoTestIdlingResource.isIdleNow) {
            if (!this) esspressoTestIdlingResource.decrement()
        }
    }
}
