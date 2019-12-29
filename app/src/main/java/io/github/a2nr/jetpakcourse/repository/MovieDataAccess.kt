package io.github.a2nr.jetpakcourse.repository

import android.database.Cursor
import androidx.paging.DataSource
import androidx.room.*

@Dao
interface MovieDataAccess {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movieData: MovieData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(listMovieData: List<MovieData>)

    @Query("UPDATE " + MovieData.NAME + " SET " + MovieData.IS_FAVORITE + " = :value WHERE id = :key")
    fun updateFavorite(value: Boolean, key: Int)

    @Query("SELECT * FROM " + MovieData.NAME + " WHERE " + MovieData.IS_FAVORITE + " = 1")
    fun getFavorite(): List<MovieData>

    @Delete
    fun delete(movieData: MovieData)

    @Query("DELETE from " + MovieData.NAME + " WHERE " + MovieData.IS_FAVORITE + " = 0")
    fun delete()

    @Query("DELETE from " + MovieData.NAME + " WHERE id = :key")
    fun delete(key: Int)

    @Query("SELECT id FROM " + MovieData.NAME + " WHERE id = :key")
    fun getIdFromId(key: Int): Int?

    @Query("SELECT * FROM " + MovieData.NAME + " WHERE id = :key")
    fun getDataFromId(key: Int): MovieData?


    @Query("SELECT * FROM " + MovieData.NAME)
    fun getAll(): List<MovieData>

    @Query("SELECT * FROM " + MovieData.NAME + " ORDER BY "+ MovieData.VOTE_AVERAGE +" DESC")
    fun getDataSource(): DataSource.Factory<Int, MovieData>

    @Query("SELECT * FROM " + MovieData.NAME)
    fun getAllCursor(): Cursor?
}