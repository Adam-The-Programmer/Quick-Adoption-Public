package pl.lbiio.quickadoption.repositories

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.Completable
import io.reactivex.Observable
import pl.lbiio.quickadoption.data.OwnAnnouncement
import pl.lbiio.quickadoption.services.ApiService
import java.io.File
import javax.inject.Inject

class ApplyingAnnouncementRepository @Inject constructor(private val apiService: ApiService) {
    private var storageReference: StorageReference? = null
    fun addAnnouncement(UID: String, announcement: OwnAnnouncement): Completable {
        return apiService.addAnnouncement(UID, announcement)
    }
    fun updateAnnouncement(announcement: OwnAnnouncement): Completable {
        return apiService.updateAnnouncement(announcement)
    }
    fun getParticularOwnAnnouncement(announcementId: Long): Observable<OwnAnnouncement> {
        return apiService.getParticularOwnAnnouncement(announcementId)
    }

    fun uploadAnimalImage(name: String, path: String, onSuccess: (url: String) -> Unit) {
        if(path.contains("http")){
            onSuccess(path)
        }
        else{
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
}