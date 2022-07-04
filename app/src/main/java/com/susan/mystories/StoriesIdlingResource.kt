package com.susan.mystories

import androidx.test.espresso.idling.CountingIdlingResource

inline fun <T> setStoriesIdlingResource(function: () -> T): T {
    StoriesIdlingResource.tambahStories()
    return try {
        function()
    } finally {
        StoriesIdlingResource.kurangStories()
    }
}

object StoriesIdlingResource {
    private const val STORIES_RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(STORIES_RESOURCE)

    fun tambahStories() {
        countingIdlingResource.increment()
    }

    fun kurangStories() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}