package io.github.a2nr.jetpakcourse.utils

import android.util.Log
import androidx.test.espresso.idling.CountingIdlingResource

class EspressoIdlingResource {
    companion object {
        private val RESOURCE = "GLOBAL"
        val esspressoTestIdlingResource = CountingIdlingResource(RESOURCE)
        fun increment() {
            Log.i(
                "increment cok!",
                Log.getStackTraceString(Exception("$esspressoTestIdlingResource"))
            )
            esspressoTestIdlingResource.increment()
        }

        fun decrement() {
            with(esspressoTestIdlingResource.isIdleNow) {
                if (!this) {
                    Log.i(
                        "decrement cok!",
                        Log.getStackTraceString(Exception("$esspressoTestIdlingResource"))
                    )
                    esspressoTestIdlingResource.decrement()
                }
            }
        }
    }
}
