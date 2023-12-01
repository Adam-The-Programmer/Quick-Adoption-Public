package pl.lbiio.quickadoption

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import pl.lbiio.quickadoption.data.Opinion
import pl.lbiio.quickadoption.models.OpinionsViewModel
import pl.lbiio.quickadoption.models.PublicChatsListViewModel
import pl.lbiio.quickadoption.ui.theme.PurpleBrown
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Composable
fun OpinionsScreen(opinionsViewModel: OpinionsViewModel){
    Scaffold(
        topBar = {
            SetOpinionsScreenTopBar(opinionsViewModel)
        },
        backgroundColor = Color.White,
        content = {innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                OpinionsScreenContent(opinionsViewModel)
            }
        },
    )
}

@Composable
fun OpinionsScreenContent(opinionsViewModel: OpinionsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        LaunchedEffect(Unit){
            opinionsViewModel.getKeeperRate(opinionsViewModel.receiverID.value)
            opinionsViewModel.fillListOfOpinions(opinionsViewModel.receiverID.value)
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 64.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(0.dp,16.dp,0.dp,0.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Text(text = opinionsViewModel.opinions.value.size.toString(), style = MaterialTheme.typography.h4)
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = PurpleBrown,
                    modifier = Modifier
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "${opinionsViewModel.rate.value}/5", style = MaterialTheme.typography.h4)
                Stars(
                    number = opinionsViewModel.rate.value.roundToInt()
                )
            }
            Spacer(modifier = Modifier.height(30.dp))


            opinionsViewModel.opinions.value.forEach{ opinion ->
                OpinionItem(opinion)
            }
        }

    }

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
private fun SetOpinionsScreenTopBar(opinionsViewModel: OpinionsViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Opinions")
        },navigationIcon = {
            IconButton(onClick = {opinionsViewModel.navigateUp()}) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        elevation = 4.dp
    )
}


@Composable
private fun OpinionItem(opinion: Opinion){
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
                Column(Modifier.padding(6.dp)){
                    Stars(
                        number = opinion.rateStars
                    )
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Bottom) {
                        Text(text = opinion.content, modifier = Modifier
                            .padding(0.dp, 0.dp, 20.dp, 0.dp)
                            .fillMaxWidth(0.6f))
                        Text(text = convertToDate(opinion.timestamp), style = MaterialTheme.typography.caption)
                    }
                }


            }
        }


}

private fun convertToDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return sdf.format(calendar.time)


}


@Composable
private fun Stars(
    maxRating: Int = 5,
    number: Int,
    starsColor: Color = PurpleBrown
) {
    Row(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= number) Icons.Filled.Star
                else Icons.Filled.StarOutline,
                contentDescription = null,
                tint = if (i <= number) starsColor
                else Color.Unspecified,
                modifier = Modifier
                    .padding(4.dp)
            )
        }
    }
}