package pl.lbiio.quickadoption.repositories

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import io.reactivex.Completable
import io.reactivex.Observable
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.ApplicationForAdoptionDTO
import pl.lbiio.quickadoption.data.ChatMessage
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

    fun setAnnouncementHaveUnreadMessage(announcementId: Long): Completable{
        return apiService.setAnnouncementHaveUnreadMessage(announcementId)
    }

    fun createDocumentAndGetID(Content: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {

        val gson = GsonBuilder().create()
        val rawMessage = ChatMessage(
            QuickAdoptionApp.getCurrentUserId()!!, Content, "text", System.currentTimeMillis()
        )

        //Log.d("json", gson.toJson(rawMessage).toString())

        val message = hashMapOf(
            "${System.currentTimeMillis()}" to gson.toJson(rawMessage).toString(),
        )
        db.collection("chats")
            .add(message)
            .addOnSuccessListener { documentReference ->
                val documentID = documentReference.id
                onSuccess(documentID)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }

    }


}