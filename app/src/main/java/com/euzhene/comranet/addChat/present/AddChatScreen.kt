package com.euzhene.comranet.addChat.present

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.euzhene.comranet.R
import com.euzhene.comranet.addChat.domain.entity.UserInfo
import com.euzhene.comranet.destinations.AllChatsScreenDestination
import com.euzhene.comranet.destinations.SetChatInfoScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@RootNavGraph(start = false)
@Destination
fun AddChatScreen(
    navigator: DestinationsNavigator,
    viewModel: AddChatViewModel,
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("New Group", color = Color.White) },
            backgroundColor = Color(0xff5291ff),
            navigationIcon = {
                IconButton(
                    onClick = { navigator.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "back to chat room",
                        tint = Color.White
                    )
                }
            }
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {

                navigator.navigate(SetChatInfoScreenDestination())
            },
            backgroundColor = Color(0xff5291ff)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "next",
                tint = Color.White
            )
        }
    }) {
        LaunchedEffect(key1 = Unit) {
            viewModel.getAllUsers()
        }

        Column(Modifier.padding(it)) {
            IncludedUsers(users = viewModel.includedUsers)
            Text("Alpha-01", color = Color.Red, modifier = Modifier.padding(horizontal = 15.dp))
            ListOfUsers(viewModel = viewModel)
        }
    }
}

@Composable
fun ListOfUsers(viewModel: AddChatViewModel) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(10.dp, 0.dp)) {
        items(viewModel.allUsers.value) { userInfo ->
            var chosen by rememberSaveable {
                mutableStateOf(false)
            }
            Row {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = Color.LightGray
                ) {
                    AsyncImage(
                        model = userInfo.photoUrl,
                        contentDescription = "user photo",
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    userInfo.username,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .clickable {
                            if (chosen) viewModel.removeMember(userInfo) else viewModel.addNewMember(
                                userInfo
                            )
                            chosen = !chosen
                        }
                        .fillMaxWidth()
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 5.dp),
                startIndent = 30.dp
            )
        }
    }
}

@Composable
fun IncludedUsers(users: List<UserInfo>) {
    var username by remember { mutableStateOf("") }
    LaunchedEffect(key1 = users.size) {
        username = ""
        users.forEach {
            username += it.username + ", "
        }
    }

    Box(Modifier.heightIn(30.dp, 150.dp)) {
        TextField(
            value = username,
            onValueChange = { username = it },
            placeholder = {
                Text(
                    "Who would you like to add?",
                    color = Color.Unspecified.copy(alpha = 0.4f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
        )
    }

}

@Destination
@Composable
fun SetChatInfoScreen(
    navigator: DestinationsNavigator,
    viewModel: AddChatViewModel,
) {
    if (viewModel.isLoading.value) {
        createLoadingAlertDialog(title = "Creating chat...")
    }
    if (viewModel.success.value) {
        navigator.popBackStack(AllChatsScreenDestination, false)
    }
    if (viewModel.error.value.isNotEmpty()) {
        Toast.makeText(LocalContext.current, viewModel.error.value, Toast.LENGTH_LONG).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Group", color = Color.White) },
                backgroundColor = Color(0xff5291ff),
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back to choosing members",
                            tint = Color.White
                        )
                    }
                }
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createChat() },
                backgroundColor = Color(0xff5291ff)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "create chat",
                    tint = Color.White
                )
            }
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(12.dp)
        ) {
            SetChatInfo(viewModel)
            Spacer(modifier = Modifier.height(25.dp))
            ListOfMembers(viewModel.includedUsers)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SetChatInfo(viewModel: AddChatViewModel) {

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            viewModel.chatUri = it
        }

    Row {
        Surface(
            onClick = { launcher.launch("image/*") },
            shape = CircleShape,
            color = Color(0xff5291ff),
            modifier = Modifier.size(65.dp)
        ) {
            AsyncImage(
                model = if (viewModel.chatUri == null) R.drawable.ic_add_photo else viewModel.chatUri,
                contentDescription = "Chat photo",
                contentScale = ContentScale.Crop,
                modifier = if (viewModel.chatUri == null) Modifier.padding(7.dp) else Modifier,
                colorFilter = if (viewModel.chatUri == null) ColorFilter.tint(Color.White) else null
            )
        }

        TextField(
            value = viewModel.chatName,
            onValueChange = { viewModel.chatName = it },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Enter group name",
                    color = Color.Unspecified.copy(alpha = 0.5f)
                )
            },
        )
    }
}

@Composable
private fun ListOfMembers(members: List<UserInfo>) {
    LazyColumn() {
        items(items = members) { member ->
            Text(member.username)
        }
    }
}

@Composable
fun createLoadingAlertDialog(title: String) {
    AlertDialog(
        modifier = Modifier.width(200.dp),
        onDismissRequest = {},
        title = { Text(title) },
        text = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
               },
        buttons = {}
    )
}