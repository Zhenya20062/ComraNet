package com.euzhene.comranet.chatRoom.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig

@Composable
fun ChatRoomAppBar(
    modifier: Modifier = Modifier, groupInfo: ChatInfo?,
    config: PreferencesConfig
) {
    Image(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "exit chat room",
        modifier = modifier
            .size(30.dp),
        colorFilter = ColorFilter.tint(config.chatTheme.appbarBackIcon)
    )
    if (groupInfo == null) return
    Spacer(modifier = Modifier.width(8.dp))

    Surface(shape = CircleShape) {
        AsyncImage(
            model = groupInfo.photo_url,
            contentDescription = "group photo",
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Crop
        )
    }
    Spacer(modifier = Modifier.width(16.dp))

    Column {
        Text(
            text = groupInfo.chat_name,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = config.chatTheme.appbarGroupName
        )
        val text = if (groupInfo.members.size == 1) "member" else "members"
        Text(text = "${groupInfo.members.size} $text", color = config.chatTheme.appbarMemberCount)
    }
}