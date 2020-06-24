package com.example.podroznik

class AppState {

    lateinit var authHeader: String

    companion object {
        private lateinit var instance: AppState
        fun getInstance(): AppState {
            if (!::instance.isInitialized) {
                instance = AppState()
            }
            return instance
        }
    }
}