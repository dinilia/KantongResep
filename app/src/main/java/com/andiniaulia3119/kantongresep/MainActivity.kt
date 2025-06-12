package com.andiniaulia3119.kantongresep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.andiniaulia3119.kantongresep.ui.screen.HomeScreen
import com.andiniaulia3119.kantongresep.ui.theme.KantongResepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KantongResepTheme {
                HomeScreen()
            }
        }
    }
}
