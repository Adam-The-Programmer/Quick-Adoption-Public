package pl.lbiio.quickadoption.models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() :
    ViewModel(){
    private var navController: NavController? = null
    val email: MutableState<String> = mutableStateOf("")
    val password: MutableState<String> = mutableStateOf("")

    fun initNavController(navController: NavController) {
        this.navController = navController
    }

    fun navigateToRegistrationForm() {
        navController?.navigate("register")
    }

    fun doLogin() {
        Log.d("login data", "${email.value}, ${password.value}")
    }


}