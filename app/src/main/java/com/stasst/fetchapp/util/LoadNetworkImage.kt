package com.stasst.fetchapp.util

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest

@Composable
fun LoadNetworkImage(imageUrl: String, onClick: () -> Unit) {

    val painter = rememberImagePainter(
        data = imageUrl,
        builder = {
            crossfade(true)
        }
    )

    BoxWithConstraints(
        modifier = Modifier.aspectRatio(1000f / 1412f)
    ) {
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
            scale = (scale * zoomChange).coerceIn(1f, 4f)

            val extraWidth = (scale - 1) * constraints.maxWidth
            val extraHeight = (scale - 1) * constraints.maxHeight

            val maxX = extraWidth / 2
            val maxY = extraHeight / 2

            offset = Offset(
                x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY)
            )
        }
        Log.d("painterImg", imageUrl)
        Image(
            modifier = Modifier
                .fillMaxWidth()

                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            val zoomFactor = 1.55f
                            val newScale = if (scale > 1f) 1f else zoomFactor

                            val offsetX = 0.5f * constraints.maxWidth * (1 - newScale)

                            scale = newScale
                            offset = Offset(offsetX, 0f)
                        },
                        onTap = {
                            if (scale > 1f) {
                                val offsetX = 0.5f * constraints.maxWidth * (1 - scale)
                                offset = Offset(offsetX, 0f)
                            }
                            onClick()
                        }
                    )
                }

                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }.transformable(state),
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
    }
}