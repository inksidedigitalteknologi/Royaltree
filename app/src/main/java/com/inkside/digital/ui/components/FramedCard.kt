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
 * FramedCard — Card dengan PNG frame overlay.
 *
 * Auto-detect aspect ratio dari PNG intrinsic size.
 * Content padding guna percent (default 22%) supaya konsisten.
 *
 * @param frameRes PNG frame dari res/drawable
 * @param paddingPercent padding content percentage (0.18 = 18%)
 * @param paddingPercentHorizontal override horizontal
 * @param paddingPercentVertical override vertical
 * @param aspectRatioOverride override aspect ratio
 * @param contentPadding backward compat (Dp)
 */
@Composable
fun FramedCard(
    frameRes: Int,
    modifier: Modifier = Modifier,
    paddingPercent: Float = 0.22f,  // default lebih aman untuk frame dengan border tebal
    paddingPercentHorizontal: Float? = null,
    paddingPercentVertical: Float? = null,
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
    } else 1.2f
    val aspectRatio = aspectRatioOverride ?: detectedAspectRatio

    val horizontalPct = paddingPercentHorizontal ?: paddingPercent
    val verticalPct = paddingPercentVertical ?: paddingPercent

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        contentAlignment = Alignment.Center
    ) {
        val horizontalPadding = contentPaddingHorizontal
            ?: contentPadding
            ?: (maxWidth * horizontalPct)

        val verticalPadding = contentPaddingVertical
            ?: contentPadding
            ?: (maxHeight * verticalPct)

        // 1. Frame PNG (background)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. Content (overlay atas frame)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
