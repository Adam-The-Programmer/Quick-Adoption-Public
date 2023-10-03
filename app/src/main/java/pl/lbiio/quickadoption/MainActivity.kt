package pl.lbiio.quickadoption

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import pl.lbiio.quickadoption.navigation.MainActivityNavigate
import pl.lbiio.quickadoption.ui.theme.QuickAdoptionTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedBoxWithConstraintsScope")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickAdoptionTheme {
                BoxWithConstraints(
                    Modifier.fillMaxSize()
                ) {
                    MainActivityNavigate()
                }
            }
        }
    }
}