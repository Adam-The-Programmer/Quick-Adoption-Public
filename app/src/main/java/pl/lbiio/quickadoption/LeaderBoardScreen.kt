package pl.lbiio.quickadoption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import pl.lbiio.quickadoption.data.LeaderBoardItem
import pl.lbiio.quickadoption.models.LeaderBoardViewModel
import pl.lbiio.quickadoption.support.TopAppBarText

@Composable
fun LeaderBoardScreen(leaderBoardViewModel: LeaderBoardViewModel) {
    Scaffold(
        topBar = {
            SetLeaderBoardScreenTopBar(leaderBoardViewModel)
        },
        backgroundColor = Color.White,
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                LeaderBoardScreenContent(leaderBoardViewModel)
            }
        },
    )
}

@Composable
fun LeaderBoardScreenContent(leaderBoardViewModel: LeaderBoardViewModel) {

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
                        leaderBoardViewModel.inflateInterfaceWithData {
                            isInternetNotAvailable.value = true
                        }
                    }) {
                    Text("Reload")
                }
            }
        })
    }

    BoxWithConstraints(contentAlignment = Alignment.Center) {
        this.constraints

        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            LaunchedEffect(Unit) {
                leaderBoardViewModel.inflateInterfaceWithData {
                    isInternetNotAvailable.value = true
                }
            }

            LazyColumn(state = rememberLazyListState()) {
                items(leaderBoardViewModel.leaderBoard.value) { leaderBoardItem ->
                    LeaderBoardListItem(leaderBoardItem)
                }
            }

        }
        if (!leaderBoardViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { leaderBoardViewModel.isFinished.value = true },
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
private fun SetLeaderBoardScreenTopBar(leaderBoardViewModel: LeaderBoardViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Leader Board")
        }, navigationIcon = {
            IconButton(onClick = { leaderBoardViewModel.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        elevation = 4.dp
    )
}

@Preview
@Composable
private fun LeaderBoardListItem(item: LeaderBoardItem = LeaderBoardItem(1, "gold", "UID", "Adam", "Piszczek", "https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg", 4.8, 10, 100)){
    val backgroundColor = when (item.color) {
        "gold" -> Color(0xFFC9AD1A)  // Using hex code for gold color
        "silver" -> Color(0xFFC0C0C0)  // Silver color
        "bronze" -> Color(0xFF8F5217)  // Bronze color
        "green" -> Color.Green  // Default color constant for green
        else -> Color.White  // Default for any other or unspecified color
    }

    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.index.toString(), fontSize = 16.sp)

            Spacer(modifier = Modifier.width(16.dp))

            AsyncImage(
                model = item.image, // For Picasso, use rememberPicassoPainter
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = "${item.name}\n${item.surname}")

            Spacer(modifier = Modifier.weight(1f))


            Text(text = "${item.amount}")

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Outlined.Message,
                contentDescription = "Award Cup"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = "${item.average}")

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Award Cup"
            )
        }
    }
}
