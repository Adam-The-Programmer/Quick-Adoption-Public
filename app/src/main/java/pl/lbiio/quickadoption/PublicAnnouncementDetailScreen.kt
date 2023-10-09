package pl.lbiio.quickadoption

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.models.LoginViewModel
import pl.lbiio.quickadoption.ui.theme.PurpleBrown
import pl.lbiio.quickadoption.ui.theme.Salmon

@Composable
fun PublicAnnouncementDetailScreen(animalId: Long) {
    Scaffold(
        topBar = {
            SetPublicAnnouncementDetailsTopBar()
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            PublicAnnouncementDetailsContent(
                animalImage = "https:**upload.wikimedia.org*wikipedia*commons*thumb*3*34*Labrador_on_Quantock_%282175262184%29.jpg*800px-Labrador_on_Quantock_%282175262184%29.jpg",
                ownerImage = "https:**bi.im-g.pl*im*11*06*1a*z27288081IER,Alvaro-Soler---2.jpg",
                date = "08.11.2023 - 23.11.2023",
                description = "Alex is a calm dog which doesn't like staying alone in home",
                food = "Fodder, Meat and Fish",
                ownerDescription = "I look after my dog since 2018.\nSometimes I need a person who can take over from me",
                onApplyClick = {

                }
            )
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
private fun SetPublicAnnouncementDetailsTopBar() {
    TopAppBar(
        title = {
            TopAppBarText(text = "Announcement Details")
        },
        elevation = 4.dp
    )
}

@Composable
private fun PublicAnnouncementDetailsContent(animalImage: String, ownerImage: String, date: String, description: String, food: String, ownerDescription: String, onApplyClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        //horizontalAlignment = Alignment.CenterHorizontally,
    ) {

Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
    Image(
        painter = rememberAsyncImagePainter(decodePathFile(animalImage)),
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
                .fillMaxWidth(), textAlign = TextAlign.Center, text = date
        )

        Divider(
            modifier = Modifier.padding(64.dp, 10.dp, 64.dp, 10.dp),
            color = Color.Black,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ){
            Text(style = MaterialTheme.typography.subtitle1, text = "Description: ")
            Text(style = MaterialTheme.typography.body1, text = description)
        }


        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ){
            Text(style = MaterialTheme.typography.subtitle1, text = "Food: ")
            Text(style = MaterialTheme.typography.body1, text = food)
        }


        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.Center) {
            Image(
                painter = rememberAsyncImagePainter(decodePathFile(ownerImage)),
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
                    Text(modifier = Modifier.padding(10.dp), text = ownerDescription)
            }
        }

        Button(modifier = Modifier
            .fillMaxWidth().padding(8.dp),
            onClick = {
                onApplyClick()

            }) {
            Text(text = "Apply For Adoption")
        }

    }

}

private fun decodePathFile(codedPath: String): String {
    return codedPath.replace("*", "/")
}

