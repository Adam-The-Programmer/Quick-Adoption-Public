package pl.lbiio.quickadoption.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.R
import pl.lbiio.quickadoption.repositories.ChatConsoleRepository
import pl.lbiio.quickadoption.repositories.InternetAccessRepository

class HandleMessagesQueueService: Service() {

    private val CHANNEL_ID = "SharePackageService"
    private val NOTIFICATION_ID = 1

    companion object {
        private lateinit var chatConsoleRepository: ChatConsoleRepository
        private lateinit var internetAccessRepository: InternetAccessRepository

        fun initialize(
            chatConsoleRepo: ChatConsoleRepository,
            internetRepo: InternetAccessRepository
        ) {
            chatConsoleRepository = chatConsoleRepo
            internetAccessRepository = internetRepo
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val chatId = intent?.getStringExtra("chat_id")
        val announcementId = intent?.getLongExtra("announcement_id", -1L)
        val isChatOwn = intent?.getBooleanExtra("is_chat_own", true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Messages Queue",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Messages will be sent when internet is available")
            .setContentText("Amount of awaiting messages: ")
            .setSmallIcon(R.drawable.baseline_send_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)

        val notification = notificationBuilder.build()
        startForeground(NOTIFICATION_ID, notification)
        Thread {
            CoroutineScope(Dispatchers.IO).launch {
                val listOfNonNulls = listOf(chatId, announcementId, isChatOwn)
                if(listOfNonNulls.all { it!=null }){
                    while(!internetAccessRepository.isInternetAvailable()){
                        Log.d("internet Availability", "unavailable")
                    }
                    Log.d("internet Availability", "available")
                    chatConsoleRepository.sendMessagesOffline(chatId!!, announcementId!!, isChatOwn!!)
                    val finishedNotification = NotificationCompat.Builder(QuickAdoptionApp.getAppContext(), CHANNEL_ID)
                        .setContentTitle("Sending Completed")
                        .setContentText("All messages provided!")
                        .setSmallIcon(R.drawable.baseline_send_24)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .build()
                    notificationManager.notify(NOTIFICATION_ID + 1, finishedNotification)
                    stopForeground(true)
                    notificationManager.cancel(NOTIFICATION_ID)
                    stopSelf()
                    super.onStartCommand(intent, flags, startId)
                }
//                chatId?.apply {
//
//                }
            }
        }.start()

        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}