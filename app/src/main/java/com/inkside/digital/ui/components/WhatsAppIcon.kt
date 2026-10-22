package com.inkside.digital.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val WhatsAppLogo: ImageVector by lazy {
    ImageVector.Builder(
        name = "WhatsApp",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFFFFFFF))) {
            moveTo(17.472f, 14.382f)
            curveTo(17.190f, 14.238f, 15.805f, 13.562f, 15.546f, 13.464f)
            curveTo(15.287f, 13.366f, 15.098f, 13.318f, 14.910f, 13.604f)
            curveTo(14.721f, 13.890f, 14.180f, 14.522f, 14.016f, 14.710f)
            curveTo(13.852f, 14.899f, 13.686f, 14.923f, 13.402f, 14.779f)
            curveTo(13.117f, 14.634f, 12.211f, 14.339f, 11.137f, 13.380f)
            curveTo(10.300f, 12.633f, 9.738f, 11.711f, 9.574f, 11.424f)
            curveTo(9.411f, 11.137f, 9.561f, 10.985f, 9.706f, 10.841f)
            curveTo(9.835f, 10.712f, 9.993f, 10.504f, 10.137f, 10.341f)
            curveTo(10.280f, 10.178f, 10.327f, 10.060f, 10.423f, 9.872f)
            curveTo(10.519f, 9.685f, 10.471f, 9.520f, 10.399f, 9.376f)
            curveTo(10.327f, 9.232f, 9.766f, 7.823f, 9.530f, 7.249f)
            curveTo(9.299f, 6.690f, 9.064f, 6.766f, 8.887f, 6.757f)
            curveTo(8.723f, 6.749f, 8.534f, 6.747f, 8.346f, 6.747f)
            curveTo(8.158f, 6.747f, 7.852f, 6.818f, 7.593f, 7.106f)
            curveTo(7.335f, 7.393f, 6.606f, 8.074f, 6.606f, 9.457f)
            curveTo(6.606f, 10.840f, 7.617f, 12.178f, 7.760f, 12.368f)
            curveTo(7.902f, 12.556f, 9.756f, 15.431f, 12.594f, 16.655f)
            curveTo(13.270f, 16.945f, 13.797f, 17.118f, 14.209f, 17.247f)
            curveTo(14.887f, 17.463f, 15.505f, 17.432f, 15.993f, 17.361f)
            curveTo(16.539f, 17.281f, 17.641f, 16.679f, 17.878f, 16.015f)
            curveTo(18.115f, 15.352f, 18.115f, 14.787f, 18.043f, 14.669f)
            curveTo(17.971f, 14.551f, 17.783f, 14.522f, 17.472f, 14.382f)
            close()
            moveTo(12.012f, 0.000f)
            curveTo(5.378f, 0.000f, 0.000f, 5.379f, 0.000f, 12.013f)
            curveTo(0.000f, 14.166f, 0.567f, 16.204f, 1.563f, 17.976f)
            lineTo(0.094f, 22.357f)
            lineTo(4.605f, 20.910f)
            curveTo(6.317f, 21.833f, 8.284f, 22.374f, 10.373f, 22.374f)
            curveTo(17.007f, 22.374f, 22.386f, 16.995f, 22.386f, 10.361f)
            curveTo(22.386f, 4.727f, 17.647f, 0.000f, 12.012f, 0.000f)
            close()
            moveTo(12.012f, 20.478f)
            curveTo(10.103f, 20.478f, 8.331f, 19.917f, 6.851f, 18.940f)
            lineTo(6.583f, 18.768f)
            lineTo(3.951f, 19.651f)
            lineTo(4.855f, 17.079f)
            lineTo(4.671f, 16.798f)
            curveTo(3.593f, 15.258f, 2.976f, 13.400f, 2.976f, 11.400f)
            curveTo(2.976f, 5.964f, 7.401f, 1.539f, 12.837f, 1.539f)
            curveTo(18.272f, 1.539f, 22.697f, 5.964f, 22.697f, 11.400f)
            curveTo(22.697f, 17.172f, 17.784f, 20.478f, 12.012f, 20.478f)
            close()
        }
    }.build()
}

@Composable
fun WhatsAppIcon(size: Int = 24, tint: Color = Color.White) {
    Icon(
        imageVector = WhatsAppLogo,
        contentDescription = "WhatsApp",
        modifier = Modifier.size(size.dp),
        tint = tint
    )
}
