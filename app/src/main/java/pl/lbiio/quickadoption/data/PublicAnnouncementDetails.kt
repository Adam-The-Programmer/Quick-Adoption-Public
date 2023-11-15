package pl.lbiio.quickadoption.data

data class PublicAnnouncementDetails(
    val announcementId: Long,
    val ownerID: String,
    val dateRange: String,
    val food: String,
    val animalImage: String,
    val animalDescription: String,
    val ownerDescription: String,
    val ownerImage: String,
    val country: String,
    val city: String

//    val announcementId: Long,
//    val ownerID: String,
//    val dateRange: String,
//    val food: String,
//    val animalImage: String,
//    val animalDescription: String,
//    val ownerDescription: String,
//    val ownerImage: String,
//    val country: String,
//    val city: String
)
