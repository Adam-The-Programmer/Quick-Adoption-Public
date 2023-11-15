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


    object ChatConsoleScreen : Destination("chat", "chat_id", "is_chat_own") {
        const val CHAT_ID_KEY = "chat_id"
        const val IS_CHAT_OWN_KEY = "is_chat_own"

        operator fun invoke(chatId: String, isChatOwn: Boolean): String = route.appendParams(
            CHAT_ID_KEY to chatId,
            IS_CHAT_OWN_KEY to isChatOwn
        )
    }

    object PublicOfferDetailsScreen : Destination("public_offer_details", "announcement_id") {
        const val ANNOUNCEMENT_ID_KEY = "announcement_id"

        operator fun invoke(announcementId: Long): String = route.appendParams(
            ANNOUNCEMENT_ID_KEY to announcementId
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