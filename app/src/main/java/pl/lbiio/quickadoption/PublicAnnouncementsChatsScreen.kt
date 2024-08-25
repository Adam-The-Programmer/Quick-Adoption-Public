package pl.lbiio.quickadoption

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
//import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import pl.lbiio.quickadoption.data.PublicAnnouncementChat
import pl.lbiio.quickadoption.enums.PublicAnnouncementStatus
import pl.lbiio.quickadoption.models.PublicChatsListViewModel
import pl.lbiio.quickadoption.support.TopAppBarText
import pl.lbiio.quickadoption.ui.theme.PurpleBrownLight
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun PublicAnnouncementsChatsContent(publicChatsListViewModel: PublicChatsListViewModel) {
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
                        publicChatsListViewModel.fillListOfChats {
                            isInternetNotAvailable.value = true
                        }
                    }) {
                    Text("Reload")
                }
            }
        })
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        LaunchedEffect(Unit){
            publicChatsListViewModel.fillListOfChats {
                isInternetNotAvailable.value = true
            }
        }

        BoxWithConstraints {
            this.constraints

            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 64.dp)
            ) {
                if(publicChatsListViewModel.isFinished.value && publicChatsListViewModel.publicChats.value.isNotEmpty() && !isInternetNotAvailable.value){
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Public Offers Chats".uppercase(),
                            style = MaterialTheme.typography.subtitle1
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("${publicChatsListViewModel.publicChats.value.size} items".uppercase(), style = MaterialTheme.typography.subtitle2.copy(PurpleBrownLight))
                    }

                    val categoryToAnnouncement = categorizeAnnouncements(publicChatsListViewModel.publicChats.value)

                    categoryToAnnouncement.forEach { (category, announcements) ->
                        if (announcements.isNotEmpty()) {
                            SpinnerList(category, announcements, publicChatsListViewModel)
                        }
                    }
                }
                else if(publicChatsListViewModel.isFinished.value && publicChatsListViewModel.publicChats.value.isEmpty() && !isInternetNotAvailable.value){
                    Text("You don't have any public chats yet!")
                }

            }

            if (!publicChatsListViewModel.isFinished.value) {
                Dialog(
                    onDismissRequest = { publicChatsListViewModel.isFinished.value = true },
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SpinnerList(
    category: PublicAnnouncementStatus,
    announcements: List<PublicAnnouncementChat>,
    publicChatsListViewModel: PublicChatsListViewModel
) {
    var expanded by remember { mutableStateOf(true) }

    ListItem(
        modifier = Modifier.clickable { expanded = !expanded },
        text = { Text(text = category.name.replace("_", " ")) },
        trailing = {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }
    )
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(animationSpec = spring()) + fadeIn(initialAlpha = 0.3f),
        exit = shrinkVertically(animationSpec = spring()) + fadeOut()
    ) {
        Column {
            announcements.forEach {
                PublicAnnouncementListItem(publicAnnouncementChat = it, onItemClick = { chatId ->
                    publicChatsListViewModel.navigateToChat(chatId)
                })
            }
        }
    }
}


@Composable
private fun PublicAnnouncementListItem(
    publicAnnouncementChat: PublicAnnouncementChat,
    onItemClick: (chatId: String) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(publicAnnouncementChat.chatID) },
        verticalAlignment = Alignment.CenterVertically
    ) {
            Card(shape = RoundedCornerShape(8.dp), elevation = 10.dp, modifier = Modifier.padding(4.dp, 4.dp, 4.dp, 4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                    Column(
                        Modifier
                            .fillMaxWidth(0.7f)
                            .padding(16.dp, 16.dp, 0.dp, 16.dp)) {
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
                                    Modifier.padding(end = 16.dp).fillMaxWidth(0.7f)
                                ) {

                                    Text(
                                        text = "${publicAnnouncementChat.name} ${publicAnnouncementChat.surname}",
                                        style = MaterialTheme.typography.subtitle2,
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (publicAnnouncementChat.lastMessageAuthor == QuickAdoptionApp.getCurrentUserId()) "You: " else "${publicAnnouncementChat.name}: ",
                                            style = MaterialTheme.typography.caption,
                                        )

                                        Text(
                                            modifier = Modifier.fillMaxWidth(0.6f),
                                            text = publicAnnouncementChat.lastMessageContent,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1,
                                            style = MaterialTheme.typography.body1
                                        )

                                        HorizontalDivider(modifier = Modifier.width(8.dp), thickness = 0.dp, color = Color.Transparent)


                                    }
                                }

                                Text(
                                    text = QuickAdoptionApp.calculateTimeDifference(publicAnnouncementChat.lastMessageTimestamp),
                                    style = MaterialTheme.typography.caption,
                                )

                            }
                        }
                        Text(text = publicAnnouncementChat.dateRange, style = MaterialTheme.typography.subtitle1.copy(PurpleBrownLight))
                    }

                    when (publicAnnouncementChat.isChatAccepted) {
                        1 -> {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Green)
                        }
                        -1 -> {
                            Icon(imageVector = Icons.Default.QuestionMark, contentDescription = null, tint = Color.Gray)
                        }
                        0 -> {
                            Icon(imageVector = Icons.Outlined.Cancel, contentDescription = null, tint = Color.Red)
                        }
                    }

//                    Icon(
//                        imageVector = when (publicAnnouncementChat.isChatAccepted) {
//                            0 -> Icons.Outlined.Cancel
//                            1 -> Icons.Outlined.Check
//                            else -> Icons.Outlined.QuestionMark
//                        },
//                        contentDescription = null,
//                        tint = when (publicAnnouncementChat.isChatAccepted) {
//                            0 -> Color.Red
//                            1 -> Color.Green
//                            else -> Color.Gray
//                        }
//                    )
                }
            }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun categorizeAnnouncements(announcements: List<PublicAnnouncementChat>): Map<PublicAnnouncementStatus, List<PublicAnnouncementChat>> {
    val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val today = LocalDate.now()

    val waiting = mutableListOf<PublicAnnouncementChat>()
    val dismissed = mutableListOf<PublicAnnouncementChat>()
    val inProgress = mutableListOf<PublicAnnouncementChat>()

    announcements.forEach { item ->
        val (startStr, endStr) = item.dateRange.split("-")
        val startDate = LocalDate.parse(startStr.trim(), dateFormat)
        val endDate = LocalDate.parse(endStr.trim(), dateFormat)

        when {
            item.isChatAccepted == 0 -> dismissed.add(item)
            today in startDate..endDate && item.isChatAccepted == 1 -> inProgress.add(item)
            startDate.isAfter(today) || (today in startDate..endDate && (item.isChatAccepted == 1 || item.isChatAccepted == -1)) -> waiting.add(item)
        }
    }

    return mapOf(
        PublicAnnouncementStatus.WAITING to waiting,
        PublicAnnouncementStatus.DISMISSED to dismissed,
        PublicAnnouncementStatus.IN_PROGRESS to inProgress
    )
}


