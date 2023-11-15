package pl.lbiio.quickadoption.data

data class ApplicationForAdoptionDTO(
    val announcementID: String,
    val chatID: String,
    val ownerID: String,
    val keeperID: String,
    val lastMessageContent: String,
    val lastMessageContentType: String,
    val lastMessageAuthor: String
)
