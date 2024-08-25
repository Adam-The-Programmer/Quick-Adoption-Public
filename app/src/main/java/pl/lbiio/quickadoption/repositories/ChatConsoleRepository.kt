package pl.lbiio.quickadoption.repositories

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.background.HandleMessagesQueueService
import pl.lbiio.quickadoption.data.ChatMessage
import pl.lbiio.quickadoption.data.ConversationMessage
import pl.lbiio.quickadoption.data.CurrentOpinion
import pl.lbiio.quickadoption.data.LastMessageDTO
import pl.lbiio.quickadoption.data.LocationData
import pl.lbiio.quickadoption.data.OpinionToInsertDTO
import pl.lbiio.quickadoption.data.OwnMessage
import pl.lbiio.quickadoption.services.ApiService
import java.io.File
import javax.inject.Inject

class ChatConsoleRepository @Inject constructor(private val apiService: ApiService) {

    private val db = Firebase.firestore
    private var storageReference: StorageReference? = null

    private val PREFERENCE_NAME = "QUICK_ADOPTION_PREFERENCES"
    private val pref: SharedPreferences = QuickAdoptionApp.getAppContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

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

    private fun processMessage(content: String, contentType: String, chatId: String, action: (newContent: String) -> Unit){
        if(contentType=="image"){
            uploadImageToFirebase("${System.currentTimeMillis()}-${chatId}", content){url->
                action(url)
            }
        }
        else{
            action(content)
        }
    }

    fun uploadMessage(Content: String, ContentType: String, ChatId: String, announcementId: Long, isChatOwn: Boolean, onComplete:()->Unit, onError: (error: Throwable) -> Unit) {
        processMessage(Content, ContentType, ChatId) { newContent ->
            val gson = GsonBuilder().create()
            val rawMessage = ConversationMessage.ProvidedMessage(
                QuickAdoptionApp.getCurrentUserId()!!,
                newContent,
                ContentType,
                System.currentTimeMillis()
            )

            val message = hashMapOf(
                "${System.currentTimeMillis()}" to gson.toJson(rawMessage),
            )
            db.collection("chats")
                .document(ChatId)
                .update(message as Map<String, Any>)
                .addOnSuccessListener {
                    setLastMessageForChat(ChatId, LastMessageDTO(newContent, ContentType, QuickAdoptionApp.getCurrentUserId()!!))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { onComplete() },
                            { error -> onError(error) }
                        )
                    if (!isChatOwn) {
                        setAnnouncementHaveUnreadMessage(announcementId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { onComplete() },
                                { error -> onError(error) }
                            )
                    }
                }.addOnFailureListener {
                    onError(it)
                }
        }
    }

    fun listenToMessages(ChatID: String, onMessagesChanged: (List<ConversationMessage.ProvidedMessage>) -> Unit) {
        val messages = mutableListOf<ConversationMessage.ProvidedMessage>()
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

                val newMessages = mutableListOf<ConversationMessage.ProvidedMessage>()

                data?.values?.forEach { value ->
                    Log.d("object", value.toString())
                    val gson = Gson()
                    val messageType = object : TypeToken<ConversationMessage.ProvidedMessage>() {}.type
                    val message: ConversationMessage.ProvidedMessage = gson.fromJson(value.toString(), messageType)
                    newMessages.add(message)
                }

                messages.clear()
                messages.addAll(newMessages.sortedBy { it.timestamp })
                Log.d("new messages", newMessages.toString())
                onMessagesChanged(messages)
            }
        }
    }

    fun getLocationData(UID: String): Observable<LocationData> {
        return apiService.getLocationData(UID)
    }

    fun setAnnouncementHaveUnreadMessage(announcementId: Long): Completable {
        return apiService.setAnnouncementHaveUnreadMessage(announcementId)
    }

    fun assignKeeperToAnnouncement(UID: String, announcementId: Long): Completable {
        return apiService.assignKeeperToAnnouncement(UID, announcementId)
    }

    fun makeChatAccepted(AnnouncementID: Long, ChatID: String): Observable<Int> {
        return apiService.makeChatAccepted(AnnouncementID, ChatID)
    }

    fun setLastMessageForChat(ChatID: String, lastMessageDTO: LastMessageDTO): Completable{
        return apiService.setLastMessageForChat(ChatID, System.currentTimeMillis(), lastMessageDTO)
    }

    fun insertOpinion(opinionToInsertDTO: OpinionToInsertDTO): Completable{
        return apiService.insertOpinion(System.currentTimeMillis(), opinionToInsertDTO)
    }

    fun updateOpinion(opinionId: Int, opinion: String, rateStars: Int, timestamp: Long): Completable{
        return apiService.updateOpinion(opinionId, opinion, rateStars, timestamp)
    }

    fun getCurrentOpinion(receiver: String, author: String): Observable<CurrentOpinion>{
        return apiService.getCurrentOpinion(receiver, author)
    }

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.getString() = pref.getString(this, "")!!

    fun addMessageToQueue(chatId: String, content: String, contentType: String){
        val gson = GsonBuilder().create()

        val listType = object : TypeToken<List<ConversationMessage.PendingMessage>>() {}.type

        val pendingMessages: List<ConversationMessage.PendingMessage>? = gson.fromJson(chatId.getString(), listType)

        val newMessage = ConversationMessage.PendingMessage(content, contentType)
        val allMessages = mutableListOf(newMessage)
        pendingMessages?.let {
            allMessages.addAll(it.toMutableList())
        }
        //val allMessages = pendingMessages!!.toMutableList().add(newMessage)
        chatId.put(gson.toJson(allMessages))
    }

    private fun deleteMessageQueue(chatId: String){
        editor.remove(chatId)
        editor.commit()
    }

    fun getAllPendingMessages(chatId: String): List<ConversationMessage.PendingMessage>? {
        val gson = GsonBuilder().create()
        val listType = object : TypeToken<List<ConversationMessage.PendingMessage>>() {}.type
        return gson.fromJson(chatId.getString(), listType)
    }

    fun sendMessagesOffline(chatId: String, announcementId: Long, isChatOwn: Boolean) {
        val list = getAllPendingMessages(chatId)
        list?.forEach { it ->
            uploadMessage(it.content, it.contentType, chatId, announcementId, isChatOwn, {
                Log.d("sendMessagesOffline", "finished")
            }, { error ->
                Log.e("sendMessagesOffline error", error.toString())
            })
        }
        deleteMessageQueue(chatId)
        //val broadcastIntent = Intent("pl.lbiio.quickadoption.SENDING_MESSAGE_BROADCAST_ACTION")
        //QuickAdoptionApp.getAppContext().sendBroadcast(broadcastIntent)
    }


}