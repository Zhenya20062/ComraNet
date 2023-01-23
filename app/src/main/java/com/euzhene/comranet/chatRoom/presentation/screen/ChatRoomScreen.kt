package com.euzhene.comranet.chatRoom.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel
import com.euzhene.comranet.chatRoom.presentation.component.ChatInput
import com.euzhene.comranet.chatRoom.presentation.component.Conversation
import com.euzhene.comranet.chatRoom.presentation.component.PollSelector
import com.euzhene.comranet.destinations.SendImageScreenDestination
import com.euzhene.comranet.destinations.WatchImageScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Destination
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
                backgroundColor = viewModel.config.chatTheme.appbarBackground,
                content = {
                    ChatRoomAppBar(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(vertical = 4.dp),
                        groupInfo = viewModel.groupInfo.value,
                        config =viewModel.config
                    )
                },
            )
        },
        snackbarHost = {
            with(viewModel.errorMessage.value) {
                LaunchedEffect(key1 = this) {
                    if (this@with.isNotBlank()) {
                        it.showSnackbar(this@with)
                    }
                }
            }


        }, sheetContent = {
            return@BottomSheetScaffold
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
                config = viewModel.config,
                onImageSelectorClick = {
                    navigator.navigate(
                        SendImageScreenDestination(viewModel.chatId)
                    )
                },
                onPollSelectorClick = onPollSelectorClick
            )
        }
    }

}