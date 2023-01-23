package com.euzhene.comranet.chatRoom.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination
@Composable
fun WatchImageScreen(
    navigator: DestinationsNavigator,
    url: String,
) {
    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = { navigator.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back to chat room",
                    tint = Color.White
                )
            }
        }, backgroundColor = Color.Black)
    }) {
        var scale by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(
            Modifier
                .padding(it)
                .clip(RectangleShape)
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        scale *= zoom
                        val maxX = (size.width * (scale - 1)) / 2
                        val minX = -maxX
                        offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
                        val maxY = (size.height * (scale - 1)) / 2
                        val minY = -maxY
                        offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))

                    }
                }
        ) {
            AsyncImage(
                model = url,
                contentDescription = "Clicked image",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = maxOf(.5f, minOf(3f, scale))
                        scaleY = maxOf(.5f, minOf(3f, scale))

                        translationX = offsetX
                        translationY = offsetY
                    }
            )
        }
    }
}

@Destination
@Composable
fun SendImageScreen(
    navigator: DestinationsNavigator,
    viewModel: ChatRoomViewModel,
    chatId: String,
) {

    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = { navigator.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back to chat room",
                    tint = Color.White
                )
            }

        }, backgroundColor = Color.Black)
    }) {
        Box(Modifier.padding(it)) {
            ImageSelector(onSendImage = {
                viewModel.sendImage(it)
                navigator.popBackStack()
            }, onDismiss = {
                navigator.popBackStack()
            })
        }
    }

}

@Composable
fun ImageSelector(
    onSendImage: (Uri) -> Unit,
    onDismiss: () -> Unit,
) {
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            if (it == null) {
                onDismiss()
            }
            imageUri = it
        }
    LaunchedEffect(key1 = launcher) {
        if (imageUri != null) return@LaunchedEffect
        launcher.launch("image/*")
    }

    if (imageUri == null) return

    Surface(
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Attached image",
                modifier = Modifier.fillMaxSize()
            )
            Button(
                onClick = { onSendImage(imageUri!!) },
                shape = CircleShape,
                modifier = Modifier
                    .size(70.dp)
                    .padding(6.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff03ecfc)),
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "send image",
                    modifier = Modifier
                        .size(50.dp),
                    tint = Color.White,
                )
            }
        }
    }

}