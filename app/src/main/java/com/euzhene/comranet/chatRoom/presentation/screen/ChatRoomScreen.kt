package com.euzhene.comranet.chatRoom.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel.Companion.CHAT_ID_STATE
import com.euzhene.comranet.chatRoom.presentation.component.ChatInput
import com.euzhene.comranet.chatRoom.presentation.component.Conversation
import com.euzhene.comranet.destinations.PreferenceScreenDestination
import com.euzhene.comranet.destinations.SendImageScreenDestination
import com.euzhene.comranet.destinations.WatchImageScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(false)
@Destination
@Composable
fun ChatRoomScreen(
    navigator: DestinationsNavigator,
    viewModel: ChatRoomViewModel,
    chatId: String,
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
    }, snackbarHost = {
        with (viewModel.chatError.value) {
            LaunchedEffect(key1 = this) {
                if (this@with.isNotBlank()) {
                    it.showSnackbar(this@with)
                }
            }
        }


    }
    ) {
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
              //  newChatData = viewModel.observedChatData,
                modifier = Modifier.weight(1f),
                chatDataPaging = viewModel.chatDataPaging,
                config = viewModel.config,
                onImageClick = { navigator.navigate(WatchImageScreenDestination(it)) },
                onPollChange = viewModel::changePoll
            )
            ChatInput(
                onSendMessage = viewModel::sendMessage,
                iconSectionColor = viewModel.config.colorOfIconSection,
                onImageSelectorClick = {
                    navigator.navigate(
                        SendImageScreenDestination(
                            viewModel.stateHandle.get<String>(
                                CHAT_ID_STATE
                            )!!
                        )
                    )
                }
            )
        }
    }

}