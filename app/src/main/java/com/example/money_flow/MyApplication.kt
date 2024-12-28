package com.example.money_flow

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}