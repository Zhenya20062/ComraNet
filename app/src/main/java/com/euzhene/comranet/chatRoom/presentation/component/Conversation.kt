package com.euzhene.comranet.chatRoom.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.PollData
import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig
import com.euzhene.comranet.preferences.presentation.ChatBackground
import com.euzhene.comranet.util.D_MMM_YYYY
import com.euzhene.comranet.util.datesEqual
import com.euzhene.comranet.util.mapTimestampToDate
import kotlinx.coroutines.flow.Flow
import java.util.*

@Composable
fun Conversation(
  //  newChatData: SnapshotStateList<ChatData>,
    chatDataPaging: Flow<PagingData<ChatData>>,
    modifier: Modifier,
    config: PreferencesConfig,
    onImageClick: (String) -> Unit,
    onPollChange:(ChatData, Int)->Unit,
) {
    Box(modifier = modifier) {
        ChatBackground(background = config.background, modifier = Modifier.fillMaxSize())

        val scrollState = rememberLazyListState()
        val pagingData = chatDataPaging.collectAsLazyPagingItems()
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            reverseLayout = true,
            state = scrollState,
        ) {
//            itemsIndexed(newChatData) { i, chatData ->
//                DateDivider(
//                    chatDataList = newChatData, index = i,
//                    backgroundColor = config.colorOfDateDividerBackground,
//                    textColor = config.colorOfDateDividerText,
//                )
//                ChatDataItem(
//                    chatData = chatData,
//                    config = config,
//                    onImageClick = { onImageClick(chatData.data) },
//                    onPollOptionClick = {onPollChange(chatData, it)}
//                )
//            }
            itemsIndexed(pagingData) { i, chatData ->
                if (chatData == null) return@itemsIndexed

                if (pagingData.itemSnapshotList.items.size > i)
                    DateDivider(
                        chatDataList = pagingData.itemSnapshotList.items,
                        index = i,
                        backgroundColor = config.colorOfDateDividerBackground,
                        textColor = config.colorOfDateDividerText,
                    )
                ChatDataItem(
                    chatData = chatData,
                    config = config,
                    onImageClick = { onImageClick(chatData.data) },
                    onPollOptionClick = {
                        onPollChange(chatData, it)
                    }

                )
            }

        }
    }

}

@Composable
fun DateDivider(
    chatDataList: List<ChatData>,
    index: Int,
    backgroundColor: Color,
    textColor: Color
) {
    if (index == 0) return

    val previousDate = chatDataList[index - 1].timestamp
    val currentDate = chatDataList[index].timestamp
    if (datesEqual(previousDate, currentDate, Calendar.DAY_OF_YEAR)) return
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(12.dp), color = backgroundColor) {
            Text(
                text = mapTimestampToDate(previousDate, D_MMM_YYYY), color = textColor,
                modifier = Modifier.padding(5.dp)
            )
        }
    }

}