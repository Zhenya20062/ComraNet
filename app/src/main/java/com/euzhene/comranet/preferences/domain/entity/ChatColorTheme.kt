package com.euzhene.comranet.preferences.domain.entity

import androidx.compose.ui.graphics.Color

data class ChatColorTheme(
    val name:String,
    //<--appbar-->
    val appbarBackground: Color,
    val appbarBackIcon: Color,
    val appbarGroupName: Color,
    val appbarMemberCount: Color,

    //<--message-->
    val messageReceiverBackground: Color,
    val messageReceiverContent: Color,
    val messageReceiverTime: Color,

    val messageSenderBackground: Color,
    val messageSenderContent: Color,
    val messageSenderTime: Color,
    val messageSenderName: Color,

    //<--bottom-->
    val iconSection: Color,
    val bottomSection:Color,
    val inputText:Color,
    //<--other-->
    val dateDividerBackground: Color,
    val dateDividerText: Color,
    val chatRoomBackground:Color,
)
