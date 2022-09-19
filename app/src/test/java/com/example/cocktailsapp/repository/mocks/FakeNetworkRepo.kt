package com.example.cocktailsapp.repository.mocks

import com.example.cocktailsapp.model.Cocktails
import com.example.cocktailsapp.repository.CocktailRepository
import com.example.cocktailsapp.util.NetworkResult

class FakeNetworkRepo: CocktailRepository {

    private var networkError = false

    fun returnNetworkError(value: Boolean){
        networkError = value
    }

    override suspend fun getCocktails(letter: String): NetworkResult<Cocktails> {
        return NetworkResult.Success(Cocktails(listOf()))
    }


}