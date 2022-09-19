package com.example.cocktailsapp.repository.mocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cocktailsapp.model.CacheCocktails
import com.example.cocktailsapp.repository.CacheCocktailRepository

class FakeCacheRepo: CacheCocktailRepository {

    private val _cocktails = mutableListOf<CacheCocktails>()
    private val obsCocktails = MutableLiveData<List<CacheCocktails>>(_cocktails)

    override suspend fun insertCocktailsToDatabase(cocktails: List<CacheCocktails>?) {
        if (cocktails != null) {
            _cocktails.addAll(cocktails)
        }
    }

    override suspend fun getCocktailsFromDatabase(letter: String): LiveData<List<CacheCocktails>> {
        return obsCocktails.apply {_cocktails.find { it.id == letter}}
    }


}