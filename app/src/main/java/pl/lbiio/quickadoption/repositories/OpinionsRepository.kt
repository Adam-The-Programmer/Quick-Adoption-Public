package pl.lbiio.quickadoption.repositories

import io.reactivex.Observable
import pl.lbiio.quickadoption.data.Opinion
import pl.lbiio.quickadoption.services.ApiService
import javax.inject.Inject

class OpinionsRepository @Inject constructor(private val apiService: ApiService) {

    fun getOpinions(receiverId: String): Observable<List<Opinion>> {
        return apiService.getOpinions(receiverId)
    }

    fun getRateOfUser(UID: String): Observable<Float>{
        return apiService.getRateOfUser(UID)
    }

}