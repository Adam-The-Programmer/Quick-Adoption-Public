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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPostOffice
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import pl.lbiio.quickadoption.models.RegistrationViewModel
import pl.lbiio.quickadoption.support.FormInput
import pl.lbiio.quickadoption.support.TopAppBarText
import java.io.File


@Composable
fun RegistrationFormForm(
    registrationViewModel: RegistrationViewModel
){
    Scaffold(
        topBar = {
            SetRegistrationFormTopBar(registrationViewModel)
        },
        backgroundColor = White,
        content = {
            it.calculateBottomPadding()
            RegistrationContent(registrationViewModel)

        }
    )
}

@Composable
private fun SetRegistrationFormTopBar(registrationViewModel: RegistrationViewModel) {
    TopAppBar(
        title = {
            TopAppBarText(text = "Registration")
        },
        navigationIcon = {
            IconButton(onClick = { registrationViewModel.navigateUp() }) {
                androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
            }
        },
        elevation = 4.dp
    )
}


@Composable
private fun RegistrationContent(
    registrationViewModel: RegistrationViewModel
) {
    var animOut by remember { mutableIntStateOf(-1) }
    var animIn by remember { mutableIntStateOf(1) }
    BoxWithConstraints(contentAlignment = Center) {
        this.constraints
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
        ///if(!registrationViewModel.isFinished.value) CircularProgressIndicator()

        if (!registrationViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { registrationViewModel.isFinished.value = true },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment= Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(White, shape = RoundedCornerShape(8.dp))
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

}

@Composable
private fun SecondStep(registrationViewModel: RegistrationViewModel){
    Column(modifier = Modifier.fillMaxHeight()){
        FormInput(
            maxChar = 30,
            label = "Name",
            leadingIcon = Icons.Default.Person,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = registrationViewModel.name.value,
            onTextChange = {
                registrationViewModel.name.value = it
            }
        )
        FormInput(
            maxChar = 40,
            label = "Surname",
            leadingIcon = Icons.Default.Person,
            transformation = VisualTransformation.None,
            keyboardType = KeyboardType.Text,
            maxLines = 1,
            currentValue = registrationViewModel.surname.value,
            onTextChange = {
                registrationViewModel.surname.value = it
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
private fun FirstStep(registrationViewModel: RegistrationViewModel){
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
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            Log.d("uri", it.toString())
            selectedImage = listOf(it) as List<Uri>
            registrationViewModel.path.value = QuickAdoptionApp.getFilePath(QuickAdoptionApp.getAppContext(), it!!)!!
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
