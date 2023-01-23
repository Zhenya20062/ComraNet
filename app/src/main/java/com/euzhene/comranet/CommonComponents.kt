package com.euzhene.comranet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Button(
    modifier: Modifier,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.combinedClickable(
            onClick = onClick ?: {},
            onLongClick = onLongClick
        ),
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CircleImage(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
    imageVector: ImageVector,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    backgroundColor:Color = Color.Transparent,
    iconColor:Color = Color.White,
    innerPadding:Dp = 0.dp
) {
    Surface(onClick = onClick ?: {}, modifier = modifier, shape = CircleShape, color = backgroundColor) {
        Image(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentScale = contentScale,
            colorFilter = ColorFilter.tint(iconColor)
        )
    }
}





