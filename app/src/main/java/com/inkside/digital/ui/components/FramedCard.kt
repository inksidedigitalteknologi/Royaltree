package com.inkside.digital.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
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
 * Guna universal di mana-mana screen.
 *
 * @param frameRes PNG frame dari res/drawable
 * @param aspectRatio Aspect ratio frame (default 1.2f, sesuaikan dgn PNG)
 * @param contentPaddingHorizontal padding horizontal content
 * @param contentPaddingVertical padding vertical content
 * @param content content di dalam frame
 */
@Composable
fun FramedCard(
    frameRes: Int,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1.2f,
    contentPadding: Dp = 32.dp,
    contentPaddingHorizontal: Dp = contentPadding,
    contentPaddingVertical: Dp = contentPadding,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        contentAlignment = Alignment.Center
    ) {
        // Content overlay dgn padding
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = contentPaddingHorizontal,
                    vertical = contentPaddingVertical
                ),
            contentAlignment = Alignment.Center,
            content = content
        )

        // Frame PNG — fit bounds
        Image(
            painter = painterResource(frameRes),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}
