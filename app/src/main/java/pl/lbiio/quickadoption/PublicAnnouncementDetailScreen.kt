package pl.lbiio.quickadoption

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import pl.lbiio.quickadoption.models.PublicAnnouncementDetailsViewModel
import pl.lbiio.quickadoption.support.TopAppBarText
import pl.lbiio.quickadoption.ui.theme.PurpleBrown
import pl.lbiio.quickadoption.ui.theme.PurpleBrownLight

@Composable
fun PublicAnnouncementDetailScreen(publicAnnouncementDetailsViewModel: PublicAnnouncementDetailsViewModel) {
    val isAdoptingDialogOpened = remember { mutableStateOf(false) }

    if (isAdoptingDialogOpened.value) {
        AlertDialog(
            onDismissRequest = {
                isAdoptingDialogOpened.value = false
            },
            text = {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(text = "Send message", style = MaterialTheme.typography.subtitle1.copy(
                        PurpleBrownLight
                    ))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 8.dp, 0.dp, 0.dp),
                        value = publicAnnouncementDetailsViewModel.message.value,
                        placeholder = {
                            Text(
                                "Type your message",
                                style = MaterialTheme.typography.subtitle1.copy(PurpleBrownLight)
                            )
                        },
                        onValueChange = {
                            publicAnnouncementDetailsViewModel.message.value = it
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
                            isAdoptingDialogOpened.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 0.dp, 8.dp, 0.dp),
                        onClick = {
                            isAdoptingDialogOpened.value = false
                            publicAnnouncementDetailsViewModel.initConversation()
                        }
                    ) {
                        Text("Send")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SetPublicAnnouncementDetailsTopBar(publicAnnouncementDetailsViewModel)
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            PublicAnnouncementDetailsContent(
                publicAnnouncementDetailsViewModel,
                onApplyClick = {
                    isAdoptingDialogOpened.value = true
                }
            )
        },
    )
}


@Composable
private fun SetPublicAnnouncementDetailsTopBar(publicAnnouncementDetailsViewModel: PublicAnnouncementDetailsViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Announcement Details")
        },
        navigationIcon = {
            IconButton(onClick = { publicAnnouncementDetailsViewModel.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        elevation = 4.dp
    )
}

@Composable
private fun PublicAnnouncementDetailsContent(publicAnnouncementDetailsViewModel: PublicAnnouncementDetailsViewModel, onApplyClick: ()-> Unit) {

    BoxWithConstraints(contentAlignment = Alignment.Center) {
        this.constraints

        LaunchedEffect(Unit){
            publicAnnouncementDetailsViewModel.fillDetailsObject()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.Center) {
                Image(
                    painter = rememberAsyncImagePainter(publicAnnouncementDetailsViewModel.announcementDetails.value.animalImage),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(50.dp))
                )
            }

            Divider(
                modifier = Modifier.padding(64.dp, 10.dp, 64.dp, 10.dp),
                color = Color.Black,
                thickness = 1.dp
            )

            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(), textAlign = TextAlign.Center, text = publicAnnouncementDetailsViewModel.announcementDetails.value.dateRange
            )

            Divider(
                modifier = Modifier.padding(64.dp, 10.dp, 64.dp, 10.dp),
                color = Color.Black,
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Text(style = MaterialTheme.typography.subtitle1, text = "Description: ")
                Text(style = MaterialTheme.typography.body1, text = publicAnnouncementDetailsViewModel.announcementDetails.value.animalDescription)
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Text(style = MaterialTheme.typography.subtitle1, text = "Food: ")
                Text(style = MaterialTheme.typography.body1, text = publicAnnouncementDetailsViewModel.announcementDetails.value.food)
            }


            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.Center) {
                Image(
                    painter = rememberAsyncImagePainter(publicAnnouncementDetailsViewModel.announcementDetails.value.ownerImage),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(50.dp))

                )
                Card(
                    elevation = 10.dp,
                    modifier = Modifier.padding(8.dp,0.dp,0.dp,0.dp),
                    backgroundColor = Color.White,
                    contentColor = PurpleBrown,
                    shape = RoundedCornerShape(
                        topStart = 0f,
                        topEnd = 48f,
                        bottomStart = 48f,
                        bottomEnd = 48f
                    )
                ) {
                    Text(modifier = Modifier.padding(10.dp), text = publicAnnouncementDetailsViewModel.announcementDetails.value.ownerDescription)
                }
            }

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                onClick = {
                    onApplyClick()

                }) {
                Text(text = "Apply For Adoption")
            }

        }

        if (!publicAnnouncementDetailsViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { publicAnnouncementDetailsViewModel.isFinished.value = true },
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


