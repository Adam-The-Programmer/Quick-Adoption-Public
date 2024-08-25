package pl.lbiio.quickadoption

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import pl.lbiio.quickadoption.models.ApplyAnnouncementViewModel
import pl.lbiio.quickadoption.models.ChatConsoleViewModel
import pl.lbiio.quickadoption.models.OwnChatsListViewModel
import pl.lbiio.quickadoption.models.PublicAnnouncementDetailsViewModel
import pl.lbiio.quickadoption.models.PublicChatsListViewModel
import pl.lbiio.quickadoption.models.TabbedAnnouncementsViewModel
import pl.lbiio.quickadoption.navigation.AppNavigatorImpl
import pl.lbiio.quickadoption.navigation.Destination
import pl.lbiio.quickadoption.navigation.MainActivityNavigate
import pl.lbiio.quickadoption.navigation.NavHost
import pl.lbiio.quickadoption.navigation.NavigationIntent
import pl.lbiio.quickadoption.navigation.composable
import pl.lbiio.quickadoption.ui.theme.QuickAdoptionTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

