package pl.lbiio.quickadoption.data

data class Chat (
    val chatId: Long,
    val name: String,
    val surname: String,
    val artwork: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long
)