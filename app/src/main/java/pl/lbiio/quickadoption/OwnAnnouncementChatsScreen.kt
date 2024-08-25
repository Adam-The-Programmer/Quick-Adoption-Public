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
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import pl.lbiio.quickadoption.data.OwnAnnouncementChat
import pl.lbiio.quickadoption.models.OwnChatsListViewModel
import pl.lbiio.quickadoption.support.TopAppBarText

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

    val isInternetNotAvailable = remember { mutableStateOf(false) }

    if (isInternetNotAvailable.value) {
        AlertDialog(onDismissRequest = {
            isInternetNotAvailable.value = false
        }, title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Internet Connection Is Lost")
            }
        }, buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(8.dp, 0.dp, 4.dp, 0.dp),
                    onClick = {
                        isInternetNotAvailable.value = false
                    }) {
                    Text("Dismiss")
                }
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp, 0.dp, 8.dp, 0.dp),
                    onClick = {
                        isInternetNotAvailable.value = false
                        ownChatsListViewModel.fillListOfChats {
                            isInternetNotAvailable.value = true
                        }
                    }) {
                    Text("Reload")
                }
            }
        })
    }

    LaunchedEffect(Unit) {
        ownChatsListViewModel.fillListOfChats {
            isInternetNotAvailable.value = true
        }
    }

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
                PublicAnnouncementListItem(
                    ownAnnouncementChat = it,
                    onItemClick = { chatId ->
                        ownChatsListViewModel.navigateToChat(chatId)
                    },
                )
            }
        }

        if (!ownChatsListViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { ownChatsListViewModel.isFinished.value = true },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
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
private fun PublicAnnouncementListItem(
    ownAnnouncementChat: OwnAnnouncementChat,
    onItemClick: (chatId: String) -> Unit,

    ) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(ownAnnouncementChat.chatID) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.3f))
            Row(
                Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.width(8.dp))
                AsyncImage(
                    model = ownAnnouncementChat.profileImage,
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.Text(
                                text = "${ownAnnouncementChat.name} ${ownAnnouncementChat.surname}",
                                style = MaterialTheme.typography.subtitle1,
                            )
                            if (ownAnnouncementChat.average != 0.0) {
                                Spacer(modifier = Modifier.width(12.dp))
                                androidx.compose.material3.Text(
                                    text = ownAnnouncementChat.average.toString(),
                                    style = MaterialTheme.typography.subtitle1,
                                )
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFE7C40C)
                                )
                            }
                        }


                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.Text(
                                text = if (ownAnnouncementChat.lastMessageAuthor == QuickAdoptionApp.getCurrentUserId()) "You: " else "${ownAnnouncementChat.name}: ",
                                style = MaterialTheme.typography.caption,
                            )

                            androidx.compose.material3.Text(
                                modifier = Modifier.fillMaxWidth(0.5f),
                                text = if (ownAnnouncementChat.lastMessageContentType == "text") ownAnnouncementChat.lastMessageContent else "sends image",
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Text(
                            text = QuickAdoptionApp.calculateTimeDifference(ownAnnouncementChat.lastMessageTimestamp),
                            style = MaterialTheme.typography.caption,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        when (ownAnnouncementChat.isChatAccepted) {
                            1 -> {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Green
                                )
                            }

                            -1 -> {
                                Icon(
                                    imageVector = Icons.Default.QuestionMark,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }

                            0 -> {
                                Icon(
                                    imageVector = Icons.Outlined.Cancel,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }
    }
}
