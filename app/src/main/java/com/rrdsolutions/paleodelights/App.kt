package com.rrdsolutions.paleodelights

import android.app.Application
import androidx.room.Room
import com.google.firebase.FirebaseApp

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

