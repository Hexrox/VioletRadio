package com.violetradio.app.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.violetradio.app.BuildConfig
import com.violetradio.app.data.remote.api.RadioBrowserApi
import com.violetradio.app.data.remote.api.SpotifyApi
import com.violetradio.app.data.remote.api.YouTubeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // === Common ===

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    // === Radio Browser ===

    @Provides
    @Singleton
    @Named("RadioBrowserOkHttp")
    fun provideRadioBrowserOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "VioletRadio/1.0")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRadioBrowserApi(
        @Named("RadioBrowserOkHttp") okHttpClient: OkHttpClient,
        gson: Gson
    ): RadioBrowserApi {
        return Retrofit.Builder()
            .baseUrl("https://de1.api.radio-browser.info/json/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(RadioBrowserApi::class.java)
    }

    // === Spotify ===

    @Provides
    @Singleton
    @Named("SpotifyOkHttp")
    fun provideSpotifyOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideSpotifyApi(
        @Named("SpotifyOkHttp") okHttpClient: OkHttpClient,
        gson: Gson
    ): SpotifyApi {
        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(SpotifyApi::class.java)
    }

    // === YouTube ===

    @Provides
    @Singleton
    @Named("YouTubeOkHttp")
    fun provideYouTubeOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideYouTubeApi(
        @Named("YouTubeOkHttp") okHttpClient: OkHttpClient,
        gson: Gson
    ): YouTubeApi {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(YouTubeApi::class.java)
    }
}
