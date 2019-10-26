package io.github.a2nr.submissionmodul1.repository

import androidx.room.*

@Dao
interface MovieDataAccess{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movieData: MovieData)

    @Query("SELECT * FROM movie")
    fun getAll() : List<MovieData>
}