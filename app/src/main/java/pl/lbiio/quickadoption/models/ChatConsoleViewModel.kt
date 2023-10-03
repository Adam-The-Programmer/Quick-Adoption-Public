package pl.lbiio.quickadoption.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatConsoleViewModel @Inject constructor() :
    ViewModel(){
    private var navController: NavController? = null

    val chatId: MutableState<Long> = mutableLongStateOf(-1L)

    fun initNavController(navController: NavController) {
        this.navController = navController
    }

    fun navigateUp(){
        navController?.navigateUp()
    }
    }