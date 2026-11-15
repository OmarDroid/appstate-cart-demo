package com.example.appstatecart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appstatecart.ui.ShopApp
import com.example.appstatecart.ui.ShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as CartApp).container
        setContent {
            ShopTheme {
                ShopApp(container)
            }
        }
    }
}
