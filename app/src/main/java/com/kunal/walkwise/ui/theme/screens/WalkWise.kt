package com.kunal.walkwise.ui.theme.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kunal.walkwise.MainViewModel
import com.kunal.walkwise.R
import com.kunal.walkwise.services.StepTrackingService
import com.kunal.walkwise.ui.theme.DarkGrey
import com.kunal.walkwise.ui.theme.DarkRed
import com.kunal.walkwise.ui.theme.WalkWiseTheme

@Composable
fun WalkWise(
    modifier: Modifier
) {
    var isServiceRunning: Boolean by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val mainViewModel = viewModel<MainViewModel>()

    val steps by mainViewModel.userStepsData.collectAsState()

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map[Manifest.permission.POST_NOTIFICATIONS] == true && map[Manifest.permission.ACTIVITY_RECOGNITION] == true) {
                handleServiceOnBasisOfAction(context, "STOP")
                handleServiceOnBasisOfAction(context, "START")
            } else {
                Toast.makeText(context, "Please grant Notification permission", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_circle), // Replace with your image
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_man),
                    contentDescription = "Walking man"
                )
                Text(
                    text = steps.toString(),
                    fontSize = 64.sp,
                    color = DarkRed,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )

                Text(
                    text = "DAILY STEPS",
                    fontSize = 12.sp,
                    color = DarkGrey
                )
            }
        }

        Button(
            onClick = {
                if (!isServiceRunning) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.POST_NOTIFICATIONS,
                                Manifest.permission.ACTIVITY_RECOGNITION
                            )
                        )
                    } else {
                        handleServiceOnBasisOfAction(context, "STOP")
                        handleServiceOnBasisOfAction(context, "START")
                    }
                } else {
                    handleServiceOnBasisOfAction(context, "STOP")
                }
                isServiceRunning = !isServiceRunning
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkRed
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = if (isServiceRunning.not()) "Start Tracking" else "Stop Tracking"
            )
        }
    }
}

@Composable
@Preview(
    showSystemUi = true
)
fun WalkWisePreview() {
    WalkWiseTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            WalkWise(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

fun handleServiceOnBasisOfAction(context: Context, action: String) {
    val serviceIntent = Intent(context, StepTrackingService::class.java)
    when (action) {
        "START" -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }

        "STOP" -> {
            context.stopService(serviceIntent)
        }
    }
}

