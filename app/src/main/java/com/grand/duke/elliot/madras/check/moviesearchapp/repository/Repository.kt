package com.grand.duke.elliot.madras.check.moviesearchapp.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.grand.duke.elliot.madras.check.moviesearchapp.network.Movie
import com.grand.duke.elliot.madras.check.moviesearchapp.network.MovieItem
import com.grand.duke.elliot.madras.check.moviesearchapp.network.NaverMovieApi
import com.grand.duke.elliot.madras.check.moviesearchapp.persistence.AppDatabase
import com.grand.duke.elliot.madras.check.moviesearchapp.persistence.RecentSearch
import kotlinx.coroutines.*
import retrofit2.HttpException
import timber.log.Timber

class Repository(appDatabase: AppDatabase) {
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private val recentSearchDao = appDatabase.recentSearchDao()

    fun getMovies(title: String, start: Int, @MainThread onMovie: suspend (Movie) -> Unit) {
        coroutineScope.launch {
            try {
                val movieAsync = NaverMovieApi.movieService().getMovieAsync(title = title, start = start)
                val movie = movieAsync.await()

                withContext(Dispatchers.Main) {
                    onMovie.invoke(movie)
                }
            } catch (e: HttpException) {
                Timber.e(e, "Failed to get search results: ${e.message}.")
            }
        }
    }

    fun getRecentSearches(): LiveData<List<RecentSearch>> = recentSearchDao.getAll()

    fun insertRecentSearch(recentSearch: RecentSearch) {
        coroutineScope.launch {
            recentSearchDao.insert(recentSearch)
        }
    }

    fun deleteRecentSearch(recentSearch: RecentSearch) {
        coroutineScope.launch {
            recentSearchDao.delete(recentSearch)
        }
    }
}