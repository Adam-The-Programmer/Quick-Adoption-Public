package pl.lbiio.quickadoption

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material.Button
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.lbiio.quickadoption.models.TabbedAnnouncementsViewModel
import pl.lbiio.quickadoption.support.RangeDateFormatter
import java.text.SimpleDateFormat
import java.util.Locale


import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import pl.lbiio.quickadoption.data.OwnAnnouncementListItem
import pl.lbiio.quickadoption.data.PublicAnnouncementDetails
import pl.lbiio.quickadoption.data.PublicAnnouncementListItem
import java.time.LocalDate


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TabbedAnnouncementsScreen(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {

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
private fun SetMainActivityTopBar() {
    TopAppBar(
        title = {
            TopAppBarText(text = "Quick Adoption App")
        },
        elevation = 0.dp
    )
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TabbedAnnouncementsContent(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {
   LaunchedEffect(Unit){
       tabbedAnnouncementsViewModel.populateOwnAnnouncementsList()
   }

    var tabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf("Own", "Public")

    val scaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Concealed)

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(shadowElevation = 4.dp) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = {
                        Text(
                            text = title, style = MaterialTheme.typography.subtitle1.copy(
                                Color.White
                            )
                        )
                    },
                        selected = tabIndex == index,
                        onClick = {
                            tabIndex = index
                        }
                    )
                }
            }
        }

        BackdropScaffold(
            scaffoldState = scaffoldState,
            frontLayerShape = RoundedCornerShape(0.dp),
            gesturesEnabled = tabIndex == 1,
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
                                tabbedAnnouncementsViewModel.populatePublicAnnouncementsList()
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
                when (tabIndex) {

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

@Composable
private fun OwnScreen(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {
    BoxWithConstraints(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 64.dp)
        ) {
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
                androidx.compose.material.Text("${tabbedAnnouncementsViewModel.ownAnnouncementsList.value.size} items".uppercase())
            }

            tabbedAnnouncementsViewModel.ownAnnouncementsList.value.forEach {
                OwnAnnouncementListItem(
                    it,
                    { announcementId, name ->
                        tabbedAnnouncementsViewModel.navigateToChatsList(announcementId, name)
                    }, { announcement ->
                        tabbedAnnouncementsViewModel.navigateToEditingForm(announcement)
                    },
                    {

                    })
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
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PublicScreen(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 64.dp)
        ) {

//            val announcements = listOf(
//                PublicAnnouncementListItem(
//                    1L,
//                    "Alex",
//                    "Dog",
//                    "Labrador",
//                    "11.12.2023-19.12.2023",
//                    "https://storage.googleapis.com/quick-adoption.appspot.com/images/FZUQgiYfWlY8gmZwlNIl3SPmRoK2",
//                    "Poland",
//                    "Warsaw"
//                )
//            )


            tabbedAnnouncementsViewModel.publicAnnouncementsList.value.forEach { announcement ->
                PublicAnnouncement(announcement) { announcementId ->
                    tabbedAnnouncementsViewModel.navigateToPublicOffer(announcementId)
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
                    model = decodePathFile(ownAnnouncementListItem.animalImage),
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

@Composable
private fun SearchField(
    placeholder: String,
    icon: ImageVector,
    currentValue: String,
    onTextChange: (text: String) -> Unit
) {
    var value by remember { mutableStateOf(currentValue) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 8.dp),
        value = value,
        placeholder = {
            Text(
                placeholder,
                style = MaterialTheme.typography.subtitle1.copy(pl.lbiio.quickadoption.ui.theme.SalmonWhite)
            )
        },
        leadingIcon = { Icon(icon, null) },

        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = pl.lbiio.quickadoption.ui.theme.SimpleWhite,
            backgroundColor = pl.lbiio.quickadoption.ui.theme.PurpleBrownLight,
            cursorColor = pl.lbiio.quickadoption.ui.theme.SimpleWhite,
            focusedBorderColor = pl.lbiio.quickadoption.ui.theme.PurpleBrownLight,
            unfocusedBorderColor = pl.lbiio.quickadoption.ui.theme.PurpleBrownLight,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            focusedLabelColor = Color.Transparent,
            placeholderColor = pl.lbiio.quickadoption.ui.theme.SimpleWhite,
            unfocusedLabelColor = pl.lbiio.quickadoption.ui.theme.SimpleWhite
        ),
        onValueChange = {
            value = it
            onTextChange(it)
        },
    )
}

@Composable
private fun DateInput(
    currentValue: String,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                if (interaction is PressInteraction.Release) {
                    onClick()
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    val text by remember(currentValue) { mutableStateOf(currentValue) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 8.dp),
        value = text,
        placeholder = {
            Text(
                "Date Range",
                style = MaterialTheme.typography.subtitle1.copy(pl.lbiio.quickadoption.ui.theme.SalmonWhite)
            )
        },
        leadingIcon = { Icon(Icons.Default.DateRange, null) },

        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = pl.lbiio.quickadoption.ui.theme.SimpleWhite,
            backgroundColor = pl.lbiio.quickadoption.ui.theme.PurpleBrownLight,
            cursorColor = pl.lbiio.quickadoption.ui.theme.SimpleWhite,
            focusedBorderColor = pl.lbiio.quickadoption.ui.theme.PurpleBrownLight,
            unfocusedBorderColor = pl.lbiio.quickadoption.ui.theme.PurpleBrownLight,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            focusedLabelColor = Color.Transparent,
            placeholderColor = pl.lbiio.quickadoption.ui.theme.SimpleWhite,
            unfocusedLabelColor = pl.lbiio.quickadoption.ui.theme.SimpleWhite
        ),
        onValueChange = {

        },
        readOnly = true,
        interactionSource = interactionSource
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSample(tabbedAnnouncementsViewModel: TabbedAnnouncementsViewModel) {
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.ROOT)

    //val calendar = Calendar.getInstance()
    //calendar.set(2023, 9, 25) // year, month, date

    var startDate by remember {
        mutableLongStateOf(System.currentTimeMillis() + 604800000) // or use mutableStateOf(calendar.timeInMillis)
    }

    //calendar.set(2024, 12, 31) // year, month, date

    var endDate by remember {
        mutableLongStateOf(System.currentTimeMillis() + 604800000 * 2) // or use mutableStateOf(calendar.timeInMillis)
    }

    // set the initial dates
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startDate,
        initialSelectedEndDateMillis = endDate
    )

    var showDateRangePicker by remember {
        mutableStateOf(false)
    }

    if (showDateRangePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDateRangePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDateRangePicker = false
                        startDate = dateRangePickerState.selectedStartDateMillis!!
                        endDate = dateRangePickerState.selectedEndDateMillis!!
                        tabbedAnnouncementsViewModel.dateRange.value =
                            "${formatInputDateValue(startDate)}-${formatInputDateValue(endDate)}"
                        //range = applyAnnouncementViewModel.date.value
                        Log.d("zakres", tabbedAnnouncementsViewModel.dateRange.value)
                    },
                ) {
                    androidx.compose.material.Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDateRangePicker = false
                }) {
                    androidx.compose.material.Text(text = "Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.height(height = 500.dp),
                dateFormatter = RangeDateFormatter(),
                title = { androidx.compose.material.Text(text = "") },
                showModeToggle = false,
                colors =
                DatePickerColors(
                    containerColor = pl.lbiio.quickadoption.ui.theme.Salmon,
                    titleContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    headlineContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    weekdayContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    subheadContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    navigationContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    yearContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    disabledYearContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    currentYearContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    selectedYearContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    disabledSelectedYearContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    selectedYearContainerColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    disabledSelectedYearContainerColor = pl.lbiio.quickadoption.ui.theme.SalmonWhite,
                    dayContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    disabledDayContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    selectedDayContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    disabledSelectedDayContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    selectedDayContainerColor = pl.lbiio.quickadoption.ui.theme.Salmon,
                    disabledSelectedDayContainerColor = pl.lbiio.quickadoption.ui.theme.SalmonWhite,
                    todayContentColor = pl.lbiio.quickadoption.ui.theme.Salmon,
                    todayDateBorderColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    dayInSelectionRangeContainerColor = pl.lbiio.quickadoption.ui.theme.Salmon,
                    dayInSelectionRangeContentColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    dividerColor = pl.lbiio.quickadoption.ui.theme.PurpleBrown,
                    dateTextFieldColors = androidx.compose.material3.TextFieldDefaults.textFieldColors() // You can customize TextFieldColors if needed
                )

            )
        }
    }
    DateInput(tabbedAnnouncementsViewModel.dateRange.value) {
        showDateRangePicker = true

    }
}

private fun formatInputDateValue(dateMillis: Long): String {
    val date = LocalDate.ofEpochDay(dateMillis / 86400000) // Convert millis to LocalDate

    // Format the date as "yy MM dd"
    return String.format(
        "%02d.%02d.%02d",
        date.dayOfMonth,
        date.monthValue,
        date.year
    )
}

private fun codePathFile(path: String): String {
    return path.replace("/", "*")
}

private fun decodePathFile(codedPath: String): String {
    return codedPath.replace("*", "/")
}