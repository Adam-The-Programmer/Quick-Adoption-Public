package pl.lbiio.quickadoption.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.lbiio.quickadoption.QuickAdoptionApp
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class LoginRepository @Inject constructor() {
    private val db = Firebase.firestore

    fun login(email: String, password: String): Task<AuthResult> {
        return QuickAdoptionApp.getAuth()?.signInWithEmailAndPassword(email, password)!!
            .addOnSuccessListener {
                Log.d("login success", "yes")
            }
    }

    fun canLogin(inputEmail: String, callback: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", inputEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val canLogin = !querySnapshot.isEmpty
                callback(canLogin)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(false) // Handle the error condition
            }
    }

}