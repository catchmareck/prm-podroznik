package com.example.podroznik.ui.list

import androidx.lifecycle.ViewModel

class ListViewModel : ViewModel() {

    private val places: ArrayList<String> = arrayListOf(
        "Polska",
        "Niemcy",
        "Rosja"
    )

    fun getPlaces(): List<String> {
        return places
    }
}
