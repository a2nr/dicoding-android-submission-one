package io.github.a2nr.jetpakcourse.repository

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import io.github.a2nr.jetpakcourse.widgetapp.StackImageAppWidgetProvider

class MovieDataProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "io.github.a2nr.smm5"
        const val SCHEME = "content"
        const val FAVORITE = 1
        const val FAVORITE_ID = 2
        val MATCHER = UriMatcher(UriMatcher.NO_MATCH)
    }

    init {
        MATCHER.addURI(AUTHORITY, MovieData.NAME, FAVORITE)
        MATCHER.addURI(AUTHORITY, "${MovieData.NAME}/#", FAVORITE_ID)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? =
        this.context?.let {
            val dataRepository = MovieDataRepository(
                MovieDatabase
                    .getInstance(it).movieDao()
            )
            val cursor = when (MATCHER.match(uri)) {
                FAVORITE -> {
                    val data = dataRepository.favoriteColector(
                        dataRepository.dao.getFavorite()
                    )
                    dataRepository.dao.delete()
                    dataRepository.dao.insert(data)
                    dataRepository.dao.getAllCursor()
                }
                else -> {
                    Log.e("ContentProvider", "URI not found!")
                    null
                }
            }
            cursor?.setNotificationUri(it.contentResolver, uri)
            cursor
        }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return this.context?.let {
            val id = -1L
            when (MATCHER.match(uri)) {
                FAVORITE_ID -> {
                    Log.i("Insert", values?.keySet().toString())
                }

            }
            ContentUris.withAppendedId(uri, id)
        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        var count = 0
        this.context?.let { context ->
            val dao = MovieDatabase.getInstance(context).movieDao()
            val pathId = MATCHER.match(uri)
            when (pathId) {
                FAVORITE_ID -> {
                    count++
                    uri.lastPathSegment?.let {
                        dao.deleteFavorite(it.toInt())
                        context.contentResolver.notifyChange(uri, null)
                        StackImageAppWidgetProvider.sendRefresh(context)
                    }

                }
                else -> {
                    Log.e("MovieDataProvide", "error uri")
                }
            }
        }
        return count
    }
}
