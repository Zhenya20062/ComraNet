package com.euzhene.comranet.chatRoom.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.euzhene.comranet.chatRoom.domain.entity.PollData
import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig
import com.euzhene.comranet.util.H_MM
import com.euzhene.comranet.util.mapTimestampToDate
import com.google.gson.Gson

@Composable
fun ChatDataItem(
    chatData: ChatData,
    config: PreferencesConfig,
    onImageClick: () -> Unit,
    onPollOptionClick: (Int) -> Unit,
) {
    val backgroundColor =
        if (chatData.owner) config.colorOfReceiverMessage
        else config.colorOfSenderMessage
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (chatData.owner) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            backgroundColor = backgroundColor,
            shape = chatDataCardShape(owner = chatData.owner)
        ) {
            Column(
                modifier = Modifier
                    .widthIn(90.dp, 340.dp)
                    .padding(start = 4.dp, end = 4.dp, top = 4.dp),
            ) {
                if (!chatData.owner) {
                    Name(chatData.senderUsername, config.colorOfMessageUsername)
                }

                if (chatData.type == ChatDataType.MESSAGE) {
                    Message(
                        message = chatData.data,
                        fontSize = config.fontSize,
                        color = config.colorOfMessageText
                    )
                } else if (chatData.type == ChatDataType.IMAGE) {
                    Picture(url = chatData.data, onImageClick = onImageClick)
                } else if (chatData.type == ChatDataType.POLL) {
                    PollItem(chatData = chatData, onOptionClick = onPollOptionClick)
                }

                Date(
                    date = mapTimestampToDate(chatData.timestamp, H_MM),
                    Modifier.align(Alignment.End),
                    color = config.colorOfMessageDate
                )
            }

        }
    }

}

@Composable
fun PollItem(chatData: ChatData, onOptionClick: (Int) -> Unit) {
    val pollData = Gson().fromJson(chatData.data, PollData::class.java)

    Column {
        Text(text = pollData.heading, color = Color.Black, fontSize = 22.sp)
        pollData.options.forEachIndexed { index, pair ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = pollData.yourChoice == index,
                    onClick = {
                        if (pollData.yourChoice != index)
                            onOptionClick(index)
                    },
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = pair.first,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = pair.second.toString(),
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Bottom),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun Name(name: String, color: Color) {
    Text(
        text = name,
        fontSize = 15.sp,
        color = color
    )
}

@Composable
fun Message(message: String, fontSize: Float, color: Color) {
    Text(
        text = message,
        color = color,
        fontSize = fontSize.sp,
        modifier = Modifier.padding(end = 29.dp)
    )
}

@Composable
fun Picture(url: String, onImageClick: () -> Unit) {
    AsyncImage(
        model = url,
        contentDescription = "",
        onError = {
        },
        onLoading = {
        },
        onSuccess = {
        },
        modifier = Modifier
            .height(450.dp)
            .width(300.dp)
            .padding(bottom = 4.dp)
            .clickable(onClick = onImageClick),
    )
}

@Composable
fun Date(date: String, modifier: Modifier, color: Color) {
    Text(
        text = date,
        fontSize = 13.sp,
        modifier = modifier
            .offset(y = (-4).dp),
        color = color
    )
}

@Composable
fun chatDataCardShape(owner: Boolean): Shape {
    val roundedCorner = RoundedCornerShape(12.dp)
    return if (owner) {
        roundedCorner.copy(bottomEnd = CornerSize(0))
    } else {
        roundedCorner.copy(bottomStart = CornerSize(0))
    }
}
