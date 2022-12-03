package com.euzhene.comranet.chatRoom.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.euzhene.comranet.R

enum class InputSelector {
    IMAGE, EMOJI, AUDIO, POLL,
}

@Composable
fun ChatInput(
    onSendMessage: (String) -> Unit,
    onImageSelectorClick: () -> Unit,
    onPollSelectorClick: () -> Unit,
    iconSectionColor: Color,
) {
    var inputText by rememberSaveable { mutableStateOf("") }
   // var shouldShowPollSelector by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            UserInputText(
                modifier = Modifier.weight(1f),
                textFieldValue = inputText,
                onTextChanged = { inputText = it },
            )
            Spacer(modifier = Modifier.width(6.dp))
            UserSendBtn {
                onSendMessage(inputText)
                inputText = ""
            }
        }
        UserInputSelector(iconSectionColor = iconSectionColor) {
            if (it == InputSelector.IMAGE) {
          //      shouldShowPollSelector = false
                onImageSelectorClick()
            } else if (it == InputSelector.POLL) {
                onPollSelectorClick()
            //    shouldShowPollSelector = true
            }
        }
    }
}


@Composable
fun PollSelector() {
    Box(
        Modifier
            .size(70.dp)
            .background(Color.Black)
    ) {

    }
}

@Composable
fun EmojiSelector() {

}


@Composable
fun UserInputSelector(
    iconSectionColor: Color,
    onInputSelectorChange: (InputSelector) -> Unit
) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        InputSelectorBtn(
            id = R.drawable.ic_image_selector,
            contentDescription = "Image selector",
            onClick = { onInputSelectorChange(InputSelector.IMAGE) },
            iconSectionColor = iconSectionColor
        )
        InputSelectorBtn(
            id = R.drawable.ic_emoji,
            contentDescription = "Emoji selector",
            iconSectionColor = iconSectionColor,
            onClick = { onInputSelectorChange(InputSelector.EMOJI) }
        )
        InputSelectorBtn(
            id = R.drawable.ic_audio_selector,
            contentDescription = "Audio selector",
            iconSectionColor = iconSectionColor,
        ) {
            onInputSelectorChange(InputSelector.AUDIO)
        }
        InputSelectorBtn(
            id = R.drawable.ic_poll,
            contentDescription = "Poll selector",
            iconSectionColor = iconSectionColor,
        ) {
            onInputSelectorChange(InputSelector.POLL)
        }
    }
}

@Composable
fun InputSelectorBtn(
    @DrawableRes id: Int,
    contentDescription: String,
    iconSectionColor: Color,
    onClick: () -> Unit,
) {
    Surface(modifier = Modifier.size(40.dp), color = Color.Transparent) {
        IconButton(onClick = onClick) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = id),
                contentDescription = contentDescription,
                colorFilter = ColorFilter.tint(iconSectionColor),
                contentScale = ContentScale.Fit
            )
        }

    }
}

@Composable
fun UserInputText(
    modifier: Modifier,
    textFieldValue: String,
    onTextChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = onTextChanged,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 250.dp)
            .padding(8.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Green,
            backgroundColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        textStyle = TextStyle(
            fontSize = 19.sp,
        ),
        placeholder = { Text("Enter a message") },
        shape = RoundedCornerShape(60.dp)
    )
}

@Composable
fun UserSendBtn(onSendMessage: () -> Unit) {
    Button(
        onClick = onSendMessage,
        elevation = ButtonDefaults.elevation(0.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f)),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = Color.Black.copy(alpha = 0.5f)
        ),
        modifier = Modifier.padding(4.dp)
    ) {
        Text("Send")
    }
}