package com.example.cocktailsapp.repository

import com.example.cocktailsapp.api.CocktailServiceApi
import com.example.cocktailsapp.model.Cocktails
import com.example.cocktailsapp.util.NetworkResult
import retrofit2.Response
import javax.inject.Inject

interface CocktailRepository {
   suspend fun getCocktails(letter:String): NetworkResult<Cocktails>
}

class CocktailRepositoryImpl @Inject constructor (
   private val cocktailsServiceApi: CocktailServiceApi
) : CocktailRepository{

   override suspend fun getCocktails(letter:String): NetworkResult<Cocktails> {
      val response = cocktailsServiceApi.getCocktails(letter)
      return handleCocktailsResponse(response)
   }

   //CONVERT RETROFIT RESPONSE OBJECT TO GENERIC DATA HANDLER (NETWORK RESULT)
   private fun handleCocktailsResponse(response: Response<Cocktails>): NetworkResult<Cocktails> {
      if (response.isSuccessful) {
         response.body()?.let { responseResult ->
            return NetworkResult.Success(responseResult)
         }
      }
      return NetworkResult.Error(response.message())
   }
}