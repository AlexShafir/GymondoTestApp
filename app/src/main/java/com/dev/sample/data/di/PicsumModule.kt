package com.dev.sample.data.di

import com.dev.sample.data.remote.PicsumApi
import com.dev.sample.data.repository.IPicsumRepository
import com.dev.sample.data.repository.PicsumRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PicsumModule {

    companion object {
        @Singleton
        @Provides
        fun providePicsumApi(): PicsumApi {
            return Retrofit.Builder()
                .baseUrl("https://picsum.photos/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PicsumApi::class.java)
        }
    }

    @Binds
    fun bindPicsumRepository(repo: PicsumRepository): IPicsumRepository

}