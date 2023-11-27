package com.claudiogalvaodev

import App
import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This also sets up the initial system bar style based on the platform theme


        setContent {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(TRANSPARENT, TRANSPARENT),
                navigationBarStyle = SystemBarStyle.light(TRANSPARENT, TRANSPARENT)
            )

            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}