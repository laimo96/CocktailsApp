package com.example.cocktailsapp.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cocktailsapp.model.CacheCocktails

@Database(entities = [CacheCocktails::class], version = 1, exportSchema = false )
abstract class CocktailDatabase : RoomDatabase() {

    abstract fun cocktailsDAO(): CocktailsDAO

}