package com.grand.duke.elliot.madras.check.moviesearchapp.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_search")
class RecentSearch (
    @PrimaryKey val title: String,
    val time: Long
)