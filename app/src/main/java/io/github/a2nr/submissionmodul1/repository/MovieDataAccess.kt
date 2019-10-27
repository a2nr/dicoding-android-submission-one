package io.github.a2nr.submissionmodul1.repository

import androidx.room.*

@Dao
interface MovieDataAccess{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movieData: MovieData)

    @Delete
    fun delete(movieData: MovieData)

    @Query("SELECT id FROM movie WHERE id = :key")
    fun getIdfromId(key : Int) : Int?

    @Query("SELECT * FROM movie")
    fun getAll() : List<MovieData>
}