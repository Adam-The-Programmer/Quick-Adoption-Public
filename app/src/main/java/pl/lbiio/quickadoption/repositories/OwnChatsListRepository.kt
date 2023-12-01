package pl.lbiio.quickadoption.repositories

import io.reactivex.Completable
import io.reactivex.Observable
import pl.lbiio.quickadoption.data.OwnAnnouncementChat
import pl.lbiio.quickadoption.data.User
import pl.lbiio.quickadoption.services.ApiService
import javax.inject.Inject

class OwnChatsListRepository@Inject constructor(private val apiService: ApiService) {

    fun getOwnChatsForAnnouncement(UID: String, announcementID: Long): Observable<List<OwnAnnouncementChat>> {
        return apiService.getOwnChatsForAnnouncement(UID, announcementID)
    }

    fun setAnnouncementDontHaveUnreadMessage(announcementID: Long): Completable {
        return apiService.setAnnouncementDontHaveUnreadMessage(announcementID)
    }
}