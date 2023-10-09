package pl.lbiio.quickadoption

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalPostOffice
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import pl.lbiio.quickadoption.models.RegistrationViewModel
import java.io.File


@Composable
fun RegistrationFormForm(
    registrationViewModel: RegistrationViewModel
){
    Scaffold(
        topBar = {
            SetRegistrationFormTopBar()
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            RegistrationContent(registrationViewModel)

        }
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
private fun SetRegistrationFormTopBar() {
    TopAppBar(
        title = {
            TopAppBarText(text = "Registration")
        },
        elevation = 4.dp
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun RegistrationContent(
    registrationViewModel: RegistrationViewModel
) {
    var animOut by remember { mutableIntStateOf(-1) }
    var animIn by remember { mutableIntStateOf(1) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column{
            Text(
                text = "Registration",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.subtitle1,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Step ${registrationViewModel.registrationStep.value} of 3",
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.subtitle1,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                progress = (registrationViewModel.registrationStep.value.toFloat()/3f),
                color = pl.lbiio.quickadoption.ui.theme.Salmon
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedContent(
                targetState = registrationViewModel.registrationStep.value,
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(400),
                        initialOffsetX = { fullWidth -> animIn * fullWidth }
                    ) togetherWith
                            slideOutHorizontally(
                                animationSpec = tween(400),
                                targetOffsetX = { fullWidth -> animOut * fullWidth }
                            )
                }
            ) { targetState ->
                when(targetState){
                    1-> FirstStep(registrationViewModel)
                    2-> SecondStep(registrationViewModel)
                    3-> ThirdStep(registrationViewModel)
                }

            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                enabled = registrationViewModel.registrationStep.value>1,
                onClick = {
                    animIn = -1
                    animOut = 1
                    registrationViewModel.moveToPreviousStep()
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(8.dp, 0.dp, 4.dp, 0.dp)
            ) {
                Text(text = "Previous")
            }
            Button(
                onClick = {
                    if(registrationViewModel.registrationStep.value==3){
                        registrationViewModel.finishRegistration()
                    }else{
                        animIn = 1
                        animOut = -1
                        registrationViewModel.moveToNextStep()
                    }

                },
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(8.dp, 0.dp, 4.dp, 0.dp)
            ) {
                Text(text = if(registrationViewModel.registrationStep.value==3)"Finish" else "Next")
            }
        }

    }
}

@Composable
private fun FirstStep(registrationViewModel: RegistrationViewModel){
    Column(modifier = Modifier.fillMaxHeight()){
        FormInput(
            maxChar = 50,
            label = "Name And Surname",
            leadingIcon = Icons.Default.Person,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = registrationViewModel.nameAndSurname.value,
            onTextChange = {
                registrationViewModel.nameAndSurname.value = it
            }
        )
        FormInput(
            maxChar = 14,
            label = "Phone",
            leadingIcon = Icons.Default.Phone,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Phone,
            maxLines = 1,
            currentValue = registrationViewModel.phone.value,
            onTextChange = {
                registrationViewModel.phone.value = it
            }
        )
        FormInput(
            maxChar = 40,
            label = "Country",
            leadingIcon = Icons.Default.MyLocation,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = registrationViewModel.country.value,
            onTextChange = {
                registrationViewModel.country.value = it
            }
        )
        FormInput(
            maxChar = 40,
            label = "City",
            leadingIcon = Icons.Default.LocationCity,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = registrationViewModel.city.value,
            onTextChange = {
                registrationViewModel.city.value = it
            }
        )
        FormInput(
            maxChar = 30,
            label = "Address",
            leadingIcon = Icons.Default.Home,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = registrationViewModel.address.value,
            onTextChange = {
                registrationViewModel.address.value = it
            }
        )
        FormInput(
            maxChar = 6,
            label = "Postal",
            leadingIcon = Icons.Default.LocalPostOffice,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Number,
            maxLines = 1,
            currentValue = registrationViewModel.postal.value,
            onTextChange = {
                registrationViewModel.postal.value = it
            }
        )
    }

}

@Composable
private fun SecondStep(registrationViewModel: RegistrationViewModel){
    Column(modifier = Modifier.fillMaxHeight()){
        FormInput(
            maxChar = 30,
            label = "Email",
            leadingIcon = Icons.Default.Email,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = registrationViewModel.email.value,
            onTextChange = {
                registrationViewModel.email.value = it
            }
        )
        FormInput(
            maxChar = 30,
            label = "Password",
            leadingIcon = Icons.Default.Password,
            transformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            maxLines = 1,
            currentValue = registrationViewModel.password.value,
            onTextChange = {
                registrationViewModel.password.value = it
            }
        )
        FormInput(
            maxChar = 30,
            label = "Password Retyped",
            leadingIcon = Icons.Default.Password,
            transformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            maxLines = 1,
            currentValue = registrationViewModel.retypedPassword.value,
            onTextChange = {
                registrationViewModel.retypedPassword.value = it
            }
        )
    }

}
private var imgBitmap: Bitmap? = null

@Composable
private fun ThirdStep(registrationViewModel: RegistrationViewModel){
    var selectedImage by remember { mutableStateOf(listOf<Uri>()) }
    //var artwork by remember { mutableStateOf("") }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            Log.d("uri", it.toString())
            selectedImage = listOf(it) as List<Uri>
           // artwork = getFilePath(QuickAdoptionApp.getAppContext(), it!!)!!
            registrationViewModel.path.value = getFilePath(QuickAdoptionApp.getAppContext(), it!!)!!

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

    val painter: Painter = if (selectedImage.isEmpty() && registrationViewModel.path.value.isEmpty()) {
        painterResource(id = R.drawable.ic_image)
    } else {
        val bitmap: Bitmap = BitmapFactory.decodeFile(
            File(registrationViewModel.path.value).absolutePath,
            BitmapFactory.Options()
        )
        val imageBitmap: ImageBitmap = bitmap.asImageBitmap()
        BitmapPainter(imageBitmap)
    }
    Column(modifier = Modifier.fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
    ){
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

        FormInput(
            maxChar = 200,
            label = "Your Description",
            leadingIcon = Icons.Default.Description,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 5,
            currentValue = registrationViewModel.description.value,
            onTextChange = {
                registrationViewModel.description.value = it
            }
        )

    }
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


private fun getFilePath(context: Context, uri: Uri): String? {
    val isMediaDocument = uri.authority == "com.android.providers.media.documents"
    if (isMediaDocument) {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":").toTypedArray()
        val type = split[0]
        var contentUri: Uri? = null
        if ("image" == type) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else if ("video" == type) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else if ("audio" == type) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])
        return getDataColumn(context, contentUri, selection, selectionArgs)
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

private fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}
