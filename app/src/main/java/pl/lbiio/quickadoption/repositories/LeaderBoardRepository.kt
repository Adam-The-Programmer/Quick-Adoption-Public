package pl.lbiio.quickadoption.repositories

import io.reactivex.Observable
import pl.lbiio.quickadoption.data.LeaderBoardItem
import pl.lbiio.quickadoption.data.OwnAnnouncementChat
import pl.lbiio.quickadoption.services.ApiService
import javax.inject.Inject

class LeaderBoardRepository  @Inject constructor(private val apiService: ApiService) {

    fun getLeaderBoard(UID: String): Observable<List<LeaderBoardItem>> {
        return apiService.getLeaderBoard(UID)
    }

}