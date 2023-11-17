package pl.lbiio.quickadoption

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pl.lbiio.quickadoption.data.PublicAnnouncementChat
import pl.lbiio.quickadoption.models.OwnChatsListViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.Card
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import pl.lbiio.quickadoption.models.PublicChatsListViewModel
import pl.lbiio.quickadoption.ui.theme.PurpleBrownLight

@Composable
fun PublicAnnouncementsChatsScreen(publicChatsListViewModel: PublicChatsListViewModel) {
    Scaffold(
        topBar = {
            SetPublicAnnouncementsChatsTopBar(publicChatsListViewModel)
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            PublicAnnouncementsChatsContent(publicChatsListViewModel)
        },
    )
}

@Composable
private fun TopAppBarText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.subtitle1,
        fontSize = 17.sp
    )
}

@Composable
private fun SetPublicAnnouncementsChatsTopBar(publicChatsListViewModel: PublicChatsListViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Announcement Details")
        },navigationIcon = {
            IconButton(onClick = {publicChatsListViewModel.navigateUp()}) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        elevation = 4.dp
    )
}

@Composable
private fun PublicAnnouncementsChatsContent(publicChatsListViewModel: PublicChatsListViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {

//        val publicAnnouncementChats = listOf<PublicAnnouncementChat>(
//            PublicAnnouncementChat(
//                "1",
//                12L,
//                "20",
//                "Christiano",
//                "Ronaldo",
//                "https://bi.im-g.pl/im/52/f5/1b/z29318482Q,WCup-World-Cup-Photo-Gallery.jpg",
//                "I can adopt your dog jhosowgwegu ugtpwrgwutg",
//                "text",
//                1696161432450L,
//                "10",
//                1
//            ),
//            PublicAnnouncementChat(
//                "2",
//                12L,
//                "23",
//                "Adele",
//                "Adkins",
//                "https://bi.im-g.pl/im/d5/60/14/z21366229AMP,Adele.jpg",
//                "I will call you back!",
//                "text",
//                1696161434450L,
//                "16",
//                1
//            ),
//            PublicAnnouncementChat(
//                "3",
//                12L,
//                "24",
//                "Alvaro",
//                "Soler",
//                "https://bi.im-g.pl/im/11/06/1a/z27288081IER,Alvaro-Soler---2.jpg",
//                "See you!",
//                "text",
//                1696161432450L,
//                "10",
//                1
//            )
//        )
        LaunchedEffect(Unit){
            publicChatsListViewModel.fillListOfChats()
        }


        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 64.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    "Public Offers Chats".uppercase(),
                    style = MaterialTheme.typography.subtitle1
                )
                Spacer(Modifier.width(12.dp))
                androidx.compose.material.Text("3 items".uppercase())
            }
            publicChatsListViewModel.publicChats.value.forEach {
                ChatsListItem(publicAnnouncementChat = it, onItemClick = { chatId ->
                    publicChatsListViewModel.navigateToChat(chatId)
                })
            }
        }

    }

}


@Composable
private fun ChatsListItem(
    publicAnnouncementChat: PublicAnnouncementChat,
    onItemClick: (chatId: String) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(publicAnnouncementChat.chatID) },
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Column(
//            Modifier.fillMaxWidth()
//        ) {

            Card(shape = RoundedCornerShape(8.dp), elevation = 10.dp, modifier = Modifier.padding(4.dp, 4.dp, 4.dp, 4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                    Column(Modifier.fillMaxWidth(0.7f).padding(0.dp, 16.dp, 0.dp, 0.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${publicAnnouncementChat.animalName} - ${publicAnnouncementChat.breed} - ${publicAnnouncementChat.species}", style = MaterialTheme.typography.subtitle1.copy(PurpleBrownLight))
                        Row(
                            Modifier.padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(Modifier.width(8.dp))
                            AsyncImage(
                                model = publicAnnouncementChat.profileImage,
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(60.dp)
                                    .clip(RoundedCornerShape(12.dp))

                            )
                            Spacer(Modifier.width(20.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column(
                                    Modifier.padding(end = 16.dp)
                                ) {

                                    androidx.compose.material3.Text(
                                        text = "${publicAnnouncementChat.name} ${publicAnnouncementChat.surname}",
                                        style = MaterialTheme.typography.subtitle2,
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        androidx.compose.material3.Text(
                                            text = "${publicAnnouncementChat.name}: ",
                                            style = MaterialTheme.typography.caption,
                                        )

                                        androidx.compose.material3.Text(
                                            modifier = Modifier.fillMaxWidth(0.6f),
                                            text = publicAnnouncementChat.lastMessageContent,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1,
                                            style = MaterialTheme.typography.body1
                                        )

                                        HorizontalDivider(modifier = Modifier.width(8.dp), thickness = 0.dp, color = Color.Transparent)


                                    }
                                }

                                androidx.compose.material3.Text(
                                    //modifier = Modifier.fillMaxWidth(0.4f),
                                    text = timeSinceLastMessage(publicAnnouncementChat.lastMessageTimestamp),
                                    style = MaterialTheme.typography.caption,
                                )

                            }
                        }
                    }
                    Icon(
                        imageVector = when (publicAnnouncementChat.isChatAccepted) {
                            0 -> Icons.Outlined.Cancel
                            1 -> Icons.Outlined.Check
                            else -> Icons.Outlined.QuestionMark
                        },
                        contentDescription = null,
                        tint = when (publicAnnouncementChat.isChatAccepted) {
                            0 -> Color.Red
                            1 -> Color.Green
                            else -> Color.Gray
                        }
                    )
                }
            }


        //}
    }
}


private fun timeSinceLastMessage(timestamp: Long): String {
    val currentTimeMillis = System.currentTimeMillis()
    val timeDifferenceMillis = currentTimeMillis - timestamp

    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)
    val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis)

    return when {
        seconds < 60 -> "${seconds}s ago"
        minutes < 60 -> "${minutes}min ago"
        hours < 24 -> "${hours}h ago"
        days < 365 -> "${days}d ago"
        else -> {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            sdf.format(calendar.time)
        }
    }
}