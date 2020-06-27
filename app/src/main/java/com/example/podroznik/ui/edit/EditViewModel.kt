package com.example.podroznik.ui.edit

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.podroznik.AppState
import com.example.podroznik.ui.list.Place
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class EditViewModel : ViewModel() {

    lateinit var place: Place

    val created: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }
    val edited: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    fun createPlace() {

        val payload = JSONObject()
        payload.put("name", place.placeName)
        payload.put("note", place.placeNote)
        payload.put("diameter", place.placeDiameter)
        payload.put("lat", 0.0) // TODO
        payload.put("lon", 0.0) // TODO
        payload.put("photo", Base64.encodeToString(place.placePhoto, Base64.DEFAULT))
        val request = Request.Builder()
            .post(payload.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("cookie", AppState.getInstance().authHeader)
            .url("http://10.0.2.2:3000/places/create")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = println("############# CREATE ERROR" + e.message)
            override fun onResponse(call: Call, response: Response) {

                if (response.code < 200 || response.code > 299) return
                created.postValue(true)
            }
        })
    }

    fun updatePlace(name: String, note: String, diameter: Double, lon: Double, lat: Double, placePhoto: ByteArray) {

        val payload = JSONObject()
        payload.put("name", name)
        payload.put("note", note)
        payload.put("diameter", diameter)
        payload.put("lat", lat)
        payload.put("lon", lon)
        payload.put("photo", Base64.encodeToString(placePhoto, Base64.DEFAULT))
        val request = Request.Builder()
            .put(payload.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("cookie", AppState.getInstance().authHeader)
            .url("http://10.0.2.2:3000/places/update/${place.placeId}")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = println("############# UPDATE ERROR" + e.message)
            override fun onResponse(call: Call, response: Response){

                if (response.code < 200 || response.code > 299) return
                edited.postValue(true)
            }
        })
    }
}
