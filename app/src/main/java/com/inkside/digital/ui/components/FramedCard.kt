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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * FramedCard — Card dengan PNG frame overlay.
 *
 * Auto-detect aspect ratio dari PNG intrinsic size.
 * Content padding guna percent (default 14%) supaya konsisten.
 *
 * @param frameRes PNG frame dari res/drawable
 * @param paddingPercent padding content sebagai percentage (0.14 = 14%)
 * @param aspectRatioOverride override aspect ratio (kalau PNG tak standard)
 * @param contentPadding backward compat (akan override paddingPercent kalau di-set)
 */
@Composable
fun FramedCard(
    frameRes: Int,
    modifier: Modifier = Modifier,
    paddingPercent: Float = 0.14f,
    aspectRatioOverride: Float? = null,
    contentPadding: Dp? = null,
    contentPaddingHorizontal: Dp? = null,
    contentPaddingVertical: Dp? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val painter = painterResource(frameRes)

    // Auto-detect aspect ratio dari PNG
    val intrinsicSize = painter.intrinsicSize
    val detectedAspectRatio = if (intrinsicSize.width > 0f && intrinsicSize.height > 0f) {
        intrinsicSize.width / intrinsicSize.height
    } else {
        1.2f  // fallback
    }
    val aspectRatio = aspectRatioOverride ?: detectedAspectRatio

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        contentAlignment = Alignment.Center
    ) {
        // Hitung padding: percent default, atau custom Dp kalau di-set
        val horizontalPadding = contentPaddingHorizontal
            ?: contentPadding
            ?: (maxWidth * paddingPercent)
        val verticalPadding = contentPaddingVertical
            ?: contentPadding
            ?: (maxHeight * paddingPercent)

        // Content overlay (BELAKANG frame)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                ),
            contentAlignment = Alignment.Center,
            content = content
        )

        // Frame PNG overlay (ATAS content)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}
