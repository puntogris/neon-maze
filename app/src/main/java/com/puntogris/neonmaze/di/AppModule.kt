package com.puntogris.neonmaze.di

import com.puntogris.neonmaze.data.Repository
import com.puntogris.neonmaze.data.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideRepository(): Repository = RepositoryImpl()
}