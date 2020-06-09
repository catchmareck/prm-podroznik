package com.example.podroznik.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.podroznik.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainFragment : Fragment() {

    private val client = OkHttpClient()

    lateinit var authLoginEmail: EditText
    lateinit var authLoginPassword: EditText
    lateinit var authLoginSubmit: Button
    lateinit var authLoginSwitch: Button

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authLoginEmail = view.findViewById(R.id.auth_login_email)
        authLoginPassword = view.findViewById(R.id.auth_login_password)
        authLoginSubmit = view.findViewById(R.id.auth_login_submit)
        authLoginSwitch = view.findViewById(R.id.auth_login_switch)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        authLoginEmail.hint = "Email"
        authLoginPassword.hint = "Password"
        authLoginSubmit.text = "Login"
        authLoginSwitch.text = "Register"

        bindEvents()
    }

    private fun bindEvents() {

        authLoginSubmit.setOnClickListener {

            val payload = JSONObject()
            payload.put("email", authLoginEmail.text)
            payload.put("password", authLoginPassword.text)
            val request = Request.Builder()
                .put(payload.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                .url("http://10.0.2.2:3000/users/auth/login")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) = println("############# DZIEJE SIE ERROR" + e.message)
                override fun onResponse(call: Call, response: Response) = println("############# DZIEJE SIE" + response.body?.string())
            })
        }
    }

    fun run(url: String) {
        val payload = JSONObject()
        payload.put("email", "android@kotlin.local")
        payload.put("password", "test123456")
        payload.put("repeatPassword", "test123456")
        val request = Request.Builder()
            .post(payload.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = println("############# DZIEJE SIE ERROR" + e.message)
            override fun onResponse(call: Call, response: Response) = println("############# DZIEJE SIE" + response.body?.string())
        })
    }
}
