package io.github.a2nr.jetpakcourse.repository

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log

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
            val dao = MovieDataRepository(
                MovieDatabase
                    .getInstance(it).movieDao()
            )
            val cursor = when (MATCHER.match(uri)) {
                FAVORITE -> dao.dao.getAllCursor()
                else -> {
                    Log.e("ContentProvider", "URI not found!")
                    null
                }
            }
            cursor?.setNotificationUri(it.contentResolver, uri)
            cursor
        }

    //TODO create API to fetch new release for remainder feature
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
            val path_id = MATCHER.match(uri)
            when (path_id) {
                FAVORITE_ID -> {
                    count++
                    uri.lastPathSegment?.let {
                        dao.delete(it.toInt())
                        context.contentResolver.notifyChange(uri, null)
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
