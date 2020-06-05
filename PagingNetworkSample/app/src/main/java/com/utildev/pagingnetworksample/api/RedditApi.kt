package com.utildev.pagingnetworksample.api

import com.utildev.pagingnetworksample.model.ListingResponse
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApi {
    companion object {
        private const val BASE_URL = "https://www.reddit.com/"

        fun create(): RedditApi = create(BASE_URL.toHttpUrlOrNull()!!)

        fun create(httpUrl: HttpUrl): RedditApi {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RedditApi::class.java)
        }
    }

    @GET("/r/{subreddit}/hot.json")
    fun getTop(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int
    ): Call<ListingResponse>

    // for after/before param, either get from RedditDataResponse.after/before,
    // or pass RedditNewsDataResponse.name (though this is technically incorrect)
    @GET("/r/{subreddit}/hot.json")
    fun getTopAfter(
        @Path("subreddit") subreddit: String,
        @Query("after") after: String,
        @Query("limit") limit: Int
    ): Call<ListingResponse>

    @GET("/r/{subreddit}/hot.json")
    fun getTopBefore(
        @Path("subreddit") subreddit: String,
        @Query("before") before: String,
        @Query("limit") limit: Int
    ): Call<ListingResponse>
}