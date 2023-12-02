package pl.lbiio.quickadoption

import android.app.Application
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.HiltAndroidApp
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class QuickAdoptionApp : Application() {
    companion object {

        private lateinit var auth: FirebaseAuth
        private lateinit var instance: QuickAdoptionApp

        fun getAuth(): FirebaseAuth? {
            return auth
        }

        fun getCurrentUser(): FirebaseUser? {
            return auth.currentUser
        }

        fun getCurrentUserId(): String? {
            return auth.uid.toString()
        }

        fun getAppContext(): Context {
            return instance.applicationContext
        }

        fun encodePathFile(path: String): String {
            return URLEncoder.encode(path.replace("%2F", "SLASH_PLACEHOLDER"), "UTF-8")
        }

        fun decodePathFile(encodedPath: String): String {
            val decodedPath = URLDecoder.decode(encodedPath, "UTF-8")
            return decodedPath.replace("SLASH_PLACEHOLDER", "%2F")
        }

        fun calculateTimeDifference(timestamp: Long): String {
            val currentTimeMillis = System.currentTimeMillis()
            val timeDifferenceMillis = currentTimeMillis - timestamp

            //val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)
            val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis)

            return when {
                //seconds < 60 -> "${seconds}s ago"
                minutes < 60 -> "${minutes}min ago"
                hours < 24 -> "${hours}h ago"
                days < 365 -> "${days}d ago"
                else -> {
                    val sdf = SimpleDateFormat("dd-MM-yyyy")
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = timestamp
                    sdf.format(calendar.time)
                }
            }
        }

        fun formatInputDateValue(dateMillis: Long): String {
            val date = LocalDate.ofEpochDay(dateMillis / 86400000) // Convert millis to LocalDate

            // Format the date as "yy MM dd"
            return String.format(
                "%02d.%02d.%02d",
                date.dayOfMonth,
                date.monthValue,
                date.year
            )
        }
        fun getFilePath(context: Context, uri: Uri): String? {
            val isMediaDocument = uri.authority == "com.android.providers.media.documents"
            if (isMediaDocument) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        private fun getDataColumn(
            context: Context,
            uri: Uri?,
            selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        fun convertToDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            return sdf.format(calendar.time)
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        if(getCurrentUser()!=null){
            val mainActivityIntent = Intent(
                getAppContext(),
                MainActivity::class.java
            )
            mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            getAppContext().startActivity(mainActivityIntent)
        }

    }
}