package pl.lbiio.quickadoption.data

data class Announcement(
    val ownerId: Long,
    val announcementId: Long,
    val name: String,
    val species: String,
    val breed: String,
    val dateRange: String,
    val food: String,
    val artwork: String,
)
