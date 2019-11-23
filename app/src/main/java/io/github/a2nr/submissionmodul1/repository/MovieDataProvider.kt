package io.github.a2nr.submissionmodul1.repository

import android.content.ContentProvider
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
        val MATCHER = UriMatcher(UriMatcher.NO_MATCH)
    }

    init {
        MATCHER.addURI(AUTHORITY, MovieData.NAME, FAVORITE)
    }

    override fun onCreate(): Boolean {
        return true;
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? =
        this.context?.let {
            val repo = MovieDataRepository(
                MovieDatabase
                    .getInstance(it).movieDao()
            )
            val cursor = when (MATCHER.match(uri)) {
                FAVORITE -> repo.movieDao.getAllCursor()
                else -> {
                    Log.e("ContentProvider", "URI not found!")
                    null
                }
            }
            cursor?.setNotificationUri(it.contentResolver, uri)
            cursor
        }

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
}
