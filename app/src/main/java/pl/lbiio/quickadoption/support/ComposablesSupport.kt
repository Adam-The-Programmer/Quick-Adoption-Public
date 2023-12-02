package pl.lbiio.quickadoption.support

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.lbiio.quickadoption.QuickAdoptionApp
import pl.lbiio.quickadoption.models.ApplyAnnouncementViewModel
import pl.lbiio.quickadoption.models.TabbedAnnouncementsViewModel
import pl.lbiio.quickadoption.ui.theme.PurpleBrown
import pl.lbiio.quickadoption.ui.theme.Salmon
import pl.lbiio.quickadoption.ui.theme.SalmonWhite
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text



@Composable
fun TopAppBarText(
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
fun FormInput(
    maxChar: Int,
    label: String,
    leadingIcon: ImageVector,
    transformation: VisualTransformation,
    keyboardType: KeyboardType,
    maxLines: Int,
    currentValue: String,
    onTextChange: (content: String) -> Unit
) {
    val keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = keyboardType // Set the keyboard type to Phone
    )
    var text by remember(currentValue) { mutableStateOf(currentValue) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 16.dp, 8.dp, 8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                if (it.length <= maxChar) {
                    text = it
                    onTextChange(it)
                }
            },

            label = { Text(label) },
            leadingIcon = {
                Icon(leadingIcon, null)
            },
            trailingIcon = {
                Icon(
                    Icons.Default.Clear, null,
                    modifier = Modifier.clickable { text = "" })
            },
            maxLines = maxLines,
            visualTransformation = transformation,
            keyboardOptions = keyboardOptions
        )

        Text(
            text = "${text.length} / $maxChar",
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        )
    }
}



@Composable
fun DateInput(
    isDark: Boolean,
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

    var text by remember(currentValue) { mutableStateOf(currentValue) }

        if (isDark) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(0.dp, 0.dp, 0.dp, 8.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp, 0.dp, 8.dp),
                    value = text,
                    placeholder = {
                        Text(
                            "Date Range",
                            style = MaterialTheme.typography.subtitle1.copy(SalmonWhite)
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.DateRange, null) },
                    colors = androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors(
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
        }
        else{
            Box(
                modifier = Modifier.fillMaxWidth().padding(8.dp, 16.dp, 8.dp, 8.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = text,
                    onValueChange = {

                    },
                    label = {
                        Text("Date Range")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, null)
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Clear, null,
                            modifier = Modifier.clickable { text = "" })
                    },
                    readOnly = true,
                    interactionSource = interactionSource
                )
            }
        }
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
                            "${QuickAdoptionApp.formatInputDateValue(startDate)}-${
                                QuickAdoptionApp.formatInputDateValue(
                                    endDate
                                )
                            }"
                        //range = applyAnnouncementViewModel.date.value
                        Log.d("zakres", tabbedAnnouncementsViewModel.dateRange.value)
                    },
                    //colors = ButtonColors(Color.Transparent, PurpleBrown, Color.Transparent, Color.Gray)
                ) {
                    Text(text = "Confirm", color = PurpleBrown)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDateRangePicker = false
                },
                    //colors = ButtonColors(Color.Transparent, PurpleBrown, Color.Transparent, Color.Gray)
                ) {
                    Text(text = "Cancel", color = PurpleBrown)
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.height(height = 500.dp),
                dateFormatter = RangeDateFormatter(),
                title = { Text(text = "") },
                showModeToggle = false,
                colors =
                DatePickerColors(
                    containerColor = Salmon,
                    titleContentColor = PurpleBrown,
                    headlineContentColor = PurpleBrown,
                    weekdayContentColor = PurpleBrown,
                    subheadContentColor = PurpleBrown,
                    navigationContentColor = PurpleBrown,
                    yearContentColor = PurpleBrown,
                    disabledYearContentColor = PurpleBrown,
                    currentYearContentColor = PurpleBrown,
                    selectedYearContentColor = PurpleBrown,
                    disabledSelectedYearContentColor = PurpleBrown,
                    selectedYearContainerColor = PurpleBrown,
                    disabledSelectedYearContainerColor = SalmonWhite,
                    dayContentColor = PurpleBrown,
                    disabledDayContentColor = PurpleBrown,
                    selectedDayContentColor = PurpleBrown,
                    disabledSelectedDayContentColor = PurpleBrown,
                    selectedDayContainerColor = Salmon,
                    disabledSelectedDayContainerColor = SalmonWhite,
                    todayContentColor = Salmon,
                    todayDateBorderColor = PurpleBrown,
                    dayInSelectionRangeContainerColor = Salmon,
                    dayInSelectionRangeContentColor = PurpleBrown,
                    dividerColor = PurpleBrown,
                    dateTextFieldColors = androidx.compose.material3.TextFieldDefaults.textFieldColors() // You can customize TextFieldColors if needed
                )

            )
        }
    }
    DateInput(true, tabbedAnnouncementsViewModel.dateRange.value) {
        showDateRangePicker = true

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSample(applyAnnouncementViewModel: ApplyAnnouncementViewModel) {
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
                TextButton(onClick = {
                    showDateRangePicker = false
                    startDate = dateRangePickerState.selectedStartDateMillis!!
                    endDate = dateRangePickerState.selectedEndDateMillis!!
                    applyAnnouncementViewModel.dateRange.value =
                        "${QuickAdoptionApp.formatInputDateValue(startDate)}-${
                            QuickAdoptionApp.formatInputDateValue(endDate)
                        }"
                    //range = applyAnnouncementViewModel.date.value
                    Log.d("zakres", applyAnnouncementViewModel.dateRange.value)
                },
                ) {
                    Text(text = "Confirm", color = PurpleBrown)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDateRangePicker = false
                },
                ) {
                    Text(text = "Cancel", color = PurpleBrown)
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.height(height = 500.dp),
                dateFormatter = RangeDateFormatter(),
                title = { Text(text = "") },
                showModeToggle = false,
                colors =
                DatePickerColors(
                    containerColor = Salmon,
                    titleContentColor = PurpleBrown,
                    headlineContentColor = PurpleBrown,
                    weekdayContentColor = PurpleBrown,
                    subheadContentColor = PurpleBrown,
                    navigationContentColor = PurpleBrown,
                    yearContentColor = PurpleBrown,
                    disabledYearContentColor = PurpleBrown,
                    currentYearContentColor = PurpleBrown,
                    selectedYearContentColor = PurpleBrown,
                    disabledSelectedYearContentColor = PurpleBrown,
                    selectedYearContainerColor = PurpleBrown,
                    disabledSelectedYearContainerColor = SalmonWhite,
                    dayContentColor = PurpleBrown,
                    disabledDayContentColor = PurpleBrown,
                    selectedDayContentColor = PurpleBrown,
                    disabledSelectedDayContentColor = PurpleBrown,
                    selectedDayContainerColor = Salmon,
                    disabledSelectedDayContainerColor = SalmonWhite,
                    todayContentColor = Salmon,
                    todayDateBorderColor = PurpleBrown,
                    dayInSelectionRangeContainerColor = Salmon,
                    dayInSelectionRangeContentColor = PurpleBrown,
                    dividerColor = PurpleBrown,
                    dateTextFieldColors = TextFieldDefaults.textFieldColors() // You can customize TextFieldColors if needed
                )

            )
        }
    }
    DateInput(false, applyAnnouncementViewModel.dateRange.value) {
        showDateRangePicker = true

    }
}

@Composable
fun RatingBar(
    maxRating: Int = 5,
    currentRating: Int,
    onRatingChanged: (Int) -> Unit,
    starsColor: Color = Color.Yellow
) {
    Row(modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp)) {
        for (i in 1..maxRating) {
            androidx.compose.material3.Icon(
                imageVector = if (i <= currentRating) Icons.Filled.Star
                else Icons.Filled.StarOutline,
                contentDescription = null,
                tint = if (i <= currentRating) starsColor
                else Color.Unspecified,
                modifier = Modifier
                    .clickable { onRatingChanged(i) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun SigningFormInput(
    maxChar: Int,
    label: String,
    leadingIcon: ImageVector,
    transformation: VisualTransformation,
    onTextChange: (content: String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 16.dp, 8.dp, 8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                if (it.length <= maxChar) {
                    text = it
                    onTextChange(text)
                }
            },

            label = { Text(label) },
            leadingIcon = {
                Icon(leadingIcon, null)
            },
            trailingIcon = {
                Icon(
                    Icons.Default.Clear, null,
                    modifier = Modifier.clickable { text = "" })
            },
            singleLine = true,
            visualTransformation = transformation,
        )

        Text(
            text = "${text.length} / $maxChar",
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        )
    }
}

@Composable
fun SearchField(
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
            androidx.compose.material3.Text(
                placeholder,
                style = MaterialTheme.typography.subtitle1.copy(SalmonWhite)
            )
        },
        leadingIcon = { Icon(icon, null) },

        colors = androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors(
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
fun Stars(
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