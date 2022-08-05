package com.example.cocktailsapp.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailsapp.repository.CocktailRepository
import com.example.cocktailsapp.util.NetworkResult
import com.example.cocktailsapp.repository.CacheCocktailRepository
import com.example.cocktailsapp.model.CacheCocktails
import com.example.cocktailsapp.model.Cocktails
import com.example.cocktailsapp.model.Drink
import com.example.cocktailsapp.model.mapToCache
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cocktailRepository: CocktailRepository,
    private val cacheCocktailRepository: CacheCocktailRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _cocktails: MutableLiveData<NetworkResult<Cocktails>> = MutableLiveData()
    val cocktails: LiveData<NetworkResult<Cocktails>> get() = _cocktails

    fun searchCocktails(letter: String) {
        safeCallCocktailsFromNetwork(letter)
    }

    suspend fun getCocktailsFromDB(letter: String) = cacheCocktailRepository.getCocktailsFromDatabase(letter)

    private fun safeCallCocktailsFromNetwork(letter: String) =
        // RUN SCOPE ON MAIN THREAD
        viewModelScope.launch {
            _cocktails.postValue(NetworkResult.Loading())
            try {
                //CHECK FOR NETWORK CONNECTIVITY
                if (hasInternetConnection()) {
                    // NETWORK CONNECTED : MAKE NETWORK CALL
                    var response = cocktailRepository.getCocktails(letter)
                    _cocktails.postValue(handleCocktailsResponse(response))
                    // SAVE RESULTS TO DATABASE IF DRINKS IS NOT NULL
                    response.body()?.let {
                        it.drinks?.let{
                            saveCocktailsToDB(response.body()!!.drinks)
                        } ?: let {
                            response = cocktailRepository.getCocktails(letter)
                            _cocktails.postValue(handleCocktailsResponse(response))
                        }
                    }
                } else {
                    //NO NETWORK : GET COCKTAILS FROM DATABASE
                    getCocktailsFromDB(letter)
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _cocktails.postValue(NetworkResult.Error("Network Failure"))
                    else -> _cocktails.postValue(NetworkResult.Error("Conversion Error"))
                }
            }
        }

    private fun saveCocktailsToDB(cocktails: List<Drink>?) = CoroutineScope(IO).launch {
        val convert: List<CacheCocktails>? = cocktails?.mapToCache()
        cacheCocktailRepository.insertCocktailsToDatabase(convert)
    }

        //CHECK IF CONNECTED TO INTERNET
        private fun hasInternetConnection(): Boolean {
            val connectivityManager = getApplication(context).getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
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



