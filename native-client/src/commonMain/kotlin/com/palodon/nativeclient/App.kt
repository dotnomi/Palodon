package com.palodon.nativeclient

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.palodon.shared.Greeting

@Composable
fun App() {
    MaterialTheme {
        Text(Greeting().greeting())
    }
}