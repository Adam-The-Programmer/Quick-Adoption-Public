package pl.lbiio.quickadoption

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import pl.lbiio.quickadoption.navigation.SigningFormNavigate
import pl.lbiio.quickadoption.ui.theme.QuickAdoptionTheme

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private  val PERMISSIONS_REQUEST_CODE = 1
    var PERMISSIONS = mutableListOf<String>()
    private val TAG = "PERMISSION_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PERMISSIONS = mutableListOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PERMISSIONS.add(Manifest.permission.POST_NOTIFICATIONS)
            PERMISSIONS.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        setupPermissions()
        setContent {
            QuickAdoptionTheme {
                BoxWithConstraints(
                    Modifier.fillMaxSize()
                ) {
                    SigningFormNavigate()
                }
            }
        }
    }

    private fun makeRequest() {

        ActivityCompat.requestPermissions(
            this,
            PERMISSIONS.toTypedArray(),
            PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                grantResults.forEach {
                    if (grantResults.isEmpty() || it != PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "Permission has been denied by user")
                    } else {
                        Log.i(TAG, "Permission has been granted by user")
                    }
                }
            }
        }
    }

    private fun setupPermissions() {
        val selfPermissions = mutableListOf(
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ),
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            ),
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.VIBRATE
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            selfPermissions.add(
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ))
            selfPermissions.add(
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ))
        }


        selfPermissions.forEachIndexed { index, permission->
            if (permission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        PERMISSIONS[index]
                    )
                ) {
                    Log.d(PERMISSIONS[index], "denied")
                } else {
                    makeRequest()
                }
            }
            else{
                Log.d(PERMISSIONS[index], "granted")
            }
        }
    }

}
