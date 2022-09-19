package com.example.cocktailsapp.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.support.annotation.VisibleForTesting
import android.widget.Toast
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
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cocktailRepository: CocktailRepository,
    private val cacheCocktailRepository: CacheCocktailRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    @VisibleForTesting
    private val _cocktails: MutableLiveData<NetworkResult<Cocktails>> = MutableLiveData()
    val cocktails: LiveData<NetworkResult<Cocktails>> get() = _cocktails

    fun searchCocktails(letter: String) {
        safeCallCocktailsFromNetwork(letter)
    }

    suspend fun getCocktailsFromDB(letter: String) =
        cacheCocktailRepository.getCocktailsFromDatabase(letter)

    private fun safeCallCocktailsFromNetwork(letter: String) =
        // RUN SCOPE ON MAIN THREAD
        viewModelScope.launch {
            _cocktails.postValue(NetworkResult.Loading())
            try {
                // NETWORK CONNECTED : MAKE NETWORK CALL
                var response = cocktailRepository.getCocktails(letter)
                _cocktails.postValue(response)
                // SAVE RESULTS TO DATABASE IF DRINKS IS NOT NULL
                response.data?.drinks?.let {
                    saveCocktailsToDataBase(it)
                } ?: let {
                    Toast.makeText(context, "No results", Toast.LENGTH_SHORT).show()
                    response = cocktailRepository.getCocktails("A")
                    _cocktails.postValue(response)
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _cocktails.postValue(NetworkResult.Error("Network Failure"))
                    else -> _cocktails.postValue(NetworkResult.Error("Conversion Error"))
                }
            }
        }

    private fun saveCocktailsToDataBase(cocktails: List<Drink>?) = viewModelScope.launch {
        withContext(IO) {
            val convert = cocktails?.mapToCache()
            cacheCocktailRepository.insertCocktailsToDatabase(convert)
        }
    }

}


