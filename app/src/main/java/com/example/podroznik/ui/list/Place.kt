package com.example.podroznik.ui.list

import org.json.JSONArray

class Place(var placeId: Int, var placeName: String, var placeDiameter: Double, placePhotoBytes: JSONArray) {

    var placePhoto: ByteArray = ByteArray(placePhotoBytes.length())

    init {
        for (byte in 0 until placePhotoBytes.length()) {
            placePhoto[byte] = (placePhotoBytes[byte] as Int).toByte()
        }
     }
}
