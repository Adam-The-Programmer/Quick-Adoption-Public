package pl.lbiio.quickadoption.repositories

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import kotlinx.coroutines.tasks.await
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.data.ChatMessage
import pl.lbiio.quickadoption.data.LastMessageDTO
import pl.lbiio.quickadoption.services.ApiService
import retrofit2.http.Body
import retrofit2.http.Path
import java.io.File
import javax.inject.Inject

class ChatConsoleRepository @Inject constructor(private val apiService: ApiService) {

    private val db = Firebase.firestore
    private var storageReference: StorageReference? = null

    fun uploadImageToFirebase(name: String, path: String, onSuccess: (url: String) -> Unit) {
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

    fun uploadMessage(Content: String, ContentType: String, ChatID: String): Task<Void> {

        val gson = GsonBuilder().create()
        val rawMessage = ChatMessage(
            QuickAdoptionApp.getCurrentUserId()!!, Content, ContentType, System.currentTimeMillis()
        )

        val message = hashMapOf(
            "${System.currentTimeMillis()}" to gson.toJson(rawMessage),
        )
        return db.collection("chats")
            .document(ChatID)
            .update(message as Map<String, Any>)
            .addOnSuccessListener {
                // Upload success
            }.addOnFailureListener {
                // Upload failure
            }

    }

    fun listenToMessages(ChatID: String, onMessagesChanged: (List<ChatMessage>) -> Unit) {
        val messages = mutableListOf<ChatMessage>()
        val query = db.collection("chats")
            .document(ChatID)

        query.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
            if (e != null) {
                // Handle errors here if needed
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val data = snapshot.data
                Log.d("dane", data.toString())

                val newMessages = mutableListOf<ChatMessage>()

                data?.values?.forEach { value ->
                    Log.d("object", value.toString())
                    val gson = Gson()
                    val messageType = object : TypeToken<ChatMessage>() {}.type
                    val message: ChatMessage = gson.fromJson(value.toString(), messageType)
                    newMessages.add(message)
                }

                messages.clear()
                messages.addAll(newMessages.sortedBy { it.timestamp })
                onMessagesChanged(messages)
            }
        }
    }

    fun setAnnouncementHaveUnreadMessage(announcementId: Long): Completable {
        return apiService.setAnnouncementHaveUnreadMessage(announcementId)
    }

    fun assignKeeperToAnnouncement(UID: String, announcementId: Long): Completable {
        return apiService.assignKeeperToAnnouncement(UID, announcementId)
    }

    fun makeChatAccepted(AnnouncementID: Long, ChatID: String): Completable{
        return apiService.makeChatAccepted(AnnouncementID, ChatID)
    }

    fun setLastMessageForChat(ChatID: String, lastMessageDTO: LastMessageDTO): Completable{
        return apiService.setLastMessageForChat(ChatID, lastMessageDTO)
    }

}