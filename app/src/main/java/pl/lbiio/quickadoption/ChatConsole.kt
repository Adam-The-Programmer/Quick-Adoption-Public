package pl.lbiio.quickadoption

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pl.lbiio.quickadoption.data.ChatMessage
import pl.lbiio.quickadoption.models.ChatConsoleViewModel
import pl.lbiio.quickadoption.models.ChatsListViewModel

@Composable
fun ChatConsole(chatConsoleViewModel: ChatConsoleViewModel) {
    Scaffold(
        topBar = {
            SetChatConsoleTopBar(chatConsoleViewModel)
        },
        bottomBar = {
            MessageSenderConsole({ value, type ->

            })
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            ChatConsoleContent(chatConsoleViewModel)
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
private fun SetChatConsoleTopBar(chatConsoleViewModel: ChatConsoleViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Chat")
        },
        navigationIcon = {
            IconButton(onClick = { chatConsoleViewModel.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        elevation = 4.dp
    )
}

@Composable
fun ChatConsoleContent(chatConsoleViewModel: ChatConsoleViewModel) {
    val messages = listOf<ChatMessage>(
        ChatMessage("g903r93rn9863", "Hello Adam", "text", 1696174281515L),
        ChatMessage("6t8b9ae639113", "Hi there!", "text", 1696174281550L),
        ChatMessage("g903r93rn9863", "https://bi.im-g.pl/im/52/f5/1b/z29318482Q,WCup-World-Cup-Photo-Gallery.jpg", "image", 1696174281590L)

        )
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 8.dp)) {
        messages.forEach {
            Message(isOwn = it.UID=="6t8b9ae639113", content = it.content, contentType = it.contentType)
        }
    }

}

@Preview
@Composable
private fun Message(isOwn: Boolean=true, content: String="https://bi.im-g.pl/im/52/f5/1b/z29318482Q,WCup-World-Cup-Photo-Gallery.jpg", contentType: String="image", howLongAgo: String = "1h") {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if(isOwn)Arrangement.End else Arrangement.Start) {
        Card(
            elevation = 10.dp,
            modifier = Modifier.padding(10.dp),
            backgroundColor = if(isOwn)pl.lbiio.quickadoption.ui.theme.PurpleBrown else Color.White,
            contentColor = if(isOwn) Color.White else pl.lbiio.quickadoption.ui.theme.PurpleBrown,
            shape = RoundedCornerShape(16.dp)
        ) {
            if (contentType == "text") {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Bottom) {
                    Text(text = content, modifier = Modifier.padding(0.dp,0.dp,20.dp,0.dp))
                    Text(text = howLongAgo, style = MaterialTheme.typography.caption)
                }
                
                
            } else {
                Column(horizontalAlignment = Alignment.End) {
                    AsyncImage(
                        model = content,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .size(200.dp)
                            .padding(10.dp)

                    )
                    Text(modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 4.dp), text = howLongAgo, style = MaterialTheme.typography.caption)
                }

            }

        }
    }

}

@Composable
private fun MessageSenderConsole(send: (value: String, type: String) -> Unit){

    var value by remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxWidth().padding(0.dp,0.dp,0.dp,6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.75f),
            value = value,
            shape = RoundedCornerShape(16.dp),
            onValueChange = {
                value = it
            },
            maxLines = 3
        )
        Icon(Icons.Default.Image, null, modifier = Modifier.padding(4.dp,0.dp,2.dp,6.dp).clickable {

        },
            tint = pl.lbiio.quickadoption.ui.theme.PurpleBrown
        )
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.padding(2.dp,0.dp,4.dp,6.dp), tint = pl.lbiio.quickadoption.ui.theme.PurpleBrown)
        }
    }
}