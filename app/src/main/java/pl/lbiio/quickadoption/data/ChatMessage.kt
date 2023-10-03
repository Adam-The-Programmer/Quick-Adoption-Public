package pl.lbiio.quickadoption.data

data class ChatMessage(
    val UID: String,
    val content: String,
    val contentType: String,
    val timestamp: Long
)
