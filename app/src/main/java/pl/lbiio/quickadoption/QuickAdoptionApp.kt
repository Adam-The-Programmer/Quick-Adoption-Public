package pl.lbiio.quickadoption

import android.app.Application
import android.content.Context
import android.content.Intent
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.HiltAndroidApp
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

@HiltAndroidApp
class QuickAdoptionApp : Application() {
    companion object {

        private lateinit var auth: FirebaseAuth
        private lateinit var instance: QuickAdoptionApp

        fun getAuth(): FirebaseAuth? {
            return auth
        }

        fun getCurrentUser(): FirebaseUser? {
            return auth.currentUser
        }

        fun getCurrentUserId(): String? {
            return auth.uid.toString()
        }

        fun getAppContext(): Context {
            return instance.applicationContext
        }

        fun codePathFile(path: String): String {
            return path.replace("/", "*")
        }

        fun decodePathFile(codedPath: String): String {
            return codedPath.replace("*", "/")
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        if(getCurrentUser()!=null){
            val mainActivityIntent = Intent(
                getAppContext(),
                MainActivity::class.java
            )
            mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            getAppContext().startActivity(mainActivityIntent)
        }

    }
}