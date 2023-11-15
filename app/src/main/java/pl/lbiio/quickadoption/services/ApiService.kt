package pl.lbiio.quickadoption.services

import com.google.gson.GsonBuilder
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.lbiio.quickadoption.data.ApplicationForAdoptionDTO
import pl.lbiio.quickadoption.data.LastMessageDTO
import pl.lbiio.quickadoption.data.OpinionToInsertDTO
import pl.lbiio.quickadoption.data.OwnAnnouncement
import pl.lbiio.quickadoption.data.OwnAnnouncementChat
import pl.lbiio.quickadoption.data.OwnAnnouncementListItem
import pl.lbiio.quickadoption.data.PublicAnnouncementChat
import pl.lbiio.quickadoption.data.PublicAnnouncementDetails
import pl.lbiio.quickadoption.data.PublicAnnouncementListItem
import pl.lbiio.quickadoption.data.User
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    companion object {
        fun createRetrofit(baseUrl: String): Retrofit {
            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }

    // Announcement Methods

    @GET("allOwnAnnouncementList/{UID}")
    fun getAllOwnAnnouncementList(@Path("UID") UID: String): Observable<List<OwnAnnouncementListItem>>

    @GET("particularOwnAnnouncement/{AnnouncementID}")
    fun getParticularOwnAnnouncement(@Path("AnnouncementID") AnnouncementID: Long): Observable<OwnAnnouncement>

    @DELETE("deleteAnnouncement/{AnnouncementID}")
    fun deleteAnnouncement(@Path("AnnouncementID") AnnouncementID: Long): Completable

    @PUT("updateAnnouncement")
    fun updateAnnouncement(@Body ownAnnouncement: OwnAnnouncement): Completable

    @PUT("addAnnouncement/{UID}")
    fun addAnnouncement(@Path("UID") UID: String, @Body ownAnnouncement: OwnAnnouncement): Completable

    @PUT("setAnnouncementHaveUnreadMessage/{AnnouncementID}")
    fun setAnnouncementHaveUnreadMessage(@Path("AnnouncementID") AnnouncementID: Long): Completable

    @PUT("setAnnouncementDontHaveUnreadMessage/{AnnouncementID}")
    fun setAnnouncementDontHaveUnreadMessage(@Path("AnnouncementID") AnnouncementID: Long): Completable

    @PUT("assignKeeperToAnnouncement/{UID}/{AnnouncementID}")
    fun assignKeeperToAnnouncement(@Path("UID") UID: String, @Path("AnnouncementID") AnnouncementID: Long): Completable

    @GET("getAllPublicAnnouncementListItems/{Country}/{City}/{DateRange}/{UID}")
    fun getAllPublicAnnouncementListItems(@Path("Country") Country: String, @Path("City") City: String, @Path("DateRange") DateRange: String, @Path("UID") UID: String): Observable<List<PublicAnnouncementListItem>>

    @GET("getParticularPublicAnnouncement/{AnnouncementID}")
    fun getParticularPublicAnnouncement(@Path("AnnouncementID") AnnouncementID: Long): Observable<PublicAnnouncementDetails>

    @PUT("applyForAdoption")
    fun applyForAdoption(@Body applicationForAdoptionDTO: ApplicationForAdoptionDTO): Completable


    // Chat methods

    @GET("ownChatsForAnnouncement/{UID}/{AnnouncementID}")
    fun getOwnChatsForAnnouncement(@Path("UID") UID: String, @Path("AnnouncementID") AnnouncementID: Long): Observable<List<OwnAnnouncementChat>>

    @GET("publicChatsForUser/{UID}")
    fun getPublicChatsForUser(@Path("UID") UID: String): Observable<List<PublicAnnouncementChat>>

    @PUT("lastMessageForChat/{ChatID}")
    fun setLastMessageForChat(@Path("ChatID") ChatID: String, @Body lastMessageDTO: LastMessageDTO): Completable

    @PUT("makeChatAccepted/{AnnouncementID}/{ChatID}")
    fun makeChatAccepted(@Path("AnnouncementID") AnnouncementID: Long, @Path("ChatID") ChatID: String): Completable


    //Opinion Methods

    @PUT("insertOpinion")
    fun insertOpinion(@Body opinionToInsertDTO: OpinionToInsertDTO): Completable

    @GET("opinions/{ReceiverID}")
    fun getOpinions(@Path("ReceiverID") ReceiverID: String): Observable<List<PublicAnnouncementChat>>


    //User Methods

    @PUT("insertUser")
    fun insertUser(@Body user: User): Completable

    @PUT("updateUser/{UID}")
    fun updateUser(@Path("UID") UID: String): Completable

    @GET("rateOfUser/{UID}")
    fun getRateOfUser(@Path("UID") UID: String): Observable<String>

    @GET("getUser/{UID}")
    fun getUser(@Path("UID") UID: String): Observable<User>


}