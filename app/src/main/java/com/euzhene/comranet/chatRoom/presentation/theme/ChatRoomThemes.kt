package com.euzhene.comranet.chatRoom.presentation.theme

import androidx.compose.ui.graphics.Color
import com.euzhene.comranet.preferences.domain.entity.ChatColorTheme

/*val lightBlueTheme = ChatColorTheme(
    appbarBackground =
)
val blackTheme = ChatColorTheme(

)*/
val telegramBlueColor = Color(80,126,162)

//todo(use material theme instead)
val darkBlueTheme = ChatColorTheme(
    name="dark blue theme",
    appbarBackground = Color(39, 46, 64),
    appbarGroupName = Color.White,
    messageReceiverBackground = Color(70, 104, 149),
    messageSenderContent = Color.White,
    messageReceiverContent = Color.White,
    appbarBackIcon = Color(85, 168, 255),
    appbarMemberCount = Color(112, 124, 140),
    messageReceiverTime = Color(117, 149, 187),
    messageSenderTime = Color(117, 149, 187),
    messageSenderBackground = Color(37, 44, 62),
    chatRoomBackground = Color(26, 32, 44),
    messageSenderName = Color.White,
    iconSection = Color(38, 45, 63),
    dateDividerBackground = Color.Transparent,
    dateDividerText = Color(132, 146, 155),
    bottomSection = Color(3, 79, 138),
    inputText = Color.White
)
val iTheme = ChatColorTheme(
    name="iTheme",

    appbarBackground = Color.White,
    appbarGroupName = Color(92,92,92),
    appbarMemberCount = Color(207,209,210),
    appbarBackIcon = Color(96,103,104),

    chatRoomBackground = Color(216,228,244),

    dateDividerBackground = Color(170,192,211),
    dateDividerText = Color(207,218,229),

    messageReceiverBackground = Color(54,143,235),
    messageReceiverContent = Color.White,
    messageReceiverTime = Color.White.copy(alpha = 0.8f),

    messageSenderContent = Color.Black,
    messageSenderTime = Color(207,209,210),
    messageSenderBackground = Color.White,
    messageSenderName = Color(159,197,227),

    iconSection = Color(216,228,244),

    bottomSection = Color(125, 163, 193),
    inputText = Color.White
)

val defaultTheme = iTheme
val themeList = listOf(darkBlueTheme, iTheme)