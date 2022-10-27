package com.euzhene.comranet.allChats

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.euzhene.comranet.TAG_PRESENT
import com.euzhene.comranet.destinations.AddChatScreenDestination
import com.euzhene.comranet.destinations.ChatRoomScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest

@Destination
@Composable
fun AllChatsScreen(
    navigator: DestinationsNavigator,
    viewModel: AllChatsViewModel,
) {
    Scaffold(topBar = {

    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigator.navigate(AddChatScreenDestination()) },
                backgroundColor = Color(0xff5291ff)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add chat",
                    tint = Color.White
                )
            }
        }) { it ->
        var errorMessage by rememberSaveable{ mutableStateOf("") }
        LaunchedEffect(key1 = Unit) {
            viewModel.errorMessage.collectLatest {
                Log.d(TAG_PRESENT, "AllChatsScreen: ")
                errorMessage = it
            }
        }

        Column(
            Modifier
                .padding(it)
                .padding(8.dp)
        ) {
            Text("Alpha-02", color = Color.Red, modifier = Modifier.align(Alignment.End))
            Text(errorMessage, color = Color.DarkGray, fontSize = 30.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            LazyColumn(Modifier.fillMaxSize()) {
                items(viewModel.chatInfoList) { chatInfo ->
                    Row {
                        Surface(
                            modifier = Modifier.size(50.dp),
                            shape = CircleShape,
                            color = Color.LightGray
                        ) {
                            AsyncImage(
                                model = chatInfo.chatInfo.chatPhoto,
                                contentDescription = "chat photo",
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            chatInfo.chatInfo.chatName,
                            fontSize = 25.sp,
                            modifier = Modifier
                                .clickable {
                                    navigator.navigate(ChatRoomScreenDestination(chatInfo.chatId))
                                }
                                .fillMaxWidth())
                    }

                    Divider(startIndent = 10.dp, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }

    }
}