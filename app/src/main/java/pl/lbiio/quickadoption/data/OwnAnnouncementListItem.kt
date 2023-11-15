package pl.lbiio.quickadoption.data

data class OwnAnnouncementListItem(
    val announcementID: Long,
    val animalName: String,
    val species: String,
    val breed: String,
    val dateRange: String,
    val animalImage: String,
    val hasNewOffer: Boolean,
    val numberOfOffers: Int
)
