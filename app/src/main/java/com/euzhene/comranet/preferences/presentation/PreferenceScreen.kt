package com.euzhene.comranet.preferences.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.euzhene.comranet.R
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.euzhene.comranet.chatRoom.presentation.component.ChatDataItem
import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig
import com.euzhene.comranet.util.D_MMM_YYYY
import com.euzhene.comranet.util.PathUtils
import com.euzhene.comranet.util.mapTimestampToDate
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


//todo add "opacity of background"
//todo add "pattern of message date"
@RootNavGraph(false)
@Destination
@Composable
fun PreferenceScreen(
    navigator: DestinationsNavigator,
    viewModel: PreferencesViewModel,
) {
    Scaffold(topBar = {
        TopAppBar(backgroundColor = viewModel.config.colorOfAppBar, navigationIcon = {
            IconButton(onClick = {
                viewModel.saveConfigAndExit()
                navigator.popBackStack()

            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back to room")
            }
        }, title = {
            Text(text = "Settings")
        })
    }) {
        val scrollState = rememberScrollState()
        Box(Modifier.padding(it)) {
            Column(
                Modifier
                    .padding(horizontal = 10.dp)
                    .verticalScroll(scrollState)
            ) {
                FontPreferenceItem(onValueChange = {
                    viewModel.config = viewModel.config.copy(fontSize = it)
                }, config = viewModel.config)

                BackgroundPreferenceItem(onBackgroundChange = {
                    viewModel.config = viewModel.config.copy(background = it)
                })
                ColorPreferenceItems(onColorChange = { i: Color, b: ColorCustomizer ->
                    viewModel.updateColor(i, b)
                })
            }
        }
    }
    BackHandler {
        viewModel.saveConfigAndExit()
        navigator.popBackStack()
    }

}

@Composable
fun ColorPreferenceItems(
    onColorChange: (Color, ColorCustomizer) -> Unit,
) {
    var shouldShowColorPicker by rememberSaveable { mutableStateOf(false) }
    var colorDesign by rememberSaveable { mutableStateOf(ColorCustomizer.NONE) }

    PreferenceItem(title = "Colors") {
        if (shouldShowColorPicker) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HarmonyColorPicker(
                    onColorChanged = {
                        onColorChange(it.toColor(), colorDesign)
                    },
                    harmonyMode = ColorHarmonyMode.NONE,
                    modifier = Modifier.size(300.dp)
                )
                IconButton(
                    onClick = {
                        shouldShowColorPicker = false
                    },
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(color = Color(0xff00751d), shape = CircleShape) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_visibility_off),
                                contentDescription = "hide",
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }
                        Text(text = "Hide")
                    }


                }
            }
        }
        ColorPreferenceItem(
            title = "Receiver's message",
            onSelected = {
                colorDesign = ColorCustomizer.YOUR_MESSAGE
                shouldShowColorPicker = true
            })
        ColorPreferenceItem(
            title = "Sender's message",
            onSelected = {
                colorDesign = ColorCustomizer.SENDER_MESSAGE
                shouldShowColorPicker = true
            })
        ColorPreferenceItem(
            title = "App bar",
            onSelected = {
                colorDesign = ColorCustomizer.APP_BAR
                shouldShowColorPicker = true
            })
        ColorPreferenceItem(
            title = "Icon section",
            onSelected = {
                colorDesign = ColorCustomizer.ICON_SECTION
                shouldShowColorPicker = true
            })
        ColorPreferenceItem(
            title = "Message text",
            onSelected = {
                colorDesign = ColorCustomizer.MESSAGE_TEXT
                shouldShowColorPicker = true
            })
        ColorPreferenceItem(
            title = "Message username",
            onSelected = {
                colorDesign = ColorCustomizer.MESSAGE_USERNAME
                shouldShowColorPicker = true
            })
        ColorPreferenceItem(
            title = "Message date",
            onSelected = {
                colorDesign = ColorCustomizer.MESSAGE_DATE
                shouldShowColorPicker = true
            })
        ColorPreferenceItem(
            title = "Date divider background",
            onSelected = {
                colorDesign = ColorCustomizer.DATE_DIVIDER_BACKGROUND
                shouldShowColorPicker = true
            })
        ColorPreferenceItem(
            title = "Date divider text",
            onSelected = {
                colorDesign = ColorCustomizer.DATE_DIVIDER_TEXT
                shouldShowColorPicker = true
            })
    }
}

@Composable
fun ColorPreferenceItem(title: String, onSelected: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, start = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 20.sp, modifier = Modifier.weight(1f))
        TextButton(onClick = onSelected) {
            Text(text = "Set color", fontSize = 20.sp)
        }
    }
}

@Composable
fun BackgroundPreferenceItem(
    onBackgroundChange: (String) -> Unit,
) {
    var urlText by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            if (it == null) return@rememberLauncherForActivityResult

            val path = PathUtils.getRealPathFromURI(context, it)
            onBackgroundChange(path!!)
        }

    PreferenceItem(title = "Background", horizontal = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = urlText,
            onValueChange = { urlText = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            label = { Text("URL") },
        )
        Button(
            onClick = { onBackgroundChange(urlText) },
        ) {
            Text(text = "Select background by url")
        }
        Button(
            onClick = {
                launcher.launch("image/*")
            },
        ) {
            Text(text = "Select background by gallery")
        }
    }
}

@Composable
fun PreferenceItem(
    title: String,
    modifier: Modifier = Modifier,
    horizontal: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = horizontal,
    ) {
        Text(
            text = title,
            fontSize = 21.sp,
            color = Color.Blue,
            modifier = Modifier.align(Alignment.Start)
        )
        content()
    }
}

@Composable
fun FontPreferenceItem(onValueChange: (Float) -> Unit, config: PreferencesConfig) {
    var height by rememberSaveable { mutableStateOf(0) }

    PreferenceItem(title = "Message text size") {
        Spacer(modifier = Modifier.height(5.dp))
        Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
            Slider(value = config.fontSize, valueRange = 12f..30f, onValueChange = {
                onValueChange(it)
            }, modifier = Modifier.weight(1f))
            Text(
                text = config.fontSize.toInt().toString(),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            Modifier
                .wrapContentHeight()
        ) {
            ChatBackground(
                background = config.background,
                Modifier
                    .fillMaxWidth()
                    .height(with(LocalDensity.current) { (height).toDp() }),
            )

            Column(Modifier
                .onGloballyPositioned {
                    height = it.size.height
                }) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = config.colorOfDateDividerBackground
                    ) {
                        Text(
                            text = mapTimestampToDate(1660575812000, D_MMM_YYYY),
                            color = config.colorOfDateDividerText,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
                ChatDataItem(
                    chatData = ChatData(
                        1660575812,
                        "Bob Harris",
                        false,
                        ChatDataType.MESSAGE,
                        "Do you know what time it is?"
                    ),
                    config = config,
                    onImageClick = {}
                )
                ChatDataItem(
                    chatData = ChatData(
                        1661586812,
                        "Jackie",
                        true,
                        ChatDataType.MESSAGE,
                        "It's morning in Tokyo \uD83D\uDE0E"
                    ),
                    config = config,
                    onImageClick = {}
                )

            }


        }
    }

}

@Composable
fun ChatBackground(background: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = background
            ?: LocalContext.current.getDrawable(R.drawable.ic_back_official)!!,
        contentDescription = "chat background",
        alpha = 0.99f,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        colorFilter = if (background == null) ColorFilter.tint(Color(0xff9dd3f5)) else null
    )
}