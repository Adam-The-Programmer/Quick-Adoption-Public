package pl.lbiio.quickadoption.data

data class OwnAnnouncementChat (
    val chatId: Long,
    val potentialKeeperId: Long,
    val name: String,
    val surname: String,
    val artwork: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val isAccepted: Int
)