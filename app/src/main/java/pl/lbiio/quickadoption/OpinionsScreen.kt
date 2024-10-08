package pl.lbiio.quickadoption

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import pl.lbiio.quickadoption.data.Opinion
import pl.lbiio.quickadoption.models.OpinionsViewModel
import pl.lbiio.quickadoption.support.Stars
import pl.lbiio.quickadoption.support.TopAppBarText
import pl.lbiio.quickadoption.ui.theme.PurpleBrown
import kotlin.math.roundToInt

@Composable
fun OpinionsScreen(opinionsViewModel: OpinionsViewModel) {
    Scaffold(
        topBar = {
            SetOpinionsScreenTopBar(opinionsViewModel)
        },
        backgroundColor = Color.White,
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                OpinionsScreenContent(opinionsViewModel)
            }
        },
    )
}

@Composable
fun OpinionsScreenContent(opinionsViewModel: OpinionsViewModel) {

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
                        opinionsViewModel.inflateInterfaceWithData {
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
                opinionsViewModel.inflateInterfaceWithData {
                    isInternetNotAvailable.value = true
                }
            }

            if (opinionsViewModel.opinions.value.isEmpty() && opinionsViewModel.isFinished.value && !isInternetNotAvailable.value) {
                Text(text = "This user has no opinions yet!")
            } else if (opinionsViewModel.opinions.value.isNotEmpty() && opinionsViewModel.isFinished.value && !isInternetNotAvailable.value) {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 64.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 16.dp, 0.dp, 0.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opinionsViewModel.opinions.value.size.toString(),
                            style = MaterialTheme.typography.h4
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = PurpleBrown,
                            modifier = Modifier
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${opinionsViewModel.rate.value}/5",
                            style = MaterialTheme.typography.h4
                        )
                        Stars(
                            number = opinionsViewModel.rate.value.roundToInt()
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))


                    opinionsViewModel.opinions.value.forEach { opinion ->
                        OpinionItem(opinion)
                    }
                }
            }


        }
        if (!opinionsViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { opinionsViewModel.isFinished.value = true },
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
private fun SetOpinionsScreenTopBar(opinionsViewModel: OpinionsViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Opinions")
        }, navigationIcon = {
            IconButton(onClick = { opinionsViewModel.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        elevation = 4.dp
    )
}


@Composable
private fun OpinionItem(opinion: Opinion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = Arrangement.Start
    ) {

        Image(
            painter = rememberAsyncImagePainter(opinion.authorImage),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(50.dp))

        )

        Card(
            elevation = 10.dp,
            modifier = Modifier.padding(10.dp),
            backgroundColor = Color.White,
            contentColor = PurpleBrown,
            shape = RoundedCornerShape(
                topStart = 0f,
                topEnd = 48f,
                bottomStart = 48f,
                bottomEnd = 48f
            )
        ) {
            Column(Modifier.padding(6.dp)) {
                Stars(
                    number = opinion.rateStars
                )
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = opinion.content, modifier = Modifier
                            .padding(0.dp, 0.dp, 20.dp, 0.dp)
                            .fillMaxWidth(0.6f)
                    )
                    Text(
                        text = QuickAdoptionApp.convertToDate(opinion.timestamp),
                        style = MaterialTheme.typography.caption
                    )
                }
            }


        }
    }
}