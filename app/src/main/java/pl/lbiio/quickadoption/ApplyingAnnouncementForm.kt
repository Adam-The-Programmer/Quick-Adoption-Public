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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import pl.lbiio.quickadoption.models.ApplyAnnouncementViewModel
import pl.lbiio.quickadoption.support.DateRangePickerSample
import pl.lbiio.quickadoption.support.FormInput
import pl.lbiio.quickadoption.support.TopAppBarText
import java.io.File

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
private fun ApplyingAnnouncementFormContent(applyAnnouncementViewModel: ApplyAnnouncementViewModel) {
    val isInternetNotAvailable = remember { mutableStateOf(false) }
    val actionBrokenByInternetLoss =
        remember { mutableStateOf(0) } // 0-getAnnouncementById(), 1-applyAnnouncement()

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
                        when (actionBrokenByInternetLoss.value) {
                            0 -> {
                                applyAnnouncementViewModel.getAnnouncementById {
                                    isInternetNotAvailable.value = true
                                }
                            }

                            1 -> {
                                applyAnnouncementViewModel.applyAnnouncement {
                                    isInternetNotAvailable.value = true
                                }
                            }
                        }
                    }) {
                    Text("Reload")
                }
            }
        })
    }

    LaunchedEffect(Unit) {
        if (applyAnnouncementViewModel.announcementId.value != -1L) {
            actionBrokenByInternetLoss.value = 0
            applyAnnouncementViewModel.getAnnouncementById {
                isInternetNotAvailable.value = true
            }
        }
    }

    BoxWithConstraints(contentAlignment = Alignment.Center) {
        this.constraints
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if ((applyAnnouncementViewModel.isFinished.value && !isInternetNotAvailable.value)||actionBrokenByInternetLoss.value==1) {
                FormInput(maxChar = 30,
                    label = "Name",
                    leadingIcon = Icons.Default.Edit,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 1,
                    currentValue = applyAnnouncementViewModel.animalName.value,
                    onTextChange = {
                        applyAnnouncementViewModel.animalName.value = it
                    })
                FormInput(maxChar = 30,
                    label = "Species",
                    leadingIcon = Icons.Default.Edit,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 1,
                    currentValue = applyAnnouncementViewModel.species.value,
                    onTextChange = {
                        applyAnnouncementViewModel.species.value = it
                    })
                FormInput(maxChar = 30,
                    label = "Breed",
                    leadingIcon = Icons.Default.Edit,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 1,
                    currentValue = applyAnnouncementViewModel.breed.value,
                    onTextChange = {
                        applyAnnouncementViewModel.breed.value = it
                    })

                DateRangePickerSample(applyAnnouncementViewModel)

                FormInput(maxChar = 30,
                    label = "Food",
                    leadingIcon = Icons.Default.Edit,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 1,
                    currentValue = applyAnnouncementViewModel.food.value,
                    onTextChange = {
                        applyAnnouncementViewModel.food.value = it
                    })

                FormInput(maxChar = 250,
                    label = "Animal Description",
                    leadingIcon = Icons.Default.Edit,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 8,
                    currentValue = applyAnnouncementViewModel.animalDescription.value,
                    onTextChange = {
                        applyAnnouncementViewModel.animalDescription.value = it
                    })


                var selectedImage by remember { mutableStateOf(listOf<Uri>()) }
                val galleryLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                        Log.d("uri", it.toString())
                        selectedImage = listOf(it) as List<Uri>
                        applyAnnouncementViewModel.animalImage.value = QuickAdoptionApp.getFilePath(
                            QuickAdoptionApp.getAppContext(), it!!
                        )!!

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

                val painter: Painter =
                    if (selectedImage.isEmpty() && applyAnnouncementViewModel.animalImage.value.isEmpty()) {
                        painterResource(id = R.drawable.ic_image)
                    } else if (!applyAnnouncementViewModel.animalImage.value.contains("http")) {
                        val bitmap: Bitmap = BitmapFactory.decodeFile(
                            File(applyAnnouncementViewModel.animalImage.value).absolutePath,
                            BitmapFactory.Options()
                        )
                        val imageBitmap: ImageBitmap = bitmap.asImageBitmap()
                        BitmapPainter(imageBitmap)
                    } else {
                        rememberAsyncImagePainter(applyAnnouncementViewModel.animalImage.value)
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
                    }, modifier = Modifier
                        .padding(8.dp, 0.dp, 8.dp, 0.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "PICK ARTWORK")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            actionBrokenByInternetLoss.value = 1
                            applyAnnouncementViewModel.applyAnnouncement {
                                isInternetNotAvailable.value = true
                            }
                        }, modifier = Modifier
                            .padding(8.dp, 0.dp, 4.dp, 0.dp)
                            .fillMaxWidth(0.5f)
                    ) {
                        Text(text = "APPLY")
                    }
                    Button(
                        onClick = {
                            Log.d("dane", applyAnnouncementViewModel.animalDescription.value)
                            applyAnnouncementViewModel.navigateUp()
                        }, modifier = Modifier
                            .padding(4.dp, 0.dp, 8.dp, 0.dp)
                            .fillMaxWidth(1f)
                    ) {
                        Text(text = "DISMISS")
                    }
                }
            }
        }

        if (!applyAnnouncementViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { applyAnnouncementViewModel.isFinished.value = true },
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
private fun SetApplyingAnnouncementFormTopBar(applyAnnouncementViewModel: ApplyAnnouncementViewModel) {
    TopAppBar(title = {
        TopAppBarText(text = "Quick Adoption App")
    }, navigationIcon = {
        IconButton(onClick = {
            applyAnnouncementViewModel.navigateUp()
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
    }, elevation = 0.dp
    )
}
