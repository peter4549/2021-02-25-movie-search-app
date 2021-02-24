package com.grand.duke.elliot.madras.check.moviesearchapp.network

data class Movie (val items: List<MovieItem>, val total: Int)

data class MovieItem(
    val title: String,
    val link: String,
    val image: String,
    val pubDate: String,
    val userRating: String
)