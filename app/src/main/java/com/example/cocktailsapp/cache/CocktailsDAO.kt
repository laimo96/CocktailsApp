package com.example.cocktailsapp.cache

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cocktailsapp.model.CacheCocktails

@Dao
interface CocktailsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCocktails(cocktails: List<CacheCocktails>?)

    @Update
    fun updateCocktail(cocktails: CacheCocktails)

    @Query("SELECT * FROM cocktails_table ORDER BY strDrink ASC")
    fun readAll(): List<CacheCocktails>

    @Query("SELECT * FROM cocktails_table WHERE strDrink LIKE  :letter || '%' ")
    fun getCocktails(letter: String): LiveData<List<CacheCocktails>>
}