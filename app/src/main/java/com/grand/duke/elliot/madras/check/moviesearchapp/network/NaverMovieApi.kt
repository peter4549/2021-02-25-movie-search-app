package com.grand.duke.elliot.madras.check.moviesearchapp.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

@Suppress("SpellCheckingInspection")
object NaverMovieApi {
    var start = 1
    const val display = 10

    private const val BASE_URL = "https://openapi.naver.com"
    private const val CLIENT_ID = "GskW74bAxEzk0HOOe9V8"
    private const val CLIENT_SECRET = "YTvPOFVRdF"

    private fun createRetrofit(): Retrofit {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(builder.build())
            .build()
    }

    interface MovieService {
        @GET("/v1/search/movie.json")
        fun getMovieAsync (
            @Header("X-Naver-Client-Id") clientId: String = CLIENT_ID,
            @Header("X-Naver-Client-Secret") clientSecret: String = CLIENT_SECRET,
            @Query("query") title: String,
            @Query("display") display: Int = NaverMovieApi.display,
            @Query("start") start: Int
        ): Deferred<Movie>
    }

    fun movieService(): MovieService =
        createRetrofit().create(MovieService::class.java)
}