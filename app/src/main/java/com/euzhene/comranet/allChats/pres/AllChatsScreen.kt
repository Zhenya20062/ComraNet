package com.euzhene.comranet.allChats.pres

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.euzhene.comranet.CircleImage
import com.euzhene.comranet.chatRoom.presentation.theme.telegramBlueColor
import com.euzhene.comranet.destinations.AddChatScreenDestination
import com.euzhene.comranet.destinations.ChatRoomScreenDestination
import com.euzhene.comranet.destinations.PreferenceScreenDestination
import com.euzhene.comranet.util.AllChatsTransition
import com.euzhene.comranet.util.NetworkConnection
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Destination(style = AllChatsTransition::class)
@Composable
fun AllChatsScreen(
    navigator: DestinationsNavigator,
    viewModel: AllChatsViewModel,
) {

    val scaffoldState = rememberScaffoldState()
    var shouldOpenMenu by rememberSaveable { mutableStateOf(false) }
    var showChatOptions by rememberSaveable { mutableStateOf(false) }
    val systemUiController = rememberSystemUiController()
    var hasInternet by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        val color = if (showChatOptions) Color.White else telegramBlueColor
        systemUiController.setStatusBarColor(color = color)
        Box(
            Modifier
                .background(color)
                .fillMaxWidth()
                .height(55.dp)
        )

        if (showChatOptions) {
            AllChatsTopBarOptions(onChatRemove = {})
        } else {
            AllChatsTopBarDefault(
                modifier = Modifier
                    .height(55.dp)
                    .padding(horizontal = 12.dp),
                onMenuClick = {
                    shouldOpenMenu = true
                }, hasInternet = hasInternet
            )
        }
    },
        scaffoldState = scaffoldState,
        drawerContent = {
            SideMenu(
                onSettingsClick = {
                    navigator.navigate(PreferenceScreenDestination())
                }
            )
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
        val context = LocalContext.current
        LaunchedEffect(key1 = Unit) {
            viewModel.errorMessage.collectLatest {
                scaffoldState.snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long)
            }
        }
        LaunchedEffect(key1 = Unit) {
            with(NetworkConnection) {
                onAvailable = {
                    //todo:refresh content
                    hasInternet = true
                }
                onLost = {
                    hasInternet = false
                }
                initNetworkStatus(context)
            }
        }
        val chatInfoList = viewModel.pagingData.collectAsLazyPagingItems()

        Box(Modifier.padding(it)) {
            Text(
                "Alpha-06", color = Color.Red, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 12.dp)
                    .zIndex(10f)
            )

            LazyColumn(Modifier.fillMaxSize()) {
                items(chatInfoList) { chatInfo ->
                    if (chatInfo == null) return@items

                    com.euzhene.comranet.Button(modifier = Modifier
                        .padding(horizontal = 8.dp), onClick = {
                        navigator.navigate(ChatRoomScreenDestination(chatId = chatInfo.chat_id))
                    }, onLongClick = {
                        showChatOptions = !showChatOptions
                    }) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(50.dp),
                                shape = CircleShape,
                                color = Color.LightGray
                            ) {
                                AsyncImage(
                                    model = chatInfo.photo_url,
                                    contentDescription = "chat photo",
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                chatInfo.chat_name,
                                fontSize = 25.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                    Divider(startIndent = (50 + 10).dp)

                }

            }
        }

    }
    if (shouldOpenMenu) {
        coroutineScope.launch {
            shouldOpenMenu = false
            scaffoldState.drawerState.open()
        }
    }
}

@Composable
fun AllChatsTopBarOptions(onChatRemove: () -> Unit) {
    Row() {

    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AllChatsTopBarDefault(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit,
    hasInternet: Boolean
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        CircleImage(
            onClick = onMenuClick,
            imageVector = Icons.Default.Menu,
            modifier = Modifier
                .size(39.dp),
            innerPadding = 7.dp
        )
        Spacer(modifier = Modifier.width(15.dp))
        AnimatedContent(targetState = hasInternet, transitionSpec = {
            EnterTransition.None with ExitTransition.None
        }) {


            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (hasInternet) "Comranet" else "Waiting for network",
                    modifier = Modifier
                        .animateEnterExit(
                            enter = slideInVertically(),
                            exit = slideOutVertically(targetOffsetY = {
                                it / 2
                            }, animationSpec = tween(durationMillis = 40))
                        )
                        .padding(start = 10.dp),
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                if (!hasInternet) {
                    DotsLoading(dotSize = 20)
                }
            }

        }

    }
}

@Composable
fun DotsLoading(
    dotSize: Int,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateScaleWithDelay(delay: Int): State<Float> {
        return infiniteTransition.animateFloat(initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 400 * 6
                    0f at delay with LinearEasing
                    1f at delay + 200 with LinearEasing
                    0.8f at delay + 400 * 4
                }
            ))
    }

    val alpha1 by animateScaleWithDelay(delay = 0)
    val alpha2 by animateScaleWithDelay(delay = 400)
    val alpha3 by animateScaleWithDelay(delay = 800)

    Row(modifier = modifier) {
        Dot(size = dotSize, scale = alpha1, color = Color.White)
        Dot(size = dotSize, scale = alpha2, color = Color.White)
        Dot(size = dotSize, scale = alpha3, color = Color.White)
    }
}

@Composable
fun Dot(
    size: Int,
    scale: Float,
    color: Color,
) {
    Text(
        text = ".",
        fontSize = size.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.alpha(scale),
        color = color,
        textAlign = TextAlign.Start
    )
}