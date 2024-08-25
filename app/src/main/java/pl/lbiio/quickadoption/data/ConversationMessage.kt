package pl.lbiio.quickadoption.data

sealed class ConversationMessage {

    data class ProvidedMessage(
        val UID: String,
        val content: String,
        val contentType: String,
        val timestamp: Long
    ) : ConversationMessage()

    data class PendingMessage(
        val content: String,
        val contentType: String
    ) : ConversationMessage()
}
