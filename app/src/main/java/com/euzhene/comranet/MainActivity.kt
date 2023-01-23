package com.euzhene.comranet

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.euzhene.comranet.addChat.present.AddChatScreen
import com.euzhene.comranet.addChat.present.AddChatViewModel
import com.euzhene.comranet.addChat.present.SetChatInfoScreen
import com.euzhene.comranet.allChats.pres.AllChatsScreen
import com.euzhene.comranet.allChats.pres.AllChatsViewModel
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
import com.euzhene.comranet.util.defaultTransition
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.onesignal.OneSignal
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.popUpTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}


    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()


        var shouldContinueSplashScreen = true

        super.onCreate(savedInstanceState)
        val viewContent = findViewById<View>(android.R.id.content)
        viewContent.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                return if (!shouldContinueSplashScreen) {
                    viewContent.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        }

        )
        OneSignal.initWithContext(this)
        setContent {
            val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }
            rememberAnimatedNavHostEngine()
            val engine = rememberAnimatedNavHostEngine(
                rootDefaultAnimations = defaultTransition
            )
            val navController = rememberAnimatedNavController()

            DestinationsNavHost(
                navGraph = NavGraphs.root,
                navController = navController,
                engine = engine
            ) {
                composable(RegisterScreenDestination) {

                    val viewModel = hiltViewModel<AuthViewModel>()

                    if (!viewModel.hasUserData.value) {

                        RegisterScreen(navigator = destinationsNavigator, viewModel = viewModel)
                        shouldContinueSplashScreen = false
                    }
                    else if (viewModel.shouldGoToChatRoom.value) {
                        destinationsNavigator.navigate(AllChatsScreenDestination()) {
                            popUpTo(RegisterScreenDestination) { inclusive = true }
                        }
                    }
                }

                composable(LoginScreenDestination) {

                    val viewModel = hiltViewModel<AuthViewModel>()
                    LoginScreen(navigator = destinationsNavigator, viewModel = viewModel)
                }
                composable(AllChatsScreenDestination) {
                    val viewModel = hiltViewModel<AllChatsViewModel>()
                    if (viewModel.chatInfoCount.value != AllChatsViewModel.NO_RESPONSE_FROM_DB_VALUE) {
                        shouldContinueSplashScreen = false
                    }

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
                    val viewModel = hiltViewModel<ChatRoomViewModel>()
                    SendImageScreen(
                        navigator = destinationsNavigator,
                        viewModel = viewModel,
                        navArgs.chatId
                    )
                }

            }
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}



