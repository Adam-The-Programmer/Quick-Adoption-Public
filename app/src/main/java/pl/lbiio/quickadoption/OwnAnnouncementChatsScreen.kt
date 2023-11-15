package pl.lbiio.quickadoption

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator

import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import pl.lbiio.quickadoption.data.OwnAnnouncementChat
import pl.lbiio.quickadoption.models.OwnChatsListViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun OwnAnnouncementChatsScreen(ownChatsListViewModel: OwnChatsListViewModel) {
    Scaffold(
        topBar = {
            SetChatsListTopBar(ownChatsListViewModel)
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            ChatsListContent(ownChatsListViewModel)
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
private fun SetChatsListTopBar(ownChatsListViewModel: OwnChatsListViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Chats")
        },
        navigationIcon = {
            IconButton(onClick = { ownChatsListViewModel.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        elevation = 4.dp
    )
}


@Composable
private fun ChatsListContent(ownChatsListViewModel: OwnChatsListViewModel) {

    LaunchedEffect(Unit){
        ownChatsListViewModel.fillListOfChats()
    }

//    val ownAnnouncementChats = listOf<OwnAnnouncementChat>(
//        OwnAnnouncementChat(
//            "1",
//            "1",
//            "Christiano",
//            "Ronaldo",
//            "https://bi.im-g.pl/im/52/f5/1b/z29318482Q,WCup-World-Cup-Photo-Gallery.jpg",
//            "I can adopt your dog jhosowgwegu ugtpwrgwutg",
//            "text",
//            1696161432450L,
//            "4",
//            0
//        ),
//        OwnAnnouncementChat(
//            "2",
//            "12",
//            "Adele",
//            "Adkins",
//            "https://bi.im-g.pl/im/d5/60/14/z21366229AMP,Adele.jpg",
//            "I will call you back!",
//            "text",
//            1696161432450L,
//            "4",
//            0
//        ),
//        OwnAnnouncementChat(
//            "3",
//            "40",
//            "Alvaro",
//            "Soler",
//            "https://bi.im-g.pl/im/11/06/1a/z27288081IER,Alvaro-Soler---2.jpg",
//            "See you!",
//            "text",
//            1696161432450L,
//            "AdamPiszczek",
//            1
//        )
//    )

    BoxWithConstraints(contentAlignment = Alignment.Center) {
        this.constraints

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
                    "Chats About ${ownChatsListViewModel.animalName.value}".uppercase(),
                    style = MaterialTheme.typography.subtitle1
                )
                Spacer(Modifier.width(12.dp))
                Text("${ownChatsListViewModel.ownChats.value.size} items".uppercase())
            }
            ownChatsListViewModel.ownChats.value.forEach {
                ChatsListItem(
                    announcementId = ownChatsListViewModel.announcementId.value,
                    ownAnnouncementChat = it,
                    onItemClick = { chatId ->
                        ownChatsListViewModel.navigateToChat(chatId)
                    },
                    onConsentGranted = { keeperId, announcementId ->

                    })
            }
        }

        if (!ownChatsListViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { ownChatsListViewModel.isFinished.value = true },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment= Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }



}


@Composable
private fun ChatsListItem(
    announcementId: Long,
    ownAnnouncementChat: OwnAnnouncementChat,
    onItemClick: (chatId: String) -> Unit,
    onConsentGranted: (keeperId: String, announcementId: Long) -> Unit
) {

    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Consent for Adoption")
                }
            },
            text = {
                Text(text = "Do You want ${ownAnnouncementChat.name} ${ownAnnouncementChat.surname} to take care of your pet?")
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(8.dp, 0.dp, 4.dp, 0.dp),
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Text("No")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 0.dp, 8.dp, 0.dp),
                        onClick = {
                            openDialog.value = false
                            onConsentGranted(ownAnnouncementChat.potentialKeeperID, announcementId)
                        }
                    ) {
                        Text("Yes")
                    }
                }
            }
        )
    }

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(ownAnnouncementChat.chatID) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Divider(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.3f))
            Row(
                Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.width(8.dp))
                AsyncImage(
                    model = ownAnnouncementChat.profileImage,
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight,
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
                            text = "${ownAnnouncementChat.name} ${ownAnnouncementChat.surname}",
                            style = MaterialTheme.typography.subtitle2,
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.Text(
                                text = if(ownAnnouncementChat.lastMessageAuthor=="AdamPiszczek") "You: " else "${ownAnnouncementChat.name}: ",
                                style = MaterialTheme.typography.caption,
                            )

                            androidx.compose.material3.Text(
                                modifier = Modifier.fillMaxWidth(0.5f),
                                text = if(ownAnnouncementChat.lastMessageContentType=="text")ownAnnouncementChat.lastMessageContent else "sends image",
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                    androidx.compose.material3.Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        text = timeSinceLastMessage(ownAnnouncementChat.lastMessageTimestamp),
                        style = MaterialTheme.typography.caption,
                    )

                }
            }
        }
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