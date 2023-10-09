package pl.lbiio.quickadoption

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import pl.lbiio.quickadoption.data.ChatMessage
import pl.lbiio.quickadoption.models.ChatConsoleViewModel
import java.io.File

@Composable
fun ChatConsole(chatConsoleViewModel: ChatConsoleViewModel) {
    Scaffold(
        topBar = {
            SetChatConsoleTopBar(chatConsoleViewModel)
        },
        bottomBar = {
            MessageSenderConsole { value, type ->
                Toast.makeText(QuickAdoptionApp.getAppContext(), value, Toast.LENGTH_SHORT).show()
            }
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
        ChatMessage(
            "g903r93rn9863",
            "https://bi.im-g.pl/im/52/f5/1b/z29318482Q,WCup-World-Cup-Photo-Gallery.jpg",
            "image",
            1696174281590L
        )

    )
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 8.dp)
    ) {
        messages.forEach {
            Message(
                isOwn = it.UID == "6t8b9ae639113",
                content = it.content,
                contentType = it.contentType
            )
        }
    }

}

@Preview
@Composable
private fun Message(
    isOwn: Boolean = true,
    content: String = "hello adam",
    contentType: String = "test",
    howLongAgo: String = "1h"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        Card(
            elevation = 10.dp,
            modifier = Modifier.padding(10.dp),
            backgroundColor = if (isOwn) pl.lbiio.quickadoption.ui.theme.PurpleBrown else Color.White,
            contentColor = if (isOwn) Color.White else pl.lbiio.quickadoption.ui.theme.PurpleBrown,
            shape = RoundedCornerShape(
                topStart = 48f,
                topEnd = 48f,
                bottomStart = if (isOwn) 48f else 0f,
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
                    Box(Modifier.padding(10.dp)){
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
    //var artwork by remember { mutableStateOf("") }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            Log.d("uri", it.toString())
            selectedImage = listOf(it) as List<Uri>
            path = getFilePath(QuickAdoptionApp.getAppContext(), it!!)!!
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
                                        // Asking for permission
                                        launcher.launch(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                }
                            },
                        tint = pl.lbiio.quickadoption.ui.theme.PurpleBrown
                    )
                    Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier
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

private fun getFilePath(context: Context, uri: Uri): String? {
    val isMediaDocument = uri.authority == "com.android.providers.media.documents"
    if (isMediaDocument) {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":").toTypedArray()
        val type = split[0]
        var contentUri: Uri? = null
        if ("image" == type) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else if ("video" == type) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else if ("audio" == type) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])
        return getDataColumn(context, contentUri, selection, selectionArgs)
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

private fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}
