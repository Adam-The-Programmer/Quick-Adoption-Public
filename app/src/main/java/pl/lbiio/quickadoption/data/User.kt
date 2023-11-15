package pl.lbiio.quickadoption.data

data class User(
    val uid: String,
    val emailAddress: String,
    val phone: String,
    val name: String,
    val surname: String,
    val country: String,
    val city: String,
    val address: String,
    val postalCode: String,
    val userDescription: String,
    val profileImage: String,
    val maxReliability: String,
    val acquiredReliability: String
)
