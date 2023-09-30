package pl.lbiio.quickadoption

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pl.lbiio.quickadoption.data.OwnAnnouncement
import pl.lbiio.quickadoption.models.TabbedAnnouncementsViewModel


@Composable
fun TabbedAnnouncementsScreen(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel){
    Scaffold(
        topBar = {
            SetMainActivityTopBar()
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            TabbedAnnouncementsContent(tabbedAnnouncementsViewModel)
        },
    )
}

@Composable
private fun TopAppBarText(
    modifier: Modifier = Modifier,
    text: String
) {
    androidx.compose.material.Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.subtitle1,
        fontSize = 17.sp
    )
}

@Composable
private fun SetMainActivityTopBar(){
    TopAppBar(
        title = {
            TopAppBarText(text = "Quick Adoption App")
        },
        elevation = 0.dp
    )
}

@Composable
private fun TabbedAnnouncementsContent(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel){
    var tabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf("Own", "Public")

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(shadowElevation = 4.dp) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(text = title, style = MaterialTheme.typography.subtitle1.copy(
                        Color.White)) },
                        selected = tabIndex == index,
                        onClick = {
                            tabIndex = index
                        }
                    )
                }
            }
        }
        when (tabIndex) {
            0 -> OwnScreen(tabbedAnnouncementsViewModel)
            1 -> PublicScreen(tabbedAnnouncementsViewModel)
        }
    }
}

@Composable
private fun OwnScreen(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel){
    BoxWithConstraints(
        Modifier.fillMaxSize()) {
        Column(Modifier.verticalScroll(rememberScrollState()).padding(bottom=64.dp)) {
            Row(
                modifier = Modifier
                    .padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Your Announcements".uppercase(),
                    style = MaterialTheme.typography.subtitle1
                )
                Spacer(Modifier.width(12.dp))
                androidx.compose.material.Text("3 items".uppercase())
            }

            val animals = listOf<OwnAnnouncement>(
                OwnAnnouncement("Alex Dog Labrador", "07.11.2023 - 11.11.2023", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/34/Labrador_on_Quantock_%282175262184%29.jpg/800px-Labrador_on_Quantock_%282175262184%29.jpg"),
                OwnAnnouncement("Dorry Parrot Ara", "08.11.2023 - 17.11.2023", "https://delasign.com/delasignBlack.png")
            )

            animals.forEach {
                OwnAnnouncementListItem(
                    it.animal,
                    it.period,
                    it.artwork,
                    {

                    }, {

                    })
            }


        }

        FloatingActionButton(
            onClick = { tabbedAnnouncementsViewModel.navigateToInsertingForm() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ){
            Icon(Icons.Filled.Add, "")
        }
    }
}

@Composable
private fun PublicScreen(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel){
    Text(
        text = "Public",
        style = MaterialTheme.typography.subtitle1,
        fontSize = 16.sp,
    )
}


@OptIn(ExperimentalCoilApi::class)
@Composable
private fun OwnAnnouncementListItem(
    animal: String,
    period: String,
    artworkUrl: String,
    onItemClick: () -> Unit,
    onEditClick: () -> Unit
){
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
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
                    model = artworkUrl,
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

                        Text(
                            text = "Animal: $animal",
                            style = MaterialTheme.typography.body1,
                        )

                        Text(
                            text = "Period: $period",
                            style = MaterialTheme.typography.subtitle2,
                        )

                    }
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = {
                            expanded = !expanded
                        },
                        Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_more),
                            contentDescription = "",
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(onClick =
                            {
                                expanded = !expanded
                                onEditClick()
                            })
                            {
                                Text("Edit")
                            }
                            Divider(thickness = 1.dp)
                            DropdownMenuItem(onClick =
                            {
                                expanded = !expanded
                                MaterialAlertDialogBuilder(
                                    QuickAdoptionApp.getAppContext(),
                                    R.style.ThemeOverlay_App_MaterialAlertDialog
                                )
                                    .setTitle("Removing Item")
                                    .setMessage("Are you sure to remove item")
                                    .setNegativeButton("Cancel") { dialog, which ->
                                    }
                                    .setPositiveButton("Confirm") { dialog, which ->
                                        //onRemoveClicked()
                                    }.show()
                            })
                            {
                                Text("Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}