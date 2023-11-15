package pl.lbiio.quickadoption.data

data class PublicAnnouncementListItem(
    val announcementID: Long,
    val animalName: String,
    val species: String,
    val breed: String,
    val dateRange: String,
    val animalImage: String,
    val country: String,
    val city: String
)
