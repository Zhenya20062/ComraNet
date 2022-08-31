package com.euzhene.comranet

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import com.euzhene.comranet.autorization.presentation.AuthViewModel
import com.euzhene.comranet.autorization.presentation.screen.LoginScreen
import com.euzhene.comranet.autorization.presentation.screen.RegisterScreen
import com.euzhene.comranet.chatRoom.hilt.DataModule
import com.euzhene.comranet.chatRoom.hilt.ViewModelModule
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel
import com.euzhene.comranet.chatRoom.presentation.screen.ChatRoomScreen
import com.euzhene.comranet.chatRoom.presentation.screen.SendImageScreen
import com.euzhene.comranet.destinations.*
import com.euzhene.comranet.preferences.presentation.PreferenceScreen
import com.euzhene.comranet.preferences.presentation.PreferencesViewModel
import com.google.firebase.auth.FirebaseUser
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    @Inject
    lateinit var assistedFactory: ViewModelModule.ChatRoomViewModelAssistedFactory

    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DestinationsNavHost(navGraph = NavGraphs.root) {
                composable(RegisterScreenDestination) {
                    val viewModel = hiltViewModel<AuthViewModel>()
                    RegisterScreen(navigator = destinationsNavigator, viewModel = viewModel,
                        onGetUser = { user = it })
                }
                composable(LoginScreenDestination) {
                    val viewModel = hiltViewModel<AuthViewModel>()
                    LoginScreen(
                        navigator = destinationsNavigator,
                        viewModel = viewModel,
                        onGetUser = { user = it })
                }
                composable(ChatRoomScreenDestination) {
                    val viewModel: ChatRoomViewModel by viewModels {
                        ViewModelModule.provideFactory(assistedFactory, user)
                    }
                    ChatRoomScreen(
                        navigator = destinationsNavigator,
                        viewModel = viewModel,
                        // navArgs.user,
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
                    val viewModel: ChatRoomViewModel by viewModels {
                        ViewModelModule.provideFactory(assistedFactory, user)
                    }
                    SendImageScreen(navigator = destinationsNavigator, viewModel = viewModel)
                }

            }
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}



