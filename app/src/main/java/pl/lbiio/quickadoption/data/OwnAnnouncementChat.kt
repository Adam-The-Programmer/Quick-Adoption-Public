package pl.lbiio.quickadoption.data

data class OwnAnnouncementChat (
    /*
    val chatId: String,
    val potentialKeeperId: Long,
    val name: String,
    val surname: String,
    val artwork: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val isAccepted: Int
    */


    val chatID: String,
    val potentialKeeperID: String,
    val name: String,
    val surname: String,
    val profileImage: String,
    val lastMessageContent: String,
    val lastMessageContentType: String,
    val lastMessageTimestamp: Long,
    val lastMessageAuthor: String,
    val isAccepted: Int

)