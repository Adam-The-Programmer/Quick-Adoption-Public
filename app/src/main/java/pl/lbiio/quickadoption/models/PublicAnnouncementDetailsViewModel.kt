package pl.lbiio.quickadoption.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.lbiio.quickadoption.navigation.AppNavigator
import javax.inject.Inject

@HiltViewModel
class PublicAnnouncementDetailsViewModel @Inject constructor() :
    ViewModel(){
    private var appNavigator: AppNavigator? = null
    //private var navController: NavController? = null

    val announcementID: MutableState<Long> = mutableStateOf(-1L)
    val animalImage: MutableState<String> = mutableStateOf("")
    val dateRange: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val food: MutableState<String> = mutableStateOf("")
    val ownerImage: MutableState<String> = mutableStateOf("")
    val ownerDescription: MutableState<String> = mutableStateOf("")


    fun initAppNavigator(appNavigator: AppNavigator){
        this.appNavigator = appNavigator
    }


    fun initValues(){
        animalImage.value = "https:**upload.wikimedia.org*wikipedia*commons*thumb*3*34*Labrador_on_Quantock_%282175262184%29.jpg*800px-Labrador_on_Quantock_%282175262184%29.jpg"
        dateRange.value = "08.11.2023 - 23.11.2023"
        description.value = "Alex is a calm dog which doesn't like staying alone in home"
        food.value = "Fodder, Meat and Fish"
        ownerImage.value = "https:**bi.im-g.pl*im*11*06*1a*z27288081IER,Alvaro-Soler---2.jpg"
        ownerDescription.value = "I look after my dog since 2018.\nSometimes I need a person who can take over from me"
    }

    fun navigateUp(){
        appNavigator?.tryNavigateBack()
    }

    fun clearViewModel(){
        val announcementID = -1L
        val animalImage = ""
        val dateRange = ""
        val description = ""
        val food = ""
        val ownerImage = ""
        val ownerDescription = ""
    }
}