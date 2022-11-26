package com.euzhene.comranet.autorization.presentation.screen

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.euzhene.comranet.R

@Composable
fun SplashScreen() {
    Box(
        Modifier
            .background(Color.White)
            .fillMaxSize(), contentAlignment = Alignment.Center) {

        Image(
            painter = painterResource(id = R.drawable.euzhene_logo),
            contentDescription = "splash screen",
            modifier = Modifier.size(250.dp)
        )
    }
}