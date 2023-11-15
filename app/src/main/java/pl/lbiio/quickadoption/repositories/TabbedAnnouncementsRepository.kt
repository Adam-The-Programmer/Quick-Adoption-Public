package pl.lbiio.quickadoption.repositories


import io.reactivex.Completable
import io.reactivex.Observable
import pl.lbiio.quickadoption.data.OwnAnnouncement
import pl.lbiio.quickadoption.data.OwnAnnouncementListItem
import pl.lbiio.quickadoption.data.PublicAnnouncementDetails
import pl.lbiio.quickadoption.data.PublicAnnouncementListItem
import pl.lbiio.quickadoption.services.ApiService
import javax.inject.Inject

class TabbedAnnouncementsRepository @Inject constructor(private val apiService: ApiService) {

    fun getAllOwnAnnouncementList(UID: String): Observable<List<OwnAnnouncementListItem>> {
        return apiService.getAllOwnAnnouncementList(UID)
    }







    fun getAllPublicAnnouncementListItems(country: String, city: String, dateRange: String, UID: String): Observable<List<PublicAnnouncementListItem>> {
        return apiService.getAllPublicAnnouncementListItems(country, city, dateRange, UID)
    }



}