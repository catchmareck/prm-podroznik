package com.example.podroznik.ui.register

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.podroznik.ListActivity

import com.example.podroznik.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterFragment : Fragment() {

    private val client = OkHttpClient()

    lateinit var authRegisterEmail: EditText
    lateinit var authRegisterPassword: EditText
    lateinit var authRegisterRepeatPassword: EditText
    lateinit var authRegisterSubmit: Button
    lateinit var authRegisterSwitch: Button

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRegisterEmail = view.findViewById(R.id.auth_register_email)
        authRegisterPassword = view.findViewById(R.id.auth_register_password)
        authRegisterRepeatPassword = view.findViewById(R.id.auth_register_repeat_password)
        authRegisterSubmit = view.findViewById(R.id.auth_register_submit)
        authRegisterSwitch = view.findViewById(R.id.auth_register_switch)

        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        authRegisterEmail.hint = "Email"
        authRegisterPassword.hint = "Password"
        authRegisterRepeatPassword.hint = "Repeat password"
        authRegisterSubmit.text = "Register"
        authRegisterSwitch.text = "Login"

        bindEvents(view)
    }

    private fun bindEvents(view: View) {

        authRegisterSubmit.setOnClickListener {

            val payload = JSONObject()
            payload.put("email", authRegisterEmail.text)
            payload.put("password", authRegisterPassword.text)
            payload.put("repeatPassword", authRegisterRepeatPassword.text)
            val request = Request.Builder()
                .post(payload.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                .url("http://10.0.2.2:3000/users/auth/register")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) = println("############# DZIEJE SIE ERROR" + e.message)
                override fun onResponse(call: Call, response: Response){
                    println("############# DZIEJE SIE" + response.body?.string())

                    if (response.code < 200 || response.code > 299) return

                    val intent = Intent(view.context, ListActivity::class.java)
                    startActivity(intent)

                    activity?.finish()
                }
            })
        }

        authRegisterSwitch.setOnClickListener {

            activity?.finish()
        }
    }
}
