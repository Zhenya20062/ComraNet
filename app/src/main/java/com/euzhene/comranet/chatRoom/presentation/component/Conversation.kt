package com.euzhene.comranet.chatRoom.presentation.component

import android.util.Log
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.euzhene.comranet.TAG_PRESENT
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig
import com.euzhene.comranet.preferences.presentation.ChatBackground
import com.euzhene.comranet.util.D_MMM_YYYY
import com.euzhene.comranet.util.datesEqual
import com.euzhene.comranet.util.mapTimestampToDate
import kotlinx.coroutines.flow.Flow
import java.util.*

@Composable
fun Conversation(
    chatDataPaging: Flow<PagingData<Any>>,
    modifier: Modifier,
    config: PreferencesConfig,
    onImageClick: (String) -> Unit,
    onPollChange: (ChatData, Int) -> Unit,
) {
    Box(modifier = modifier) {
        ChatBackground(config = config, modifier = Modifier.fillMaxSize())

        val scrollState = rememberLazyListState()
        val pagingData = chatDataPaging.collectAsLazyPagingItems()

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            reverseLayout = true,
            state = scrollState,
        ) {


            itemsIndexed(pagingData,
//                key = { index, item ->
//                if (item is ChatData)
//                    item.messageId
//                else (item as DateItem).date
//            }
            ) { i, chatData ->
                if (chatData == null) return@itemsIndexed

                if (chatData is ChatData) {
                    ChatDataItem(
                        chatData = chatData,
                        config = config,
                        onImageClick = { onImageClick(chatData.data) },
                        onPollOptionClick = {
                            onPollChange(chatData, it)
                        }

                    )
                } else {
                    chatData as DateItem
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Surface(shape = RoundedCornerShape(12.dp), color = config.chatTheme.dateDividerBackground) {
                            Text(
                                text = chatData.date, color = config.chatTheme.dateDividerText,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
//                    DateDivider(
//                        chatDataList = pagingData.itemSnapshotList.items,
//                        index = i,
//                        backgroundColor = config.chatTheme.dateDividerBackground,
//                        textColor = config.chatTheme.dateDividerText,
//                    )
                }
          //      if (pagingData.itemSnapshotList.items.size > i)


            }
            if (pagingData.loadState.append is LoadState.Error) {
                item {
                    Button(onClick = {
                        pagingData.retry()
                    }) {
                        Text("retry")
                    }
                }

            }

        }
    }

}

data class DateItem(
    val date: String,
)

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