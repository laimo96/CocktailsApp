package com.example.cocktailsapp.di

import com.example.cocktailsapp.repository.CacheCocktailRepository
import com.example.cocktailsapp.repository.CacheCocktailRepositoryImpl
import com.example.cocktailsapp.repository.CocktailRepository
import com.example.cocktailsapp.repository.CocktailRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideRepository( cocktailRepositoryImpl: CocktailRepositoryImpl): CocktailRepository
    @Binds
    abstract fun provideCacheRepository( cacheRepositoryImpl: CacheCocktailRepositoryImpl): CacheCocktailRepository


}