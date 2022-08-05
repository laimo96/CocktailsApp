package com.example.cocktailsapp.repository

import com.example.cocktailsapp.api.CocktailServiceApi
import com.example.cocktailsapp.model.Cocktails
import retrofit2.Response
import javax.inject.Inject

interface CocktailRepository {
   suspend fun getCocktails(letter:String): Response<Cocktails>
}

class CocktailRepositoryImpl @Inject constructor (
   private val cocktailsServiceApi: CocktailServiceApi
) : CocktailRepository {

   override suspend fun getCocktails(letter:String): Response<Cocktails> {
      return cocktailsServiceApi.getCocktails(letter)
   }

}