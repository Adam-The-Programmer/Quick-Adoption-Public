package pl.lbiio.quickadoption.data

data class PublicAnnouncementChat(
    val chatId: Long,
    val announcementId: Long,
    val ownerId: Long,
    val name: String,
    val surname: String,
    val artwork: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val isAccepted: Int
)
