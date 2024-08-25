package pl.lbiio.quickadoption.data

data class OwnAnnouncementChat (
    val chatID: String,
    val potentialKeeperID: String,
    val name: String,
    val surname: String,
    val profileImage: String,
    val lastMessageContent: String,
    val lastMessageContentType: String,
    val lastMessageTimestamp: Long,
    val lastMessageAuthor: String,
    val isChatAccepted: Int,
    val average: Double
)