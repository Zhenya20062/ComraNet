package com.euzhene.comranet.chatRoom.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel.Companion.CHAT_ID_STATE
import com.euzhene.comranet.chatRoom.presentation.component.ChatInput
import com.euzhene.comranet.chatRoom.presentation.component.Conversation
import com.euzhene.comranet.chatRoom.presentation.component.PollSelector
import com.euzhene.comranet.destinations.PreferenceScreenDestination
import com.euzhene.comranet.destinations.SendImageScreenDestination
import com.euzhene.comranet.destinations.WatchImageScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@RootNavGraph(false)
@Destination
@Composable
fun ChatRoomScreen(
    navigator: DestinationsNavigator,
    viewModel: ChatRoomViewModel,
    chatId: String,
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val coroutine = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        topBar = {
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
        },
        snackbarHost = {
            with(viewModel.chatError.value) {
                LaunchedEffect(key1 = this) {
                    if (this@with.isNotBlank()) {
                        it.showSnackbar(this@with)
                    }
                }
            }


        }, sheetContent = {
            PollSelector()
        }
    ) {
        Box(Modifier.padding(it)) {
            ChatRoom(viewModel = viewModel, navigator,
                onPollSelectorClick = {
                    coroutine.launch {
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    }
                })
        }
    }
}

@Composable
fun ChatRoom(
    viewModel: ChatRoomViewModel, navigator: DestinationsNavigator,
    onPollSelectorClick: () -> Unit
) {
    Box {
        Column {
            Conversation(
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
                },
                onPollSelectorClick = onPollSelectorClick
            )
        }
    }

}