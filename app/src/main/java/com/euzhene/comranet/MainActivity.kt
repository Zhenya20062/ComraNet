package com.euzhene.comranet

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.euzhene.comranet.addChat.present.AddChatScreen
import com.euzhene.comranet.addChat.present.AddChatViewModel
import com.euzhene.comranet.addChat.present.SetChatInfoScreen
import com.euzhene.comranet.allChats.AllChatsScreen
import com.euzhene.comranet.allChats.AllChatsViewModel
import com.euzhene.comranet.autorization.presentation.AuthViewModel
import com.euzhene.comranet.autorization.presentation.screen.LoginScreen
import com.euzhene.comranet.autorization.presentation.screen.RegisterScreen
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel.Companion.CHAT_ID_STATE
import com.euzhene.comranet.chatRoom.presentation.screen.ChatRoomScreen
import com.euzhene.comranet.chatRoom.presentation.screen.SendImageScreen
import com.euzhene.comranet.destinations.*
import com.euzhene.comranet.preferences.presentation.PreferenceScreen
import com.euzhene.comranet.preferences.presentation.PreferencesViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }

            DestinationsNavHost(navGraph = NavGraphs.root) {
                composable(RegisterScreenDestination) {
                    val viewModel = hiltViewModel<AuthViewModel>()
                    RegisterScreen(navigator = destinationsNavigator, viewModel = viewModel,)
                }

                composable(LoginScreenDestination) {
                    val viewModel = hiltViewModel<AuthViewModel>()
                    LoginScreen(navigator = destinationsNavigator, viewModel = viewModel)
                }
                composable(AllChatsScreenDestination) {
                    val viewModel = hiltViewModel<AllChatsViewModel>()
                    AllChatsScreen(navigator = destinationsNavigator, viewModel = viewModel)
                }
                composable(AddChatScreenDestination) {
                    val viewModel = hiltViewModel<AddChatViewModel>(viewModelStoreOwner)
                    AddChatScreen(navigator = destinationsNavigator, viewModel = viewModel)
                }
                composable(SetChatInfoScreenDestination) {
                    val viewModel = hiltViewModel<AddChatViewModel>(viewModelStoreOwner)
                    SetChatInfoScreen(navigator = destinationsNavigator, viewModel = viewModel)
                }
                composable(ChatRoomScreenDestination) {
                    navBackStackEntry.arguments!!.putString(CHAT_ID_STATE, navArgs.chatId)
                    val viewModel = hiltViewModel<ChatRoomViewModel>()
                    ChatRoomScreen(
                        navigator = destinationsNavigator,
                        viewModel = viewModel,
                        chatId = navArgs.chatId,
                    )
                }
                composable(PreferenceScreenDestination) {
                    val viewModel = hiltViewModel<PreferencesViewModel>()
                    PreferenceScreen(
                        navigator = destinationsNavigator,
                        viewModel = viewModel,
                    )
                }
                composable(SendImageScreenDestination) {
                    navBackStackEntry.arguments!!.putString(CHAT_ID_STATE, navArgs.chatId)
                    val viewModel = hiltViewModel<ChatRoomViewModel>()
                    SendImageScreen(navigator = destinationsNavigator, viewModel = viewModel, navArgs.chatId)
                }

            }
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}



