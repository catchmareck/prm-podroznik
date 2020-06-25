package com.example.podroznik.ui.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.podroznik.AppState
import com.example.podroznik.ui.list.Place
import okhttp3.*
import java.io.IOException

class DetailsViewModel : ViewModel() {

    lateinit var place: Place

    val deleted: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    fun deletePlace() {

        val request = Request.Builder()
            .delete()
            .addHeader("cookie", AppState.getInstance().authHeader)
            .url("http://10.0.2.2:3000/places/delete/${place.placeId}")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = println("############# PLACES ERROR" + e.message)
            override fun onResponse(call: Call, response: Response){

                if (response.code < 200 || response.code > 299) return
                deleted.postValue(true)
            }
        })
    }
}
