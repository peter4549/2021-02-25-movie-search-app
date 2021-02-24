package com.grand.duke.elliot.madras.check.moviesearchapp.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grand.duke.elliot.madras.check.moviesearchapp.network.MovieItem
import com.grand.duke.elliot.madras.check.moviesearchapp.network.NaverMovieApi
import com.grand.duke.elliot.madras.check.moviesearchapp.persistence.AppDatabase
import com.grand.duke.elliot.madras.check.moviesearchapp.persistence.RecentSearch
import com.grand.duke.elliot.madras.check.moviesearchapp.repository.Repository

const val BLANK = ""

class MainViewModel(application: Application): ViewModel() {

    private val repository = Repository(AppDatabase.getInstance(application))

    val recentSearches: LiveData<List<RecentSearch>> = repository.getRecentSearches()

    private var rowCount = 0

    private val _movies = MutableLiveData<List<MovieItem>>()
    val movies: LiveData<List<MovieItem>>
       get() = _movies

    private var _currentTitle = BLANK
    val currentTitle: String
        get() = _currentTitle

    private var _total = -1
    val total: Int
        get() = _total

    fun setRowCount(rowCount: Int) {
        this.rowCount = rowCount
    }

    fun searchMovies(title: String) {
        if (title == _currentTitle)
            return

        _total = -1
        _movies.value = listOf()  // Clear adapter.
        _currentTitle = title
        NaverMovieApi.start = 1

        if (rowCount > 9) {
            recentSearches.value?.first()?.also {
                deleteRecentSearch(it)
            }
        }

        insertRecentSearch(RecentSearch(_currentTitle, System.currentTimeMillis()))

        repository.getMovies(_currentTitle, NaverMovieApi.start) {
            _total = it.total
            setMovies(it.items)
        }
    }

    fun searchAdditionalMovies(itemCount: Int) {
        if (_currentTitle.isBlank())
            return

        NaverMovieApi.start += itemCount

        repository.getMovies(_currentTitle, NaverMovieApi.start) {
            addMovies(it.items)
        }
    }

    private fun insertRecentSearch(recentSearch: RecentSearch) {
        repository.insertRecentSearch(recentSearch)
    }

    private fun deleteRecentSearch(recentSearch: RecentSearch) {
        repository.deleteRecentSearch(recentSearch)
    }

    private fun setMovies(movies: List<MovieItem>) {
        _movies.value = movies
    }

    private fun addMovies(movies: List<MovieItem>) {
        val value = _movies.value?.toMutableList() ?: mutableListOf()
        value.addAll(movies)
        _movies.value = value
    }
}