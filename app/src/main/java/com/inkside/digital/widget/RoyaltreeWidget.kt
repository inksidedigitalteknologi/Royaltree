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
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

// ==== Colors ====
private val DarkNavy = Color(0xFF0A0E1A)
private val CardSlate = Color(0xFF1E293B)
private val GoldAccent = Color(0xFFFFD700)
private val EmeraldLight = Color(0xFF10B981)
private val TextSecondary = Color(0xFF94A3B8)
private val ElectricBlue = Color(0xFF3B82F6)
private val PurpleAccent = Color(0xFF8B5CF6)

// ==== Keys ====
val KEY_USER_NAME = stringPreferencesKey("user_name")
val KEY_BALANCE = stringPreferencesKey("user_balance")
val KEY_POINTS = stringPreferencesKey("user_points")
val KEY_STREAK = stringPreferencesKey("user_streak")
val KEY_STEPS = stringPreferencesKey("user_steps")
val KEY_TIER = stringPreferencesKey("user_tier")

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
        val steps = prefs[KEY_STEPS] ?: "0"
        val tier = prefs[KEY_TIER] ?: "FREE"
        val isPremium = tier == "PREMIUM"

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(DarkNavy)
                .padding(14.dp),
            verticalAlignment = Alignment.Vertical.Top,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            // ==== Header: Royaltree + Tier Badge ====
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "Royaltree",
                    style = TextStyle(
                        color = ColorProvider(GoldAccent),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                Text(
                    text = if (isPremium) "PREMIUM" else "FREE",
                    style = TextStyle(
                        color = ColorProvider(if (isPremium) GoldAccent else TextSecondary),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(GlanceModifier.height(6.dp))

            // ==== Greeting ====
            Text(
                text = "Halo, $userName",
                style = TextStyle(
                    color = ColorProvider(TextSecondary),
                    fontSize = 11.sp
                )
            )

            Spacer(GlanceModifier.height(8.dp))

            // ==== Balance (Gold, besar) ====
            Text(
                text = "Saldo",
                style = TextStyle(
                    color = ColorProvider(TextSecondary),
                    fontSize = 9.sp
                )
            )
            Text(
                text = balance,
                style = TextStyle(
                    color = ColorProvider(GoldAccent),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(GlanceModifier.height(10.dp))

            // ==== Stats Row (3 kolom) ====
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                StatItem(
                    value = points,
                    label = "Poin",
                    color = EmeraldLight,
                    modifier = GlanceModifier.defaultWeight()
                )
                StatItem(
                    value = streak,
                    label = "Streak",
                    color = PurpleAccent,
                    modifier = GlanceModifier.defaultWeight()
                )
                StatItem(
                    value = steps,
                    label = "Langkah",
                    color = ElectricBlue,
                    modifier = GlanceModifier.defaultWeight()
                )
            }
        }
    }

    @Composable
    private fun StatItem(
        value: String,
        label: String,
        color: Color,
        modifier: GlanceModifier = GlanceModifier
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(
                text = value,
                style = TextStyle(
                    color = ColorProvider(color),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(TextSecondary),
                    fontSize = 9.sp
                )
            )
        }
    }
}

class RoyaltreeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoyaltreeWidget()
}
