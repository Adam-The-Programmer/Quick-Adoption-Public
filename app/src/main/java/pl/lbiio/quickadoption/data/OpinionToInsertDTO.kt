package pl.lbiio.quickadoption.data

data class OpinionToInsertDTO(
    val authorID: String,
    val receiverID: String,
    val rate: Int,
    val content: String
)