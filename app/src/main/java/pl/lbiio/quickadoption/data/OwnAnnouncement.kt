package pl.lbiio.quickadoption.data

data class OwnAnnouncement(
    val ownerId: Long,
    val animalId: Long,
    val name: String,
    val species: String,
    val breed: String,
    val dateRange: String,
    val food: String,
    val artwork: String,
    val assignedKeeperId: Long,
    val hasNewOffer: Boolean,
    val numberOfOffers: Int

)
