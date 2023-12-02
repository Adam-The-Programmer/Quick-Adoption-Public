package pl.lbiio.quickadoption

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import pl.lbiio.quickadoption.models.ChatConsoleViewModel
import pl.lbiio.quickadoption.support.RatingBar
import pl.lbiio.quickadoption.support.TopAppBarText
import pl.lbiio.quickadoption.ui.theme.PurpleBrownLight
import java.io.File

@Composable
fun ChatConsole(chatConsoleViewModel: ChatConsoleViewModel) {
    Scaffold(
        topBar = {
            SetChatConsoleTopBar(chatConsoleViewModel)
        },
        bottomBar = {
            MessageSenderConsole { value, type ->
                chatConsoleViewModel.uploadMessage(value, type)
            }
        },
        backgroundColor = Color.White,
        content = {innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                ChatConsoleContent(chatConsoleViewModel)
            }
        },
    )
}





@Composable
private fun SetChatConsoleTopBar(chatConsoleViewModel: ChatConsoleViewModel) {

    val isAssigningDialogOpened = remember { mutableStateOf(false) }

    if (isAssigningDialogOpened.value) {
        AlertDialog(
            onDismissRequest = {
                isAssigningDialogOpened.value = false
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Assigning keeper")
                }
            },
            text = {
                Text(text = "Do you want ${chatConsoleViewModel.potentialKeeperName.value} to be a keeper of your animal? \n\nThis operation cannot be undone")
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
                            isAssigningDialogOpened.value = false
                        }
                    ) {
                        Text("NO")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 0.dp, 8.dp, 0.dp),
                        onClick = {
                            isAssigningDialogOpened.value = false
                            chatConsoleViewModel.acceptChatAndAssignUser()
                        }
                    ) {
                        Text("YES")
                    }
                }
            }
        )
    }


    val isRatingDialogOpened = remember { mutableStateOf(false) }

    if (isRatingDialogOpened.value) {
        var myRating by remember { mutableIntStateOf(0) }
        var opinion by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                isRatingDialogOpened.value = false
            },
            text = {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(text = "Rating ${chatConsoleViewModel.potentialKeeperName.value}", style = MaterialTheme.typography.subtitle1.copy(PurpleBrownLight))

                    RatingBar(
                        currentRating = myRating,
                        onRatingChanged = { myRating = it }
                    )



                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 8.dp, 0.dp, 0.dp),
                        value = opinion,
                        placeholder = {
                            Text(
                                "Type your opinion",
                                style = MaterialTheme.typography.subtitle1.copy(PurpleBrownLight)
                            )
                        },
                        onValueChange = {
                            opinion = it
                        },
                    )
                }


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
                            isRatingDialogOpened.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 0.dp, 8.dp, 0.dp),
                        onClick = {
                            isRatingDialogOpened.value = false
                            chatConsoleViewModel.rate(myRating, opinion)
                        }
                    ) {
                        Text("Send")
                    }
                }
            }
        )
    }


    TopAppBar(
        title = {
            TopAppBarText(text = "Chat")
        },
        navigationIcon = {
            IconButton(onClick = { chatConsoleViewModel.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        actions = {
            if(chatConsoleViewModel.isChatOwn.value){
                IconButton(onClick = {
                    chatConsoleViewModel.navigateToOpinions()
                }) {
                    Icon(Icons.Default.Info, null, tint = Color.White)
                }
                IconButton(onClick = {
                    isAssigningDialogOpened.value = true
                }) {
                    Icon(Icons.Default.Check, null, tint = Color.White)
                }
                IconButton(onClick = {
                    isRatingDialogOpened.value = true
                }) {
                    Icon(Icons.Default.StarRate, null, tint = Color.White)
                }
            }
        },
        elevation = 4.dp
    )
}

@Composable
fun ChatConsoleContent(chatConsoleViewModel: ChatConsoleViewModel) {

    LaunchedEffect(Unit){
        chatConsoleViewModel.listenToMessages()
        Log.d("image message", chatConsoleViewModel.potentialKeeperImage.value)
    }

    BoxWithConstraints(contentAlignment = Alignment.Center) {
        this.constraints

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            chatConsoleViewModel.conversation.value.forEach {
                Message(
                    isOwn = it.UID == QuickAdoptionApp.getCurrentUserId(),
                    content = it.content,
                    contentType = it.contentType,
                    potentialKeeperImage = chatConsoleViewModel.potentialKeeperImage.value,
                    howLongAgo = QuickAdoptionApp.calculateTimeDifference(it.timestamp)
                )
            }
        }

        if (!chatConsoleViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { chatConsoleViewModel.isFinished.value = true },
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
private fun Message(
    isOwn: Boolean,
    content: String,
    contentType: String,
    potentialKeeperImage: String,
    howLongAgo: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        if(!isOwn){
            AsyncImage(
                model = potentialKeeperImage,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(50.dp))

            )
        }
        Card(
            elevation = 10.dp,
            modifier = Modifier.padding(10.dp),
            backgroundColor = if (isOwn) pl.lbiio.quickadoption.ui.theme.PurpleBrown else Color.White,
            contentColor = if (isOwn) Color.White else pl.lbiio.quickadoption.ui.theme.PurpleBrown,
            shape = RoundedCornerShape(
                topStart =if(isOwn) 48f else 0f,
                topEnd = 48f,
                bottomStart = 48f,
                bottomEnd = if (isOwn) 0f else 48f
            )
        ) {
            if (contentType == "text") {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Bottom) {
                    Text(text = content, modifier = Modifier.padding(0.dp, 0.dp, 20.dp, 0.dp))
                    Text(text = howLongAgo, style = MaterialTheme.typography.caption)
                }


            } else {
                Column(horizontalAlignment = Alignment.End) {
                    Box(Modifier.padding(10.dp)) {
                        AsyncImage(
                            model = content,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .size(200.dp)
                        )
                    }

                    Text(
                        modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 4.dp),
                        text = howLongAgo,
                        style = MaterialTheme.typography.caption
                    )
                }

            }

        }
    }

}

@Composable
private fun MessageSenderConsole(send: (value: String, type: String) -> Unit) {

    var value by remember { mutableStateOf("") }
    var path by remember { mutableStateOf("") }
    var isDialogShown by remember { mutableStateOf(false) }

    var selectedImage by remember { mutableStateOf(listOf<Uri>()) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            Log.d("uri", it.toString())
            selectedImage = listOf(it) as List<Uri>
            path = QuickAdoptionApp.getFilePath(QuickAdoptionApp.getAppContext(), it!!)!!
            isDialogShown = true
        }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("notification", "PERMISSION GRANTED")
        } else {
            Log.d("notification", "PERMISSION DENIED")
        }
    }
    if (isDialogShown) {
        ImageSendDialog(selectedImage, path, {
            isDialogShown = false
        }, {
            send(path, "image")
        })
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp, 6.dp, 6.dp, 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            shape = RoundedCornerShape(16.dp),
            placeholder = {
                Text(text = "Type Your Message here...")
            },
            trailingIcon = {
                Row() {
                    Icon(
                        Icons.Default.Image, null, modifier = Modifier
                            .padding(8.dp, 0.dp, 6.dp, 0.dp)
                            .clickable {

                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        QuickAdoptionApp.getAppContext(),
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
                                    ) -> {
                                        galleryLauncher.launch("image/*")
                                    }
                                    else -> {
                                        launcher.launch(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                }
                            },
                        tint = pl.lbiio.quickadoption.ui.theme.PurpleBrown
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.Send, null, modifier = Modifier
                            .padding(6.dp, 0.dp, 8.dp, 0.dp)
                            .clickable {
                                send(value, "text")
                                value = ""
                            }, tint = pl.lbiio.quickadoption.ui.theme.PurpleBrown
                    )
                }
            },
            onValueChange = {
                value = it
            },
            maxLines = 3
        )
    }
}

@Composable
private fun ImageSendDialog(
    selectedImage: List<Uri>,
    path: String,
    onFinish: () -> Unit,
    send: (path: String) -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    val painter: Painter = if (selectedImage.isEmpty() && path.isEmpty()) {
        painterResource(id = R.drawable.ic_image)
    } else {
        val bitmap: Bitmap = BitmapFactory.decodeFile(
            File(path).absolutePath,
            BitmapFactory.Options()
        )
        val imageBitmap: ImageBitmap = bitmap.asImageBitmap()
        BitmapPainter(imageBitmap)
    }

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
                    Text(text = "Do you want to send Image?")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )
                }
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
                            onFinish()
                            openDialog.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 0.dp, 8.dp, 0.dp),
                        onClick = {
                            openDialog.value = false
                            onFinish()
                            send(path)
                        }
                    ) {
                        Text("Send")
                    }
                }
            }
        )
    }
}
