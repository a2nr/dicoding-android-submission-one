package io.github.a2nr.jetpakcourse.repository

import android.database.Cursor
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDataAccess {
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = MovieData::class)
    fun insert(listMovieData: List<MovieData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = MovieData::class)
    fun insert(movieData: MovieData)

    @Query("DELETE from " + MovieData.NAME)
    fun delete()

    @Query(
        "DELETE from " + MovieData.NAME
                + " WHERE id = :key"
    )
    fun delete(key: Int)

    @Query(
        "SELECT * FROM " + MovieData.NAME
                + " WHERE id = :key"
    )
    fun getMovieFromId(key: Int): MovieData?


    @Query(
        "SELECT * FROM " + MovieData.NAME
                + " ORDER BY " + MovieData.ORDER_ID + " ASC"
    )
    fun getAllData(): List<MovieData>

    @Query(
        "SELECT * FROM " + MovieData.NAME
                + " ORDER BY " + MovieData.ORDER_ID + " ASC"
    )
    fun getDataSource(): DataSource.Factory<Int, MovieData>

    @Query("SELECT * FROM " + MovieData.NAME)
    fun getAllCursor(): Cursor?

    @Insert(entity = FavoriteMovieData::class)
    fun insertFavorite(fav: FavoriteMovieData)

    @Query("SELECT * FROM " + FavoriteMovieData.NAME)
    fun getFavorite(): List<FavoriteMovieData>

    @Query(
        "SELECT * FROM " + FavoriteMovieData.NAME
                + " WHERE " + FavoriteMovieData.ID_FAVORITE + " = :key"
    )
    fun getFavorite(key: Int): FavoriteMovieData?

    @Query(
        "DELETE from " + FavoriteMovieData.NAME
                + " WHERE " + FavoriteMovieData.ID_FAVORITE + " = :key"
    )
    fun deleteFavorite(key: Int)
}
