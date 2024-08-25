package pl.lbiio.quickadoption.repositories

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.Completable
import io.reactivex.Observable
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.LocationData
import pl.lbiio.quickadoption.data.UserCurrentData
import pl.lbiio.quickadoption.services.ApiService
import java.io.File
import javax.inject.Inject

class EditingAccountRepository @Inject constructor(private val apiService: ApiService) {
    private var storageReference: StorageReference? = null

    fun getUserCurrentData(UID: String): Observable<UserCurrentData> {
        return apiService.getUserCurrentData(UID)
    }

    private fun updateProfileImage(name: String, path: String, onSuccess: (url: String) -> Unit) {
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

    fun updateUser(UID: String, user: UserCurrentData): Completable {
        if(!user.profileImage.contains("https")){
            updateProfileImage("${QuickAdoptionApp.getCurrentUserId()}-profile", user.profileImage){
                user.profileImage = it
            }
        }
        return apiService.updateUser(UID, user)
    }
}