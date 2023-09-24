package pl.lbiio.quickadoption

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.lbiio.quickadoption.models.LoginViewModel

@Composable
fun LoginForm(
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
        
        FormInput(
            maxChar = 30,
            label = "Email",
            leadingIcon = Icons.Default.Email,
            transformation = VisualTransformation.None,
            onTextChange = {
                loginViewModel.email.value = it
            }
        )

        FormInput(
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
                style = MaterialTheme.typography.subtitle1.copy(pl.lbiio.quickadoption.ui.theme.Salmon),
                fontSize = 16.sp,
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = {
                if(loginViewModel.email.value.isNotEmpty() && loginViewModel.password.value.isNotEmpty())
                    loginViewModel.doLogin()
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

}

@Composable
private fun FormInput(
    maxChar: Int,
    label: String,
    leadingIcon: ImageVector,
    transformation: VisualTransformation,
    onTextChange: (content: String) -> Unit
) {
    var text by remember { mutableStateOf("") }
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