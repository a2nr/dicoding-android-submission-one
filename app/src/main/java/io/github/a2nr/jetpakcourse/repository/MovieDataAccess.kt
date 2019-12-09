package io.github.a2nr.jetpakcourse.repository

import android.database.Cursor
import androidx.room.*

@Dao
interface MovieDataAccess {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movieData: MovieData)

    @Delete
    fun delete(movieData: MovieData)

    @Query("DELETE from " + MovieData.NAME + " WHERE id = :key")
    fun delete(key: Int)

    @Query("SELECT id FROM " + MovieData.NAME + " WHERE id = :key")
    fun getIdfromId(key: Int): Int?

    @Query("SELECT * FROM " + MovieData.NAME)
    fun getAll(): List<MovieData>

    @Query("SELECT * FROM " + MovieData.NAME)
    fun getAllCursor(): Cursor?
}