package com.inkside.digital.ui.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * AppDesign: Design System Royaltree.
 *
 * Semua spacing, radius, dan ukuran font standar.
 * Pakai ini biar konsisten di semua screen.
 */
object AppDesign {

    // ============ SPACING ============
    val screenPadding = 16.dp
    val cardSpacing = 12.dp
    val cardPadding = 16.dp
    val itemSpacing = 8.dp
    val sectionSpacing = 24.dp

    // ============ RADIUS ============
    val cardRadius = RoundedCornerShape(16.dp)
    val buttonRadius = RoundedCornerShape(12.dp)
    val inputRadius = RoundedCornerShape(12.dp)
    val chipRadius = RoundedCornerShape(8.dp)
    val dialogRadius = RoundedCornerShape(20.dp)

    // ============ FONT SIZE ============
    val titleSize = 20.sp
    val subtitleSize = 14.sp
    val bodySize = 13.sp
    val captionSize = 11.sp
    val tinySize = 10.sp

    // ============ ICON SIZE ============
    val iconSmall = 16.dp
    val iconMedium = 20.dp
    val iconLarge = 24.dp
    val iconXLarge = 32.dp
}
