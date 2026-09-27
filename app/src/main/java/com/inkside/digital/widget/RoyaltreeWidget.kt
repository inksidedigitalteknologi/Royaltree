package com.inkside.digital.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.appwidget.updateAll
import androidx.glance.appwidget.GlanceAppWidgetReceiver

// ==== Colors ====
private val DarkNavy = Color(0xFF0A0E1A)
private val GoldAccent = Color(0xFFFFD700)
private val EmeraldLight = Color(0xFF10B981)
private val TextSecondary = Color(0xFF94A3B8)
private val ElectricBlue = Color(0xFF3B82F6)

// ==== Keys ====
val KEY_USER_NAME = stringPreferencesKey("user_name")
val KEY_BALANCE = stringPreferencesKey("user_balance")
val KEY_POINTS = stringPreferencesKey("user_points")
val KEY_STREAK = stringPreferencesKey("user_streak")

class RoyaltreeWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
        val userName = prefs[KEY_USER_NAME] ?: "User"
        val balance = prefs[KEY_BALANCE] ?: "Rp 0"
        val points = prefs[KEY_POINTS] ?: "0"
        val streak = prefs[KEY_STREAK] ?: "0"

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(DarkNavy)
                .padding(12.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Royaltree",
                style = TextStyle(
                    color = ColorProvider(GoldAccent),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = "Halo, $userName",
                style = TextStyle(
                    color = ColorProvider(TextSecondary),
                    fontSize = 10.sp
                )
            )

            Spacer(GlanceModifier.height(8.dp))

            // Balance
            Text(
                text = balance,
                style = TextStyle(
                    color = ColorProvider(GoldAccent),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(GlanceModifier.height(8.dp))

            // Stats Row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Text(
                        text = points,
                        style = TextStyle(
                            color = ColorProvider(EmeraldLight),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Poin",
                        style = TextStyle(
                            color = ColorProvider(TextSecondary),
                            fontSize = 9.sp
                        )
                    )
                }
                Spacer(GlanceModifier.width(20.dp))
                Column(
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Text(
                        text = streak,
                        style = TextStyle(
                            color = ColorProvider(ElectricBlue),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Streak",
                        style = TextStyle(
                            color = ColorProvider(TextSecondary),
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }
    }
}

class RoyaltreeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoyaltreeWidget()
}
