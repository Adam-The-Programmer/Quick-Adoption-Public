package pl.lbiio.quickadoption.data

data class PublicAnnouncement(
    val ownerId: Long,
    val animalId: Long,
    val name: String,
    val species: String,
    val breed: String,
    val dateRange: String,
    val food: String,
    val artwork: String,
    val country: String,
    val city: String
)
