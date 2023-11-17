package pl.lbiio.quickadoption.repositories

import io.reactivex.Observable
import pl.lbiio.quickadoption.data.PublicAnnouncementChat
import pl.lbiio.quickadoption.services.ApiService
import javax.inject.Inject

class PublicChatsListRepository @Inject constructor(private val apiService: ApiService){
    fun getPublicChatsForUser(UID: String): Observable<List<PublicAnnouncementChat>> {
        return apiService.getPublicChatsForUser(UID)
    }
}