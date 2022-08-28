package com.euzhene.comranet.chatRoom.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.euzhene.comranet.TAG_PRESENT
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel
import com.euzhene.comranet.chatRoom.presentation.component.ChatInput
import com.euzhene.comranet.chatRoom.presentation.component.Conversation
import com.euzhene.comranet.chatRoom.presentation.component.InputSelector
import com.euzhene.comranet.destinations.PreferenceScreenDestination
import com.euzhene.comranet.destinations.SendImageScreenDestination
import com.euzhene.comranet.destinations.WatchImageScreenDestination
import com.google.firebase.auth.FirebaseUser
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(false)
@Destination
@Composable
fun ChatRoomScreen(
    navigator: DestinationsNavigator,
    viewModel: ChatRoomViewModel,
   // user: FirebaseUser,
) {
    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = viewModel.config.colorOfAppBar,
            title = { Text("") },
            navigationIcon = {
                IconButton(onClick = {
                    navigator.navigate(PreferenceScreenDestination())
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "settings icon"
                    )
                }

            },
        )
    }) {
        Box(Modifier.padding(it)) {
            ChatRoom(viewModel = viewModel, navigator)
        }
    }
}

@Composable
fun ChatRoom(viewModel: ChatRoomViewModel, navigator: DestinationsNavigator) {
    Box {
        Column {
            Conversation(
                newChatData = viewModel.observedChatData,
                modifier = Modifier.weight(1f),
                chatDataPaging = viewModel.chatDataPaging,
                config = viewModel.config,
                onImageClick = { navigator.navigate(WatchImageScreenDestination(it))}
            )
            ChatInput(
                onSendMessage = viewModel::sendMessage,
                iconSectionColor = viewModel.config.colorOfIconSection,
                onImageSelectorClick = {
                    navigator.navigate(SendImageScreenDestination())
                }
            )
        }
    }

}