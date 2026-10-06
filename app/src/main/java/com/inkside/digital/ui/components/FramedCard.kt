package com.inkside.digital.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * FramedCard — Card dengan PNG frame overlay (content di ATAS frame).
 *
 * Frame PNG = background border. Content = overlay di atas.
 * Auto-detect aspect ratio dari PNG intrinsic size.
 *
 * @param frameRes PNG frame dari res/drawable
 * @param paddingPercent padding content percentage (0.12 = 12%)
 * @param aspectRatioOverride override aspect ratio (kalau perlu)
 * @param contentPadding backward compat (override paddingPercent)
 * @param contentPaddingHorizontal / contentPaddingVertical
 */
@Composable
fun FramedCard(
    frameRes: Int,
    modifier: Modifier = Modifier,
    paddingPercent: Float = 0.12f,
    aspectRatioOverride: Float? = null,
    contentPadding: Dp? = null,
    contentPaddingHorizontal: Dp? = null,
    contentPaddingVertical: Dp? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val painter = painterResource(frameRes)

    val intrinsicSize = painter.intrinsicSize
    val detectedAspectRatio = if (intrinsicSize.width > 0f && intrinsicSize.height > 0f) {
        intrinsicSize.width / intrinsicSize.height
    } else {
        1.2f
    }
    val aspectRatio = aspectRatioOverride ?: detectedAspectRatio

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth().aspectRatio(aspectRatio),
        contentAlignment = Alignment.Center
    ) {
        val horizontalPadding = contentPaddingHorizontal
            ?: contentPadding
            ?: (maxWidth * paddingPercent)
        val verticalPadding = contentPaddingVertical
            ?: contentPadding
            ?: (maxHeight * paddingPercent)

        // 1. Frame PNG (background)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. Content (overlay ATAS frame)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
