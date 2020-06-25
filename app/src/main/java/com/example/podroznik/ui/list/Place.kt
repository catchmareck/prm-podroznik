package com.example.podroznik.ui.list

import org.json.JSONArray
import java.io.Serializable

class Place(var placeId: Int, var placeName: String, var placeNote: String, var placeDiameter: Double, placePhotoBytes: JSONArray) : Serializable {

    var placePhoto: ByteArray = ByteArray(placePhotoBytes.length())

    init {
        for (byte in 0 until placePhotoBytes.length()) {
            placePhoto[byte] = (placePhotoBytes[byte] as Int).toByte()
        }
     }
}
