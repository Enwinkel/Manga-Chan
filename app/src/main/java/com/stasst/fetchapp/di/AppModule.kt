package com.stasst.fetchapp.di

import com.stasst.fetchapp.data.MangaRepository
import com.stasst.fetchapp.data.MangaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMangaRepository(): MangaRepository {
        return MangaRepositoryImpl()
    }
}