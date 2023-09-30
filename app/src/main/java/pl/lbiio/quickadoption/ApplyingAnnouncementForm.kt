package pl.lbiio.quickadoption

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.lbiio.quickadoption.models.ApplyAnnouncementViewModel
import pl.lbiio.quickadoption.support.RangeDateFormatter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ApplyingAnnouncementForm(applyAnnouncementViewModel: ApplyAnnouncementViewModel) {
    Scaffold(
        topBar = {
            SetApplyingAnnouncementFormTopBar(applyAnnouncementViewModel)
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            ApplyingAnnouncementFormContent(applyAnnouncementViewModel)
        },
    )
}

private var imgBitmap: Bitmap? = null
@Composable
private fun ApplyingAnnouncementFormContent(applyAnnouncementViewModel: ApplyAnnouncementViewModel)
{
    Column(modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        FormInput(
            maxChar = 30,
            label = "Name",
            leadingIcon = Icons.Default.Edit,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = applyAnnouncementViewModel.animal_name.value,
            onTextChange = {
                applyAnnouncementViewModel.animal_name.value = it
            }
        )
        FormInput(
            maxChar = 30,
            label = "Species",
            leadingIcon = Icons.Default.Edit,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = applyAnnouncementViewModel.species.value,
            onTextChange = {
                applyAnnouncementViewModel.species.value = it
            }
        )
        FormInput(
            maxChar = 30,
            label = "Breed",
            leadingIcon = Icons.Default.Edit,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = applyAnnouncementViewModel.breed.value,
            onTextChange = {
                applyAnnouncementViewModel.breed.value = it
            }
        )

        DateRangePickerSample(applyAnnouncementViewModel)

        FormInput(
            maxChar = 30,
            label = "Food",
            leadingIcon = Icons.Default.Edit,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = applyAnnouncementViewModel.food.value,
            onTextChange = {
                applyAnnouncementViewModel.breed.value = it
            }
        )


        var selectedImage by remember { mutableStateOf(listOf<Uri>()) }
        //var artwork by remember { mutableStateOf("") }
        val galleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                Log.d("uri", it.toString())
                selectedImage = listOf(it) as List<Uri>
                // artwork = getFilePath(QuickAdoptionApp.getAppContext(), it!!)!!
                applyAnnouncementViewModel.animal_image.value = getFilePath(QuickAdoptionApp.getAppContext(), it!!)!!

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

        val painter: Painter = if (selectedImage.isEmpty() && applyAnnouncementViewModel.animal_image.value.isEmpty()) {
            painterResource(id = R.drawable.ic_image)
        } else {
            val bitmap: Bitmap = BitmapFactory.decodeFile(
                File(applyAnnouncementViewModel.animal_image.value).absolutePath,
                BitmapFactory.Options()
            )
            val imageBitmap: ImageBitmap = bitmap.asImageBitmap()
            BitmapPainter(imageBitmap)
        }
            Image(
                painter = painter,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Button(
                onClick = {
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
                    Log.d("bmp", imgBitmap.toString())
                },
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "PICK ARTWORK")
            }

    }
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
private fun SetApplyingAnnouncementFormTopBar(applyAnnouncementViewModel: ApplyAnnouncementViewModel){
    TopAppBar(
        title = {
            TopAppBarText(text = "Quick Adoption App")
        },
        navigationIcon = {
            IconButton(onClick = {applyAnnouncementViewModel.navigateUp()}) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        elevation = 0.dp
    )
}

@Composable
private fun FormInput(
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
    var text by remember { mutableStateOf(currentValue) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp, 16.dp, 8.dp, 8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                if (it.length <= maxChar){
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

    var text by remember(currentValue) { mutableStateOf(currentValue) }

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 16.dp, 8.dp, 8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = text,
                onValueChange = {

                },
                label = { Text("Date Range") },
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




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSample(applyAnnouncementViewModel: ApplyAnnouncementViewModel) {
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.ROOT)

    //val calendar = Calendar.getInstance()
    //calendar.set(2023, 9, 25) // year, month, date

    var startDate by remember {
        mutableLongStateOf(System.currentTimeMillis()+604800000) // or use mutableStateOf(calendar.timeInMillis)
    }

    //calendar.set(2024, 12, 31) // year, month, date

    var endDate by remember {
        mutableLongStateOf(System.currentTimeMillis()+604800000*2) // or use mutableStateOf(calendar.timeInMillis)
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
                    applyAnnouncementViewModel.date.value = formatInputDateValue("${formatter.format(Date(startDate))}-${formatter.format(Date(endDate))}")
                    //range = applyAnnouncementViewModel.date.value
                    Log.d("zakres", applyAnnouncementViewModel.date.value)
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDateRangePicker = false
                }) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.height(height = 500.dp),
                dateFormatter = RangeDateFormatter(),
                title= { Text(text = "")},
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
                    dateTextFieldColors = TextFieldDefaults.textFieldColors() // You can customize TextFieldColors if needed
                )

            )
        }
    }
    DateInput(applyAnnouncementViewModel.date.value) {
        showDateRangePicker = true

    }
}

private fun formatInputDateValue(input: String): String {
    return input.replace("M", "").replace(" ", ".")
}


