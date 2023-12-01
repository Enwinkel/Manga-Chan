package com.stasst.fetchapp.util

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.delay

@Composable
fun FrameAnimation(
    modifier: Modifier = Modifier,
    frames: List<Painter>,
    frameDurationMillis: Int = 50
) {
    var currentFrame by remember { mutableStateOf(0) }

    LaunchedEffect(currentFrame) {
        while (true) {
            delay(frameDurationMillis.toLong())
            currentFrame = (currentFrame + 1) % frames.size
        }
    }

    Image(
        modifier = modifier,
        painter = frames[currentFrame],
        contentDescription = null // Установите ваше описание здесь
    )
}