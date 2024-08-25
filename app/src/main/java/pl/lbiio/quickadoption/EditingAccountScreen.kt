package pl.lbiio.quickadoption

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPostOffice
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import pl.lbiio.quickadoption.data.LeaderBoardItem
import pl.lbiio.quickadoption.models.EditingAccountViewModel
import pl.lbiio.quickadoption.models.LeaderBoardViewModel
import pl.lbiio.quickadoption.support.FormInput
import pl.lbiio.quickadoption.support.TopAppBarText
import java.io.File

@Composable
fun EditingAccountScreen(editingAccountViewModel: EditingAccountViewModel) {
    Scaffold(
        topBar = {
            SetEditingAccountScreenTopBar(editingAccountViewModel)
        },
        backgroundColor = Color.White,
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                EditingAccountScreenContent(editingAccountViewModel)
            }
        },
    )
}

@Composable
fun EditingAccountScreenContent(editingAccountViewModel: EditingAccountViewModel) {

    var selectedImage by remember { mutableStateOf(listOf<Uri>()) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            Log.d("uri", it.toString())
            selectedImage = listOf(it) as List<Uri>
            editingAccountViewModel.path.value = QuickAdoptionApp.getFilePath(QuickAdoptionApp.getAppContext(), it!!)!!
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

    val painter: Painter = if (selectedImage.isEmpty() && editingAccountViewModel.path.value.isEmpty()) {
        painterResource(id = R.drawable.ic_image)
    } else if(selectedImage.isEmpty()) {
        val imageUrl = selectedImage.ifEmpty { editingAccountViewModel.path.value }
        // Optional: Display while loading
        rememberAsyncImagePainter(ImageRequest.Builder // Optional: Display on error
            (LocalContext.current).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
            placeholder(R.drawable.ic_image) // Optional: Display while loading
            error(R.drawable.ic_image) // Optional: Display on error
        }).build()
        )
    }else{
        try{
            val bitmap: Bitmap = BitmapFactory.decodeFile(
                File(editingAccountViewModel.path.value).absolutePath,
                BitmapFactory.Options()
            )
            val imageBitmap: ImageBitmap = bitmap.asImageBitmap()
            BitmapPainter(imageBitmap)
        }catch(ex: NullPointerException){
            painterResource(id = R.drawable.ic_image)
        }

    }

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
                        editingAccountViewModel.inflateInterfaceWithData {
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            LaunchedEffect(Unit) {
                editingAccountViewModel.inflateInterfaceWithData {
                    isInternetNotAvailable.value = true
                }
            }

            Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
                FormInput(
                    maxChar = 30,
                    label = "Name",
                    leadingIcon = Icons.Default.Person,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 1,
                    currentValue = editingAccountViewModel.name.value,
                    onTextChange = {
                        editingAccountViewModel.name.value = it
                    }
                )
                FormInput(
                    maxChar = 40,
                    label = "Surname",
                    leadingIcon = Icons.Default.Person,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 1,
                    currentValue = editingAccountViewModel.surname.value,
                    onTextChange = {
                        editingAccountViewModel.surname.value = it
                    }
                )
                FormInput(
                    maxChar = 14,
                    label = "Phone",
                    leadingIcon = Icons.Default.Phone,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Phone,
                    maxLines = 1,
                    currentValue = editingAccountViewModel.phone.value,
                    onTextChange = {
                        editingAccountViewModel.phone.value = it
                    }
                )
                FormInput(
                    maxChar = 40,
                    label = "Country",
                    leadingIcon = Icons.Default.MyLocation,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 1,
                    currentValue = editingAccountViewModel.country.value,
                    onTextChange = {
                        editingAccountViewModel.country.value = it
                    }
                )
                FormInput(
                    maxChar = 40,
                    label = "City",
                    leadingIcon = Icons.Default.LocationCity,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 1,
                    currentValue = editingAccountViewModel.city.value,
                    onTextChange = {
                        editingAccountViewModel.city.value = it
                    }
                )
                FormInput(
                    maxChar = 30,
                    label = "Address",
                    leadingIcon = Icons.Default.Home,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 1,
                    currentValue = editingAccountViewModel.address.value,
                    onTextChange = {
                        editingAccountViewModel.address.value = it
                    }
                )
                FormInput(
                    maxChar = 6,
                    label = "Postal",
                    leadingIcon = Icons.Default.LocalPostOffice,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Number,
                    maxLines = 1,
                    currentValue = editingAccountViewModel.postal.value,
                    onTextChange = {
                        editingAccountViewModel.postal.value = it
                    }
                )
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
                                launcher.launch(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 8.dp, 0.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "PICK ARTWORK")
                }

                FormInput(
                    maxChar = 200,
                    label = "Your Description",
                    leadingIcon = Icons.Default.Description,
                    transformation = VisualTransformation.None,
                    keyboardType = KeyboardType.Text,
                    maxLines = 5,
                    currentValue = editingAccountViewModel.description.value,
                    onTextChange = {
                        editingAccountViewModel.description.value = it
                    }
                )

                Button(
                    onClick = {
                        editingAccountViewModel.applyAccountData({
                            editingAccountViewModel.navigateUp()
                        },{
                            Toast.makeText(QuickAdoptionApp.getAppContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        })
                    },
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 8.dp, 0.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "APPLY")
                }

            }

        }
        if (!editingAccountViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { editingAccountViewModel.isFinished.value = true },
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
private fun SetEditingAccountScreenTopBar(editingAccountViewModel: EditingAccountViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Leader Board")
        }, navigationIcon = {
            IconButton(onClick = { editingAccountViewModel.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        },
        elevation = 4.dp
    )
}


