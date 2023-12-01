package pl.lbiio.quickadoption.data

data class Opinion(
    val opinionID: Long,
    val authorName: String,
    val authorSurname: String,
    val authorImage: String,
    val content: String,
    val timestamp: Long,
    val rateStars: Int
)