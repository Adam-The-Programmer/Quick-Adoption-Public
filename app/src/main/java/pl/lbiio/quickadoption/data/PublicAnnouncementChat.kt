package pl.lbiio.quickadoption.data

data class PublicAnnouncementChat(
    val chatID: String,
    val announcementID: Long,
    val dateRange: String,
    val animalName: String,
    val breed: String,
    val species: String,
    val ownerID: String,
    val name: String,
    val surname: String,
    val profileImage: String,
    val lastMessageContent: String,
    val lastMessageContentType: String,
    val lastMessageTimestamp: Long,
    val lastMessageAuthor: String,
    val isChatAccepted: Int
)
