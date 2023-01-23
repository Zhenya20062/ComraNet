package com.euzhene.comranet.allChats.pres

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.euzhene.comranet.R

@Composable
fun SideMenu(
    onSettingsClick: () -> Unit,
) {
    Column() {
        SideMenuOption(iconId = R.drawable.ic_news, title = "News", onClick = {})
        SideMenuOption(iconId = R.drawable.ic_settings, title = "Settings", onClick = onSettingsClick)
        SideMenuOption(iconId = R.drawable.ic_person, title = "Sign out", onClick = {})
        SideMenuOption(iconId = R.drawable.ic_info, title = "Info", onClick = {})
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SideMenuOption(
    iconId: Int,
    title: String,
    onClick: () -> Unit,
) {
    Surface(onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {

            Image(
                painter = painterResource(id = iconId),
                contentDescription = "settings",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(30.dp),
            )
            Text(title, fontSize = 25.sp)
        }
    }

}