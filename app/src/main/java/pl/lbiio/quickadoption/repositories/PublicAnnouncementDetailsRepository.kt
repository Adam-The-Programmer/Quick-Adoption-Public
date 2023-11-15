package pl.lbiio.quickadoption.repositories

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import io.reactivex.Observable
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.ApplicationForAdoptionDTO
import pl.lbiio.quickadoption.data.OwnAnnouncement
import pl.lbiio.quickadoption.data.PublicAnnouncementDetails
import pl.lbiio.quickadoption.services.ApiService
import javax.inject.Inject

class PublicAnnouncementDetailsRepository @Inject constructor(private val apiService: ApiService) {
    private val db = Firebase.firestore

    fun getParticularPublicAnnouncement(announcementId: Long): Observable<PublicAnnouncementDetails> {
        return apiService.getParticularPublicAnnouncement(announcementId)
    }

    fun applyForAdoption(applicationForAdoptionDTO: ApplicationForAdoptionDTO): Completable {
        return apiService.applyForAdoption(applicationForAdoptionDTO)
    }

    fun createDocumentAndGetID(content: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val data = hashMapOf(
            "UID" to QuickAdoptionApp.getCurrentUserId(),
            "Content" to content,
            "ContentType" to "text",
            "Timestamp" to System.currentTimeMillis()
        )
        db.collection("chats")
            .add(data)
            .addOnSuccessListener { documentReference ->
                val documentID = documentReference.id
                onSuccess(documentID)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


}