package com.example.podroznik.ui.edit

import androidx.lifecycle.ViewModel
import com.example.podroznik.ui.list.Place

class EditViewModel : ViewModel() {

    lateinit var place: Place

    fun createPlace() {
        // TODO save this place
    }

    fun updatePlace(name: String, note: String, diameter: Double, lon: Double, lat: Double, placePhoto: ByteArray) {
        // TODO update existing place with the new data
    }
}
