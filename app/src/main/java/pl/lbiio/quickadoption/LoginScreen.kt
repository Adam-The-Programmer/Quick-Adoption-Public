package pl.lbiio.quickadoption

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pl.lbiio.quickadoption.models.LoginViewModel
import pl.lbiio.quickadoption.support.SigningFormInput
import pl.lbiio.quickadoption.support.TopAppBarText
import pl.lbiio.quickadoption.ui.theme.Salmon

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel
){
    Scaffold(
        topBar = {
            SetLoginFormTopBar()
        },
        backgroundColor = Color.White,
        content = {
            it.calculateBottomPadding()
            LoginContent(loginViewModel)
        },
    )
}

@Composable
private fun SetLoginFormTopBar() {
    TopAppBar(
        title = {
            TopAppBarText(text = "Login")
        },
        elevation = 4.dp
    )
}

@Composable
private fun LoginContent(
    loginViewModel: LoginViewModel
){
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        this.constraints
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Text(
                text = "Login",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.subtitle1,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))

            SigningFormInput(
                maxChar = 30,
                label = "Email",
                leadingIcon = Icons.Default.Email,
                transformation = VisualTransformation.None,
                onTextChange = {
                    loginViewModel.email.value = it
                }
            )

            SigningFormInput(
                maxChar = 30,
                label = "Password",
                leadingIcon = Icons.Default.Password,
                transformation = PasswordVisualTransformation(),
                onTextChange = {
                    loginViewModel.password.value = it
                }
            )

            Row(
                modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            ){
                Text(
                    text = "Not a member?",
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    modifier = Modifier.clickable {
                        loginViewModel.navigateToRegistrationForm()
                    },
                    text = "Signup now!",
                    style = MaterialTheme.typography.subtitle1.copy(Salmon),
                    fontSize = 16.sp,
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    if(loginViewModel.email.value.isNotEmpty() && loginViewModel.password.value.isNotEmpty())
                        loginViewModel.tryLogIn()
                    else{
                        Log.d("login form", "login data not provided")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
            ) {
                Text(text = "Login")
            }

        }

        if (!loginViewModel.isFinished.value) {
            Dialog(
                onDismissRequest = { loginViewModel.isFinished.value = true },
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