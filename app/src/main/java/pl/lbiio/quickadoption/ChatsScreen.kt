package pl.lbiio.quickadoption

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import pl.lbiio.quickadoption.data.Chat
import pl.lbiio.quickadoption.models.ChatsListViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun ChatsScreen(chatsListViewModel: ChatsListViewModel) {
    Scaffold(
        topBar = {
            SetChatsListTopBar(chatsListViewModel)
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            ChatsListContent(chatsListViewModel)
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
private fun SetChatsListTopBar(chatsListViewModel: ChatsListViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Chats")
        },
        navigationIcon = {
            IconButton(onClick = { chatsListViewModel.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        elevation = 4.dp
    )
}


@Composable
private fun ChatsListContent(chatsListViewModel: ChatsListViewModel) {

    val chats = listOf<Chat>(
        Chat(
            1L,
            "Christiano",
            "Ronaldo",
            "https://bi.im-g.pl/im/52/f5/1b/z29318482Q,WCup-World-Cup-Photo-Gallery.jpg",
            "I can adopt your dog jhosowgwegu ugtpwrgwutg",
            1696131451850L
        ),
        Chat(
            2L,
            "Adele",
            "Adkins",
            "https://bi.im-g.pl/im/d5/60/14/z21366229AMP,Adele.jpg",
            "I will call you back!",
            1696161432450L
        )
    )

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
                "Your Chats".uppercase(),
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(Modifier.width(12.dp))
            androidx.compose.material.Text("3 items".uppercase())
        }
        chats.forEach {
            ChatsListItem(chat = it, onItemClick = {chatId ->
                chatsListViewModel.navigateToChat(chatId)
            })
        }
    }

}


@Composable
private fun ChatsListItem(chat: Chat, onItemClick: (chatId: Long) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(chat.chatId) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Divider(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.3f))
            Row(
                Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(8.dp))
                AsyncImage(
                    model = chat.artwork,
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
                            text = "${chat.name} ${chat.surname}",
                            style = MaterialTheme.typography.subtitle2,
                        )

                        //Box(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.material3.Text(
                                    text = "${chat.name}: ",
                                    style = MaterialTheme.typography.caption,
                                )

                                androidx.compose.material3.Text(
                                    modifier = Modifier.fillMaxWidth(0.7f),
                                    text = chat.lastMessage,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.body1
                                )

                                Divider(modifier = Modifier.width(8.dp))

                                androidx.compose.material3.Text(
                                    text = timeSinceLastMessage(chat.lastMessageTimestamp),
                                    style = MaterialTheme.typography.caption,
                                )

                            }
                        //}
                    }
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