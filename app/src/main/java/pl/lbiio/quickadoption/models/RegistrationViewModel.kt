package pl.lbiio.quickadoption.models

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.lbiio.quickadoption.MainActivity
import pl.lbiio.quickadoption.QuickAdoptionApp
import javax.inject.Inject


@HiltViewModel
class RegistrationViewModel @Inject constructor() :
    ViewModel(){
    private var navController: NavController? = null
    val registrationStep: MutableState<Int> = mutableIntStateOf(1)
    val nameAndSurname: MutableState<String> = mutableStateOf("")
    val phone: MutableState<String> = mutableStateOf("")
    val country: MutableState<String> = mutableStateOf("")
    val city: MutableState<String> = mutableStateOf("")
    val address: MutableState<String> = mutableStateOf("")
    val postal: MutableState<String> = mutableStateOf("")
    val email: MutableState<String> = mutableStateOf("")
    val password: MutableState<String> = mutableStateOf("")
    val retypedPassword: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val path: MutableState<String> = mutableStateOf("")

    fun moveToNextStep(){
        when(registrationStep.value){
            1->{
                registrationStep.value = 2
//                if(nameAndSurname.value.isNotEmpty() &&
//                    phone.value.isNotEmpty() &&
//                    country.value.isNotEmpty() &&
//                    city.value.isNotEmpty() &&
//                    address.value.isNotEmpty() &&
//                    postal.value.isNotEmpty())
//                {
//                    if(isPhoneValid(phone.value)){
//                        if(isPostalCodeValid(postal.value)){
//                            registrationStep.value = 2
//                        }
//                        else{
//                            Toast.makeText(QuickAdoptionApp.getAppContext(), "postal code should look like xx-xxx", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    else{
//                        Toast.makeText(QuickAdoptionApp.getAppContext(), "phone number must contain country code", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                else{
//                    Toast.makeText(QuickAdoptionApp.getAppContext(), "Fill all inputs", Toast.LENGTH_SHORT).show()
//                }
            }
            2 -> {
                registrationStep.value = 3
//                if(email.value.isNotEmpty() && password.value.isNotEmpty() && retypedPassword.value.isNotEmpty()){
//                    if(password.value == retypedPassword.value){
//                        if(password.value.length>=6){
//                            registrationStep.value = 3
//                        }
//                        else{
//                            Toast.makeText(QuickAdoptionApp.getAppContext(), "password mus be minimum 6 characters", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    else{
//                        Toast.makeText(QuickAdoptionApp.getAppContext(), "passwords not matching", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                else{
//                    Toast.makeText(QuickAdoptionApp.getAppContext(), "Fill all inputs", Toast.LENGTH_SHORT).show()
//                }

            }
        }
    }

    fun moveToPreviousStep(){
        when(registrationStep.value) {
            2 -> {
                registrationStep.value = 1
            }
            3 -> {
                registrationStep.value = 2
            }
        }
    }

    fun initNavController(navController: NavController) {
        this.navController = navController
    }

    fun finishRegistration() {
        val mainActivityIntent = Intent(QuickAdoptionApp.getAppContext(), MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        QuickAdoptionApp.getAppContext().startActivity(mainActivityIntent)
    }

    private fun isPostalCodeValid(text: String): Boolean {
        val postalCodePattern = Regex("\\d{2}-\\d{3}")
        return postalCodePattern.matches(text)
    }

    private fun isPhoneValid(phoneNumber: String): Boolean {
        val countryCodePattern = Regex("^\\+\\d{1,3}-?\\d{9}$")
        return countryCodePattern.matches(phoneNumber)
    }




}