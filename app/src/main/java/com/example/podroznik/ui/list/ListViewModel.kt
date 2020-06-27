package com.example.podroznik.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.podroznik.AppState
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ListViewModel : ViewModel() {

    val data: MutableLiveData<MutableList<Place>> by lazy {
        MutableLiveData(mutableListOf(Place(0, "", "", 0.0, 0.0, 0.0, JSONArray("[]"))))
    }

    fun getPlaces(): MutableLiveData<MutableList<Place>> {
        return data
    }

    fun JSONArray.toMutableList(): MutableList<Any> = MutableList(length(), this::get)

    fun loadPlaces() {

        val request = Request.Builder()
            .get()
            .addHeader("cookie", AppState.getInstance().authHeader)
            .url("http://10.0.2.2:3000/places/read")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = println("############# PLACES ERROR" + e.message)
            override fun onResponse(call: Call, response: Response){
                val res = response.body!!.string()

                if (response.code < 200 || response.code > 299) return

                val places = JSONArray(res)
                val result = mutableListOf<Place>()
                for (place in places.toMutableList()) {
                    val json: JSONObject = place as JSONObject
                    val p = Place(
                        json.getInt("placeId"),
                        json.getString("placeName"),
                        json.getString("placeNote"),
                        json.getDouble("placeDiameter"),
                        json.getDouble("placeLat"),
                        json.getDouble("placeLon"),
                        json.getJSONObject("placePhoto").get("data") as JSONArray
                    )
                    result.add(p)
                }
                data.postValue(result)
            }
        })
    }
}
