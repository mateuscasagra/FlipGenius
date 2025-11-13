package com.example.flipgenius

import android.app.Application
import com.google.firebase.FirebaseApp

class FlipGeniusApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
