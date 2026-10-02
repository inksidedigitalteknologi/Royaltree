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

// ==== Colors (Royaltree theme) ====
private val DarkBg = Color(0xFF0A0E1A)
private val DarkBg2 = Color(0xFF0F172A)
private val CardSurface = Color(0xFF1E293B)
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldMid = Color(0xFF10B981)
private val GoldAccent = Color(0xFFFFD700)
private val CyanAccent = Color(0xFF06B6D4)
private val PinkAccent = Color(0xFFEC4899)
private val TextSecondary = Color(0xFF94A3B8)
private val TextMuted = Color(0xFF64748B)

// ==== Keys ====
val KEY_USER_NAME = stringPreferencesKey("user_name")
val KEY_BALANCE = stringPreferencesKey("user_balance")
val KEY_POINTS = stringPreferencesKey("user_points")
val KEY_STREAK = stringPreferencesKey("user_streak")
val KEY_STEPS = stringPreferencesKey("user_steps")
val KEY_TIER = stringPreferencesKey("user_tier")
val KEY_TOKEN = stringPreferencesKey("user_token")
val KEY_REFERRAL = stringPreferencesKey("user_referral")

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
        val token = prefs[KEY_TOKEN] ?: "0"
        val referral = prefs[KEY_REFERRAL] ?: "0"
        val tier = prefs[KEY_TIER] ?: "FREE"

        val (tierEmoji, tierColor) = when (tier.uppercase()) {
            "ROYAL" -> "💎" to Color(0xFFB9F2FF)
            "VIP" -> "🥇" to GoldAccent
            "PREMIUM" -> "🥈" to Color(0xFFC0C0C0)
            "STARTER" -> "🥉" to Color(0xFFCD7F32)
            else -> "🌱" to EmeraldLight
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(12.dp),
            verticalAlignment = Alignment.Vertical.Top,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            // ============ HEADER ============
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                // Logo emoji
                Text(
                    text = "🌳",
                    style = TextStyle(fontSize = 18.sp)
                )
                Spacer(GlanceModifier.width(6.dp))
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = "Royaltree",
                        style = TextStyle(
                            color = ColorProvider(GoldAccent),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$tierEmoji $tier",
                        style = TextStyle(
                            color = ColorProvider(tierColor),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                // User initial
                Text(
                    text = userName.take(1).uppercase(),
                    style = TextStyle(
                        color = ColorProvider(EmeraldLight),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(GlanceModifier.height(8.dp))

            // ============ BALANCE CARD ============
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(CardSurface)
                    .padding(10.dp)
            ) {
                Text(
                    text = "💰 Saldo",
                    style = TextStyle(
                        color = ColorProvider(TextSecondary),
                        fontSize = 8.sp
                    )
                )
                Spacer(GlanceModifier.height(2.dp))
                Text(
                    text = balance,
                    style = TextStyle(
                        color = ColorProvider(GoldAccent),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(GlanceModifier.height(6.dp))

            // ============ 2×2 STATS GRID ============
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                StatItem(
                    emoji = "⭐",
                    value = points,
                    label = "Poin",
                    color = EmeraldLight,
                    modifier = GlanceModifier.defaultWeight()
                )
                Spacer(GlanceModifier.width(4.dp))
                StatItem(
                    emoji = "🔥",
                    value = streak,
                    label = "Streak",
                    color = PinkAccent,
                    modifier = GlanceModifier.defaultWeight()
                )
            }
            Spacer(GlanceModifier.height(4.dp))
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                StatItem(
                    emoji = "⛏️",
                    value = token,
                    label = "Token",
                    color = CyanAccent,
                    modifier = GlanceModifier.defaultWeight()
                )
                Spacer(GlanceModifier.width(4.dp))
                StatItem(
                    emoji = "👥",
                    value = referral,
                    label = "Teman",
                    color = EmeraldLight,
                    modifier = GlanceModifier.defaultWeight()
                )
            }

            Spacer(GlanceModifier.height(6.dp))

            // ============ ACTION ROW ============
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                ActionChip(
                    emoji = "🎁",
                    label = "Check-in",
                    color = GoldAccent,
                    modifier = GlanceModifier.defaultWeight()
                )
                Spacer(GlanceModifier.width(4.dp))
                ActionChip(
                    emoji = "⛏️",
                    label = "Mining",
                    color = EmeraldLight,
                    modifier = GlanceModifier.defaultWeight()
                )
            }
        }
    }

    @Composable
    private fun StatItem(
        emoji: String,
        value: String,
        label: String,
        color: Color,
        modifier: GlanceModifier = GlanceModifier
    ) {
        Row(
            modifier = modifier
                .background(CardSurface)
                .padding(8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = emoji,
                style = TextStyle(fontSize = 12.sp)
            )
            Spacer(GlanceModifier.width(4.dp))
            Column {
                Text(
                    text = value,
                    style = TextStyle(
                        color = ColorProvider(color),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = label,
                    style = TextStyle(
                        color = ColorProvider(TextMuted),
                        fontSize = 8.sp
                    )
                )
            }
        }
    }

    @Composable
    private fun ActionChip(
        emoji: String,
        label: String,
        color: Color,
        modifier: GlanceModifier = GlanceModifier
    ) {
        Row(
            modifier = modifier
                .background(color.copy(alpha = 0.15f))
                .padding(vertical = 6.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(
                text = emoji,
                style = TextStyle(fontSize = 10.sp)
            )
            Spacer(GlanceModifier.width(4.dp))
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(color),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

class RoyaltreeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoyaltreeWidget()
}
