package pl.lbiio.quickadoption.navigation

sealed class Destination(protected val route: String, vararg params: String) {
    val fullRoute: String = if (params.isEmpty()) route else {
        val builder = StringBuilder(route)
        params.forEach { builder.append("/{${it}}") }
        builder.toString()
    }

    sealed class NoArgumentsDestination(route: String) : Destination(route) {
        operator fun invoke(): String = route
    }

    object RegistrationScreen : NoArgumentsDestination("registration_screen")

    object LoginScreen : NoArgumentsDestination("login_screen")

    object TabbedScreen : NoArgumentsDestination("tabbed_screen")

    object AnnouncementFormScreen : NoArgumentsDestination("announcement_form_screen")

    object LeaderBoardScreen : NoArgumentsDestination("leader_board_screen")


    object AnnouncementEditScreen : Destination(
        "announcement_form_screen",
        "announcement_id"
    ) {
        const val ANNOUNCEMENT_ID_KEY = "announcement_id"

        operator fun invoke(announcementId: Long): String = route.appendParams(
            ANNOUNCEMENT_ID_KEY to announcementId,
        )
    }

    object ChatsScreen : Destination("chats", "announcement_id", "animal_name") {
        const val ANNOUNCEMENT_ID_KEY = "announcement_id"
        const val ANIMAL_NAME_KEY = "animal_name"

        operator fun invoke(announcementId: Long, animalName: String): String = route.appendParams(
            ANNOUNCEMENT_ID_KEY to announcementId,
            ANIMAL_NAME_KEY to animalName
        )
    }

    object PublicAnnouncementsChatsScreen : NoArgumentsDestination("public_announcements_chats")

    object EditingAccountScreen : NoArgumentsDestination("editing_account")


    object ChatConsoleScreen : Destination("chat", "chat_id", "announcement_id", "is_chat_own", "partner_name", "partner_image", "partner_uid") {
        const val CHAT_ID_KEY = "chat_id"
        const val ANNOUNCEMENT_ID_KEY = "announcement_id"
        const val IS_CHAT_OWN_KEY = "is_chat_own"
        const val PARTNER_NAME_KEY = "partner_name"
        const val PARTNER_IMAGE_KEY = "partner_image"
        const val PARTNER_UID_KEY = "partner_uid"


        operator fun invoke(chatId: String, announcementId: Long, isChatOwn: Boolean, partnerName: String, partnerImage: String, partnerUID: String): String = route.appendParams(
            CHAT_ID_KEY to chatId,
            ANNOUNCEMENT_ID_KEY to announcementId,
            IS_CHAT_OWN_KEY to isChatOwn,
            PARTNER_NAME_KEY to partnerName,
            PARTNER_IMAGE_KEY to partnerImage,
            PARTNER_UID_KEY to partnerUID
        )
    }

    object PublicOfferDetailsScreen : Destination("public_offer_details", "announcement_id") {
        const val ANNOUNCEMENT_ID_KEY = "announcement_id"

        operator fun invoke(announcementId: Long): String = route.appendParams(
            ANNOUNCEMENT_ID_KEY to announcementId
        )
    }

    object OpinionsScreen : Destination("opinions_screen", "uid") {
        const val UID_KEY = "uid"

        operator fun invoke(uid: String): String = route.appendParams(
            UID_KEY to uid
        )
    }
}

internal fun String.appendParams(vararg params: Pair<String, Any?>): String {
    val builder = StringBuilder(this)

    params.forEach {
        it.second.toString().let { arg ->
            builder.append("/$arg")
        }
    }

    return builder.toString()
}