package pl.lbiio.quickadoption.navigation


import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import pl.lbiio.quickadoption.LoginScreen
import pl.lbiio.quickadoption.RegistrationFormForm
import pl.lbiio.quickadoption.models.LoginViewModel
import pl.lbiio.quickadoption.models.RegistrationViewModel

@Composable
fun SigningFormNavigate() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val registrationViewModel: RegistrationViewModel = hiltViewModel()
    val appNavigator = AppNavigatorImpl()
    loginViewModel.initAppNavigator(appNavigator)
    registrationViewModel.initAppNavigator(appNavigator)

    NavigationEffects(
        navigationChannel = appNavigator.navigationChannel,
        navHostController = navController
    )
    NavHost(
        navController = navController,
        startDestination = Destination.LoginScreen
    ) {
        composable(destination = Destination.LoginScreen) {
            //loginViewModel.clearViewModel()
            LoginScreen(loginViewModel)
        }
        composable(destination = Destination.RegistrationScreen) {
            //registrationViewModel.clearViewModel()
            RegistrationFormForm(registrationViewModel)
        }
    }
}

