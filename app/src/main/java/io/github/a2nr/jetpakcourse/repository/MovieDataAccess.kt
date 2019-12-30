package io.github.a2nr.jetpakcourse.repository

import android.database.Cursor
import androidx.paging.DataSource
import androidx.room.*

@Dao
interface MovieDataAccess {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(listMovieData: List<MovieData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movieData: MovieData)

//    @Query("UPDATE " + MovieData.NAME
//            + " SET " + MovieData.IS_FAVORITE + "=1 " +
//            "WHERE id=:key")
//    fun insertFavorite(key: Int)

//    @Query(
//        "SELECT * FROM " + MovieData.NAME
//                + " WHERE " + MovieData.IS_FAVORITE + " = 1"
//    )
//    fun getFavorite(): DataSource.Factory<Int, MovieData>

//    @Query(
//        "DELETE from " + MovieData.NAME
//                + " WHERE id= :key AND " + MovieData.IS_FAVORITE + " = 1"
//    )
//    fun deleteFavorite(key: Int)

    @Query("DELETE from " + MovieData.NAME)
    fun delete()

    @Query("DELETE from " + MovieData.NAME
            + " WHERE id = :key")
    fun delete(key: Int)

    @Query(
        "SELECT * FROM " + MovieData.NAME
                + " WHERE id = :key"
//                + " AND " + MovieData.IS_FAVORITE + " = 1"
    )
    fun getMovieFromId(key: Int): MovieData?

    @Query(
        "SELECT * FROM " + MovieData.NAME
//                + " WHERE " + MovieData.IS_FAVORITE + " = 0"
                + " ORDER BY " + MovieData.ORDER_ID + " ASC"
    )
    fun getDataSource(): DataSource.Factory<Int, MovieData>

    @Query("SELECT * FROM " + MovieData.NAME
            + " WHERE " + MovieData.IS_FAVORITE + " = 1")
    fun getAllCursor(): Cursor?
}