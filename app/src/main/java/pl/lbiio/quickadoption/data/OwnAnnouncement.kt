package pl.lbiio.quickadoption.data

data class OwnAnnouncement(
    val announcementID: Long,
    val animalName: String,
    val species: String,
    val breed: String,
    val dateRange: String,
    val food: String,
    val animalImage: String,
    val animalDescription: String
)
