package com.example.cocktailsapp.di

import android.content.Context;
import androidx.room.Room
import com.example.cocktailsapp.cache.CocktailDatabase
import com.example.cocktailsapp.cache.CocktailsDAO


import dagger.Module

import dagger.Provides;
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun providesRoomDb(@ApplicationContext appContext:Context): CocktailDatabase = Room.databaseBuilder(
        appContext,
        CocktailDatabase::class.java,
        "cocktail-db"
    ).
    build()

    @Provides
    fun providesCocktailDAO(database: CocktailDatabase): CocktailsDAO =
        database.cocktailsDAO()

}