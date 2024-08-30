package com.kunal.walkwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.kunal.walkwise.ui.theme.WalkWiseTheme
import com.kunal.walkwise.ui.theme.screens.WalkWise

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalkWiseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WalkWise(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
