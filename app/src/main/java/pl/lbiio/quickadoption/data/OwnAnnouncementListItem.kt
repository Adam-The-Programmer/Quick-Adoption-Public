package pl.lbiio.quickadoption.data

data class OwnAnnouncementListItem(
    val animalId: Long,
    val name: String,
    val species: String,
    val breed: String,
    val dateRange: String,
    val artwork: String,
    val hasNewOffer: Boolean,
    val numberOfOffers: Int
)
