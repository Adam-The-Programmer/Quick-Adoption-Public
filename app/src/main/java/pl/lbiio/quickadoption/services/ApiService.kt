package pl.lbiio.quickadoption.services

import com.google.gson.GsonBuilder
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.lbiio.quickadoption.data.ApplicationForAdoptionDTO
import pl.lbiio.quickadoption.data.CurrentOpinion
import pl.lbiio.quickadoption.data.LastMessageDTO
import pl.lbiio.quickadoption.data.LeaderBoardItem
import pl.lbiio.quickadoption.data.LocationData
import pl.lbiio.quickadoption.data.Opinion
import pl.lbiio.quickadoption.data.OpinionToInsertDTO
import pl.lbiio.quickadoption.data.OwnAnnouncement
import pl.lbiio.quickadoption.data.OwnAnnouncementChat
import pl.lbiio.quickadoption.data.OwnAnnouncementListItem
import pl.lbiio.quickadoption.data.PublicAnnouncementChat
import pl.lbiio.quickadoption.data.PublicAnnouncementDetails
import pl.lbiio.quickadoption.data.PublicAnnouncementListItem
import pl.lbiio.quickadoption.data.User
import pl.lbiio.quickadoption.data.UserCurrentData
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    @DELETE("deleteAnnouncement/{AnnouncementID}") // tabbed
    fun deleteAnnouncement(@Path("AnnouncementID") AnnouncementID: Long): Completable

    @PUT("updateAnnouncement")
    fun updateAnnouncement(@Body ownAnnouncement: OwnAnnouncement): Completable

    @PUT("addAnnouncement/{UID}")
    fun addAnnouncement(@Path("UID") UID: String, @Body ownAnnouncement: OwnAnnouncement): Completable

    @PUT("setAnnouncementHaveUnreadMessage/{AnnouncementID}") // details, chatConsloe
    fun setAnnouncementHaveUnreadMessage(@Path("AnnouncementID") AnnouncementID: Long): Completable

    @PUT("setAnnouncementDontHaveUnreadMessage/{AnnouncementID}") //chats
    fun setAnnouncementDontHaveUnreadMessage(@Path("AnnouncementID") AnnouncementID: Long): Completable

    @PUT("assignKeeperToAnnouncement/{UID}/{AnnouncementID}") //chatConsole
    fun assignKeeperToAnnouncement(@Path("UID") UID: String, @Path("AnnouncementID") AnnouncementID: Long): Completable

    @GET("getAllPublicAnnouncementListItems/{Country}/{City}/{DateRange}/{UID}")
    fun getAllPublicAnnouncementListItems(@Path("Country") Country: String, @Path("City") City: String, @Path("DateRange") DateRange: String, @Path("UID") UID: String): Observable<List<PublicAnnouncementListItem>>

    @GET("getParticularPublicAnnouncement/{AnnouncementID}")
    fun getParticularPublicAnnouncement(@Path("AnnouncementID") AnnouncementID: Long): Observable<PublicAnnouncementDetails>

    @PUT("applyForAdoption/{timestamp}")
    fun applyForAdoption(@Path("timestamp") timestamp: Long, @Body applicationForAdoptionDTO: ApplicationForAdoptionDTO): Completable


    // Chat methods

    @GET("getLocationData/{UID}")
    fun getLocationData(@Path("UID") UID: String): Observable<LocationData>

    @GET("ownChatsForAnnouncement/{UID}/{AnnouncementID}")
    fun getOwnChatsForAnnouncement(@Path("UID") UID: String, @Path("AnnouncementID") AnnouncementID: Long): Observable<List<OwnAnnouncementChat>>

    @GET("publicChatsForUser/{UID}")
    fun getPublicChatsForUser(@Path("UID") UID: String): Observable<List<PublicAnnouncementChat>>

    @PUT("lastMessageForChat/{ChatID}/{timestamp}") // chat console
    fun setLastMessageForChat(@Path("ChatID") ChatID: String, @Path("timestamp") timestamp: Long, @Body lastMessageDTO: LastMessageDTO): Completable

    @PUT("makeChatAccepted/{AnnouncementID}/{ChatID}") //chatConsole
    fun makeChatAccepted(@Path("AnnouncementID") AnnouncementID: Long, @Path("ChatID") ChatID: String): Observable<Int> //chatConsole


    //Opinion Methods

    @PUT("insertOpinion/{timestamp}")
    fun insertOpinion(@Path("timestamp") timestamp: Long, @Body opinionToInsertDTO: OpinionToInsertDTO): Completable

    @PATCH("updateOpinion/{opinionID}/{Content}/{RateStars}/{Timestamp}")
    fun updateOpinion(@Path("opinionID") opinionID: Int, @Path("Content") Content: String, @Path("RateStars") RateStars: Int, @Path("Timestamp") Timestamp: Long): Completable

    @GET("opinions/{ReceiverID}")
    fun getOpinions(@Path("ReceiverID") ReceiverID: String): Observable<List<Opinion>>

    @GET("getCurrentOpinion/{receiver}/{author}")
    fun getCurrentOpinion(@Path("receiver") receiver: String, @Path("author") author: String): Observable<CurrentOpinion>


    //User Methods

    @PUT("insertUser")
    fun insertUser(@Body user: User): Completable

    @PUT("updateUser/{UID}")
    fun updateUser(@Path("UID") UID: String, @Body user: UserCurrentData): Completable

    @GET("rateOfUser/{UID}")
    fun getRateOfUser(@Path("UID") UID: String): Observable<Float>

    @GET("getUser/{UID}")
    fun getUser(@Path("UID") UID: String): Observable<User>

    @GET("getUserCurrentData/{UID}")
    fun getUserCurrentData(@Path("UID") UID: String): Observable<UserCurrentData>

    @GET("getLeaderBoard/{UID}")
    fun getLeaderBoard(@Path("UID") UID: String): Observable<List<LeaderBoardItem>>


}