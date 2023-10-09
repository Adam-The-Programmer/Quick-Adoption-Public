package pl.lbiio.quickadoption.data

data class PublicAnnouncementListItem(
    val animalId: Long,
    val name: String,
    val species: String,
    val breed: String,
    val dateRange: String,
    val artwork: String,
)
