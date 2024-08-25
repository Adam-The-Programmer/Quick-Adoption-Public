package pl.lbiio.quickadoption.data

data class UserCurrentData(
    val phone: String,
    val name: String,
    val surname: String,
    val country: String,
    val city: String,
    val address: String,
    val postalCode: String,
    val userDescription: String,
    var profileImage: String
)
