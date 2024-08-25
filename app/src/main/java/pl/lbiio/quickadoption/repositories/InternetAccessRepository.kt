package pl.lbiio.quickadoption.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class InternetAccessRepository @Inject constructor(){
    suspend fun isInternetAvailable(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val timeoutMs = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)

                socket.connect(socketAddress, timeoutMs)
                socket.close()

                true // Connection successful
            }
        } catch (e: IOException) {
            false // Connection failed
        }
    }
}