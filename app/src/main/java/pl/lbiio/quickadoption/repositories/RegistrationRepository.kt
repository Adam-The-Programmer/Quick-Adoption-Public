package pl.lbiio.quickadoption.repositories

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.Completable
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.User
import pl.lbiio.quickadoption.services.ApiService
import java.io.File
import javax.inject.Inject

class RegistrationRepository @Inject constructor(private val apiService: ApiService) {
    private val db = Firebase.firestore
    private var storageReference: StorageReference? = null

    fun register(email: String, password: String): Task<AuthResult> {
        return QuickAdoptionApp.getAuth().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("register success", "yes")
            }
    }

    fun canRegister(inputEmail: String, callback: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", inputEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val canRegister = querySnapshot.isEmpty
                callback(canRegister)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(false) // Handle the error condition
            }
    }



    fun uploadUserToFirebase(email: String): Task<DocumentReference> {
        val data = hashMapOf(
            "email" to email
        )
        return db.collection("users")
            .add(data)
            .addOnSuccessListener {
                // Upload success
            }.addOnFailureListener {
                // Upload failure
            }
    }

    fun insertUser(user: User): Completable {
        return apiService.insertUser(user)
    }

    fun uploadProfileImage(name: String, path: String, onSuccess: (url: String) -> Unit) {
        val file = Uri.fromFile(File(path))
        storageReference = FirebaseStorage.getInstance().reference.child("images")
        val imageRef: StorageReference = storageReference!!.child(name)
        imageRef.putFile(file!!).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }

            // Get the download URL of the uploaded image
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                onSuccess(imageUrl)
            }
        }
    }
}