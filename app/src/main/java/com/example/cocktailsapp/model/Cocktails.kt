package com.example.cocktailsapp.model


import com.google.gson.annotations.SerializedName


data class Cocktails(
    @SerializedName("drinks")
    val drinks: List<Drink>?
)