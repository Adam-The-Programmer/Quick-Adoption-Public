package pl.lbiio.quickadoption


import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.data.OwnAnnouncementListItem
import pl.lbiio.quickadoption.data.PublicAnnouncementListItem
import pl.lbiio.quickadoption.enums.OwnAnnouncementStatus
import pl.lbiio.quickadoption.models.TabbedAnnouncementsViewModel
import pl.lbiio.quickadoption.support.*
import pl.lbiio.quickadoption.ui.theme.Active
import pl.lbiio.quickadoption.ui.theme.PurpleBrownLight
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val actionBrokenByInternetLoss = mutableStateOf(0) // 0-loading own 1-loading public
private val isInternetNotAvailable = mutableStateOf(false)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabbedAnnouncementsScreen(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {

    Scaffold(
        topBar = {
            SetMainActivityTopBar(tabbedAnnouncementsViewModel)
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            TabbedAnnouncementsContent(tabbedAnnouncementsViewModel)
        },
    )
}

@Composable
private fun SetMainActivityTopBar(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Quick Adoption App")
        },
        actions = {
            // Adding an action icon
            IconButton(onClick = {
                tabbedAnnouncementsViewModel.navigateToLeaderBoard()
            }) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,  // Using an icon that could represent an award
                    contentDescription = "Award Cup"
                )
            }
            IconButton(onClick = {
                tabbedAnnouncementsViewModel.navigateToOwnOpinions()
            }) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Opinions"
                )
            }
            IconButton(onClick = {
                tabbedAnnouncementsViewModel.navigateToEditingAccountScreen()
            }) {
                Icon(
                    imageVector = Icons.Filled.ManageAccounts,
                    contentDescription = "Personal Data"
                )
            }
        },
        elevation = 0.dp
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TabbedAnnouncementsContent(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {

    if(isInternetNotAvailable.value){
        AlertDialog(
            onDismissRequest = {
                isInternetNotAvailable.value = false
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material.Text(text = "Internet Connection Is Lost")
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
                            isInternetNotAvailable.value = false
                        }
                    ) {
                        androidx.compose.material.Text("Dismiss")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 0.dp, 8.dp, 0.dp),
                        onClick = {
                            isInternetNotAvailable.value = false
                            when(actionBrokenByInternetLoss.value){
                                0->{
                                    tabbedAnnouncementsViewModel.populateOwnAnnouncementsList {
                                        isInternetNotAvailable.value = true
                                    }
                                }
                                1->{
                                    tabbedAnnouncementsViewModel.populatePublicAnnouncementsList {
                                        isInternetNotAvailable.value = true
                                    }
                                }
                            }

                        }
                    ) {
                        androidx.compose.material.Text("Reload")
                    }
                }
            }
        )
    }


   LaunchedEffect(Unit){
       actionBrokenByInternetLoss.value = 0
       tabbedAnnouncementsViewModel.populateOwnAnnouncementsList{
           isInternetNotAvailable.value = true
       }
   }

    val tabs = listOf("Own", "Public")

    val scaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Concealed)

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(shadowElevation = 4.dp) {
            TabRow(selectedTabIndex = tabbedAnnouncementsViewModel.tabIndex.value) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = {
                        Text(
                            text = title, style = MaterialTheme.typography.subtitle1.copy(
                                Color.White
                            )
                        )
                    },
                        selected = tabbedAnnouncementsViewModel.tabIndex.value == index,
                        onClick = {
                            tabbedAnnouncementsViewModel.tabIndex.value = index
                        }
                    )
                }
            }
        }

        BackdropScaffold(
            scaffoldState = scaffoldState,
            frontLayerShape = RoundedCornerShape(0.dp),
            gesturesEnabled = tabbedAnnouncementsViewModel.tabIndex.value == 1,
            appBar = {

            },
            backLayerContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SearchField("Country", Icons.Filled.MyLocation, tabbedAnnouncementsViewModel.country.value) {
                        tabbedAnnouncementsViewModel.country.value = it
                    }
                    SearchField("City", Icons.Filled.LocationCity, tabbedAnnouncementsViewModel.city.value) {
                        tabbedAnnouncementsViewModel.city.value = it
                    }
                    DateRangePickerSample(tabbedAnnouncementsViewModel)
                    Button(modifier = Modifier
                        .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = pl.lbiio.quickadoption.ui.theme.Salmon,
                            contentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                            disabledBackgroundColor = pl.lbiio.quickadoption.ui.theme.Salmon,
                            disabledContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown
                        ),
                        onClick = {
                            if (tabbedAnnouncementsViewModel.country.value.isNotEmpty() && tabbedAnnouncementsViewModel.city.value.isNotEmpty() && tabbedAnnouncementsViewModel.dateRange.value.isNotEmpty()) {
                                actionBrokenByInternetLoss.value = 1
                                tabbedAnnouncementsViewModel.populatePublicAnnouncementsList{
                                    isInternetNotAvailable.value = true
                                }
                                scope.launch {
                                    scaffoldState.conceal()
                                }
                            } else {
                                Toast.makeText(
                                    QuickAdoptionApp.getAppContext(),
                                    "Fill all fields!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                        Text(text = "Search")
                    }

                }
            },
            frontLayerContent = {
                when (tabbedAnnouncementsViewModel.tabIndex.value) {

                    0 -> {
                        OwnScreen(tabbedAnnouncementsViewModel)
                        scope.launch {
                            scaffoldState.conceal()
                        }

                    }

                    1 -> {
                        PublicScreen(tabbedAnnouncementsViewModel)
                        scope.launch {
                            scaffoldState.reveal()
                        }
                    }
                }

            },
            peekHeight = 0.dp,
            ) {

        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OwnScreen(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {

    BoxWithConstraints(
        Modifier.fillMaxSize(),
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 64.dp)
        ) {

            if(tabbedAnnouncementsViewModel.isFinished.value && tabbedAnnouncementsViewModel.ownAnnouncementsList.value.isNotEmpty() && !isInternetNotAvailable.value){
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
                    Text("${tabbedAnnouncementsViewModel.ownAnnouncementsList.value.size} items".uppercase(), style = MaterialTheme.typography.subtitle2.copy(PurpleBrownLight))
                }

                val categoryToAnnouncement = categorizeAnnouncements(tabbedAnnouncementsViewModel.ownAnnouncementsList.value)

                categoryToAnnouncement.forEach { (category, announcements) ->
                    if (announcements.isNotEmpty()) {
                        SpinnerList(category, announcements, tabbedAnnouncementsViewModel)
                    }
                }
            }
            else if(tabbedAnnouncementsViewModel.isFinished.value && tabbedAnnouncementsViewModel.ownAnnouncementsList.value.isEmpty() && !isInternetNotAvailable.value){
                Text(text = "No Announcements published yet!")
            }
        }

        FloatingActionButton(
            onClick = {
                tabbedAnnouncementsViewModel.navigateToPublicAnnouncementsChats()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp, 24.dp, 24.dp, 90.dp)
        ) {
            Icon(Icons.Filled.Message, "")
        }

        FloatingActionButton(
            onClick = { tabbedAnnouncementsViewModel.navigateToInsertingForm() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Filled.Add, "")
        }

        if (!tabbedAnnouncementsViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { tabbedAnnouncementsViewModel.isFinished.value = true },
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SpinnerList(
    category: OwnAnnouncementStatus,
    announcements: List<OwnAnnouncementListItem>,
    viewModel: TabbedAnnouncementsViewModel
) {
    var expanded by remember { mutableStateOf(true) }

    ListItem(
        modifier = Modifier.clickable { expanded = !expanded },
        text = { Text(text = category.name.replace("_", " ")) },
        trailing = {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }
    )
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(animationSpec = spring()) + fadeIn(initialAlpha = 0.3f),
        exit = shrinkVertically(animationSpec = spring()) + fadeOut()
    ) {
        Column {
            announcements.forEach { announcement ->
                OwnAnnouncementListItem(announcement,
                    { announcementId, name ->
                        viewModel.navigateToChatsList(announcementId, name)
                    }, { item ->
                        viewModel.navigateToEditingForm(item)
                    },
                    { announcementId ->
                        Log.d("id ogloszenia", announcementId.toString())
                        viewModel.deleteAnnouncementAndRefresh(announcementId)
                    }
                )
            }
        }
    }
}

@Composable
private fun PublicScreen(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {

    BoxWithConstraints(contentAlignment = Alignment.Center) {
        this.constraints
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 64.dp)
        ) {


            tabbedAnnouncementsViewModel.publicAnnouncementsList.value.forEach { announcement ->
                PublicAnnouncement(announcement) { announcementId ->
                    tabbedAnnouncementsViewModel.navigateToPublicOffer(announcementId)
                }
            }

        }

        if (!tabbedAnnouncementsViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { tabbedAnnouncementsViewModel.isFinished.value = true },
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
private fun PublicAnnouncement(
    announcement: PublicAnnouncementListItem,
    onItemClick: (announcementId: Long) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(announcement.announcementID) },
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
                    model = announcement.animalImage,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
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
                            text = "${announcement.animalName} - ${announcement.species} - ${announcement.breed}",
                            style = MaterialTheme.typography.body1,
                        )

                        Text(
                            text = "${announcement.country} - ${announcement.city}",
                            style = MaterialTheme.typography.body1,
                        )

                        Text(
                            text = announcement.dateRange,
                            style = MaterialTheme.typography.subtitle2,
                        )

                    }
                }
            }
        }
    }
}

@Composable
private fun OwnAnnouncementListItem(
    ownAnnouncementListItem: OwnAnnouncementListItem,
    onItemClick: (announcementId: Long, name: String) -> Unit,
    onEditClick: (announcementId: Long) -> Unit,
    onRemoveClick: (animalId: Long) -> Unit,
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val color by infiniteTransition.animateColor(
        initialValue = Color.Transparent,
        targetValue = Active,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    val openDialog = remember { mutableStateOf(false) }

    if(openDialog.value){
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material.Text(text = "Removing Announcement")
                }
            },
            text = {
                androidx.compose.material.Text(text = "Do you want to delete Announcement?")
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
                            openDialog.value = false
                        }
                    ) {
                        androidx.compose.material.Text("Cancel")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 0.dp, 8.dp, 0.dp),
                        onClick = {
                            openDialog.value = false
                            onRemoveClick(ownAnnouncementListItem.announcementID)
                        }
                    ) {
                        androidx.compose.material.Text("OK")
                    }
                }
            }
        )
    }

    Row(
        Modifier
            .drawBehind {
                if(ownAnnouncementListItem.isInProgress)drawRect(color)
            }
            .fillMaxWidth()
            .clickable {
                onItemClick(
                    ownAnnouncementListItem.announcementID,
                    ownAnnouncementListItem.animalName
                )
            },
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
                    model = ownAnnouncementListItem.animalImage,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
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
                            text = "Name: ${ownAnnouncementListItem.animalName}\nSpecies: ${ownAnnouncementListItem.species}\nBreed: ${ownAnnouncementListItem.breed}",
                            style = MaterialTheme.typography.body1,
                        )

                        Text(
                            text = "Period: ${ownAnnouncementListItem.dateRange}",
                            style = MaterialTheme.typography.subtitle2,
                        )

                    }

                    //Card(elevation = 0.dp, backgroundColor = pl.lbiio.quickadoption.ui.theme.Salmon, border = BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(30.dp)) {
                    Text(
                        color = if (ownAnnouncementListItem.hasNewOffer) Color.Red else Color.Gray,
                        modifier = Modifier.padding(3.dp),
                        text = ownAnnouncementListItem.numberOfOffers.toString()
                    )
                    //}
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
                                onEditClick(ownAnnouncementListItem.announcementID)
                            })
                            {
                                Text("Edit")
                            }
                            Divider(thickness = 1.dp)
                            DropdownMenuItem(
                                onClick = {
                                    expanded = !expanded
                                    openDialog.value = true
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

@RequiresApi(Build.VERSION_CODES.O)
private fun categorizeAnnouncements(announcements: List<OwnAnnouncementListItem>): Map<OwnAnnouncementStatus, List<OwnAnnouncementListItem>> {
    val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val today = LocalDate.now()

    val waiting = mutableListOf<OwnAnnouncementListItem>()
    val outOfDate = mutableListOf<OwnAnnouncementListItem>()
    val inProgress = mutableListOf<OwnAnnouncementListItem>()

    announcements.forEach { item ->
        val (startStr, endStr) = item.dateRange.split("-")
        val startDate = LocalDate.parse(startStr.trim(), dateFormat)
        val endDate = LocalDate.parse(endStr.trim(), dateFormat)

        when {
            endDate.isBefore(today) -> outOfDate.add(item)
            startDate.isAfter(today) || (today in startDate..endDate && !item.isInProgress) -> waiting.add(item)
            today in startDate..endDate && item.isInProgress -> inProgress.add(item)
        }
    }

    return mapOf(
        OwnAnnouncementStatus.WAITING to waiting,
        OwnAnnouncementStatus.OUT_OF_DATE to outOfDate,
        OwnAnnouncementStatus.IN_PROGRESS to inProgress
    )
}

