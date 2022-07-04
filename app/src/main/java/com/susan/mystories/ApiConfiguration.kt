package com.susan.mystories

import androidx.viewbinding.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object ApiConfiguration {
    @Provides
    fun apiStories(retrofit: Retrofit): ApiStories {
        return retrofit.create(ApiStories::class.java)
    }
    @Provides
    fun retrofitStories(): Retrofit {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(com.susan.mystories.BuildConfig.URL_STORIES_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}