package pl.lbiio.quickadoption.navigation


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.lbiio.quickadoption.LoginForm
import pl.lbiio.quickadoption.RegistrationFormForm
import pl.lbiio.quickadoption.models.LoginViewModel
import pl.lbiio.quickadoption.models.RegistrationViewModel

@Composable
fun SigningFormNavigate() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val registrationViewModel: RegistrationViewModel = viewModel()
    loginViewModel.initNavController(navController)
    registrationViewModel.initNavController(navController)
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginForm(loginViewModel)
        }
        composable("register") {
            RegistrationFormForm(registrationViewModel)
        }
    }
}