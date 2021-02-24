package com.grand.duke.elliot.madras.check.moviesearchapp.persistence

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_search ORDER BY time ASC")
    fun getAll(): LiveData<List<RecentSearch>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recentSearch: RecentSearch)

    @Delete
    suspend fun delete(recentSearch: RecentSearch)
}