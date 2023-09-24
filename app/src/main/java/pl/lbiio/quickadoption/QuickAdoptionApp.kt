package pl.lbiio.quickadoption

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuickAdoptionApp : Application() {
    companion object {
        private lateinit var instance: QuickAdoptionApp
        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this

    }
}