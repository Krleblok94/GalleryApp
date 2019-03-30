package com.example.galleryapp.di

import android.app.Activity
import android.app.Application
import androidx.room.Room
import com.example.galleryapp.BuildConfig
import com.example.galleryapp.repository.PhotoRepository
import com.example.galleryapp.repository.database.AppDatabase
import com.example.galleryapp.repository.network.ApiService
import com.example.galleryapp.ui.PhotoAdapter
import com.example.galleryapp.ui.PhotosViewModel
import com.example.galleryapp.util.AppExecutors
import com.example.galleryapp.util.Constants
import com.example.galleryapp.util.GlideApp
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single { provideAppDatabase(androidApplication()) }
    single { provideApiService() }
    single { AppExecutors() }
    single { PhotoRepository(get(), get(), get()) }

    viewModel { PhotosViewModel(get()) }

    factory { (activity: Activity) -> PhotoAdapter(GlideApp.with(activity)) }
    factory { (activity: Activity) -> GlideApp.with(activity) }
}

private fun provideApiService() : ApiService {
    val okHttpClient = OkHttpClient.Builder()
    okHttpClient.connectTimeout(Constants.TIMEOUT_IN_SEC, TimeUnit.SECONDS)
    okHttpClient.readTimeout(Constants.TIMEOUT_IN_SEC, TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClient.addInterceptor(httpLoggingInterceptor)
    }

    val gson = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient.build())
        .build()

    return retrofit.create(ApiService::class.java)
}

private fun provideAppDatabase(application: Application) : AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, "app_database.db")
        .fallbackToDestructiveMigration().build()
}