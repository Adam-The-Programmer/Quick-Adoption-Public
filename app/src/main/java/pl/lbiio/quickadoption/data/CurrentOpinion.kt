package pl.lbiio.quickadoption.data

data class CurrentOpinion(
    val opinionID: Int = -1,
    val content: String = "",
    val rateStars: Int = 0
)