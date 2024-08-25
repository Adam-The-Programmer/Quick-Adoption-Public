package pl.lbiio.quickadoption.data

data class LeaderBoardItem(
    val index: Int,
    val color: String,
    val UID: String,
    val name: String,
    val surname: String,
    val image: String,
    val average: Double,
    val amount: Int,
    val totalSum: Int
)
