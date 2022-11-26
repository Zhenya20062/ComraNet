package com.euzhene.comranet.autorization.presentation.screen

import android.net.Uri
import android.window.SplashScreen
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.euzhene.comranet.R
import com.euzhene.comranet.autorization.presentation.AuthViewModel
import com.euzhene.comranet.destinations.AllChatsScreenDestination
import com.euzhene.comranet.destinations.LoginScreenDestination
import com.euzhene.comranet.destinations.RegisterScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


@RootNavGraph(true)
@Composable
@Destination
fun RegisterScreen(
    navigator: DestinationsNavigator,
    viewModel: AuthViewModel,
) {


    if (viewModel.shouldGoToChatRoom.value) {
        LaunchedEffect(key1 = Unit) {
            navigator.navigate(AllChatsScreenDestination()) {
                popUpTo(RegisterScreenDestination) { inclusive = true }
            }
        }

    }
    if (viewModel.shouldShowDialog.value) {
        LoadingAlertDialog()
    }

    if (viewModel.isLoading.value) {
        SplashScreen()
        return
    }

    AuthScaffold(snackbarMessageFlow = viewModel.toastMessage) {
        AuthColumn {
            AuthTextName(title = "SIGN UP", modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(20.dp))
            InputForm(
                title = "Email",
                textFieldValue = viewModel.email,
                onValueChange = { viewModel.email = it })
            Spacer(modifier = Modifier.height(20.dp))
            InputForm(
                title = "Password",
                textFieldValue = viewModel.password,
                onValueChange = { viewModel.password = it },
                secured = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            InputForm(
                title = "Login",
                textFieldValue = viewModel.login,
                onValueChange = { viewModel.login = it },
            )
            Spacer(modifier = Modifier.height(20.dp))
            InputForm(
                title = "Username",
                textFieldValue = viewModel.username,
                onValueChange = { viewModel.username = it },
            )
            Spacer(modifier = Modifier.height(20.dp))
            Avatar(photo = viewModel.photo, onPhotoUpdate = { viewModel.photo = it })
            Spacer(modifier = Modifier.height(20.dp))
            ConfirmButton(title = "Sign up", onClick = { viewModel.registerUser() })
            Text(text = "OR", fontSize = 20.sp)
            ConfirmButton(
                title = "Sign in",
                onClick = {
                    navigator.navigate(LoginScreenDestination()) {
                        popUpTo(RegisterScreenDestination) { inclusive = true }
                    }
                }
            )
        }
    }

}

@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator, viewModel: AuthViewModel,
) {
    if (viewModel.shouldGoToChatRoom.value) {
        LaunchedEffect(key1 = Unit) {
            navigator.navigate(AllChatsScreenDestination()) {
                this.popUpTo(RegisterScreenDestination) { inclusive = true }
            }
        }

    }

    if (viewModel.shouldShowDialog.value) {
        LoadingAlertDialog()
    }
    AuthScaffold(snackbarMessageFlow = viewModel.toastMessage) {
        AuthColumn {
            AuthTextName(title = "SIGN IN", modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(20.dp))
            InputForm(
                title = "Email/login",
                textFieldValue = viewModel.emailOrLogin,
                onValueChange = { viewModel.emailOrLogin = it })
            Spacer(modifier = Modifier.height(20.dp))
            InputForm(
                title = "Password",
                textFieldValue = viewModel.password,
                onValueChange = { viewModel.password = it },
                secured = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            ConfirmButton(title = "Sign in", onClick = { viewModel.loginUser() })
            Text(text = "OR", fontSize = 20.sp)
            ConfirmButton(
                title = "Sign up",
                onClick = {
                    navigator.navigate(RegisterScreenDestination()) {
                        popUpTo(LoginScreenDestination) { inclusive = true }
                    }
                }
            )

        }
    }
}

@Composable
private fun LoadingAlertDialog() {
    Dialog(
        onDismissRequest = {},
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .background(White, shape = RoundedCornerShape(8.dp))
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun AuthScaffold(
    snackbarMessageFlow: SharedFlow<String>,
    content: @Composable () -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = snackbarMessageFlow) {
        snackbarMessageFlow.collectLatest {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }
    Scaffold(scaffoldState = scaffoldState) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Avatar(photo: Uri?, onPhotoUpdate: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            if (it != null)
                onPhotoUpdate(it)
        }
    )
    Surface(
        shape = CircleShape,
        color = Color.Blue.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.7f)),
        onClick = { launcher.launch("image/*") }
    ) {
        Box {
            AsyncImage(
                model = photo ?: LocalContext.current.getDrawable(R.drawable.ic_person),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(110.dp),
                contentScale = ContentScale.Crop
            )
            AsyncImage(
                model = LocalContext.current.getDrawable(R.drawable.ic_camera),
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.BottomCenter),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

    }
}

@Composable
private fun AuthTextName(title: String, modifier: Modifier) {
    Text(
        text = title,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
}

@Composable
private fun AuthColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .padding(vertical = 12.dp, horizontal = 20.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
private fun ConfirmButton(title: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(0.5f),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red.copy(alpha = 0.7f))
    ) {
        Text(text = title, color = Color.White, fontSize = 20.sp)
    }
}

@Composable
private fun InputForm(
    title: String,
    textFieldValue: String,
    onValueChange: (String) -> Unit,
    secured: Boolean = false
) {
    val visualTransformation = if (secured) passwordTransformation else VisualTransformation.None
    OutlinedTextField(
        value = textFieldValue, onValueChange = onValueChange,
        label = { Text(title) }, visualTransformation = visualTransformation
    )
}

private val passwordTransformation = VisualTransformation {
    var str = ""
    repeat(it.text.length) { str += '*' }
    TransformedText(
        AnnotatedString(str),
        OffsetMapping.Identity
    )
}