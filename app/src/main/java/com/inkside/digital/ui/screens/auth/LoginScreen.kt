package com.inkside.digital.ui.screens.auth

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.BuildConfig
import com.inkside.digital.R
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.components.AnimatedBackground
import com.inkside.digital.ui.components.LanguageSelectionModal
import com.inkside.digital.ui.components.RoyaltreeTree
import com.inkside.digital.util.AppLogger
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
// Palette
// ─────────────────────────────────────────────────────────────
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldDeep = Color(0xFF059669)
private val GoldAccent = Color(0xFFFFD700)
private val GoldSoft = Color(0xFFFBBF24)
private val TextMuted = Color(0xFF94A3B8)
private val GlassBg = Color(0x14FFFFFF)

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    currentLanguage: AppLanguage,
    isReturningUser: Boolean = false,
    onLanguageChange: (AppLanguage) -> Unit,
    onLoginGoogle: (idToken: String, marketingChecked: Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var googleLoading by remember { mutableStateOf(false) }
    var termsChecked by remember { mutableStateOf(false) }
    var privacyChecked by remember { mutableStateOf(false) }
    var marketingChecked by remember { mutableStateOf(false) }
    
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    var showLanguageModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { contentVisible = true }

    // "Centang Semua" = terms + privacy saja (marketing tetap opsional)

    // Animasi masuk
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "contentAlpha"
    )

    AnimatedBackground(
        accentColor = EmeraldLight,
        enableAurora = true,
        enableStars = true,
        enableGlow = true,
        enableParticles = true
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Language selector — top-left
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { showLanguageModal = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(currentLanguage.flag, fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = currentLanguage.code,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Main content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .alpha(contentAlpha),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(40.dp))

                // Logo pohon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .aspectRatio(1f) // ✅ Kotak persegi, bukan tinggi paksa
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.95f)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        GoldAccent.copy(alpha = 0.18f),
                                        EmeraldLight.copy(alpha = 0.08f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                    RoyaltreeTree(
                        level = 5,
                        growthProgress = 1f,
                        modifier = Modifier.fillMaxSize(0.85f) // ✅ Proporsional
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ───────────────────────────────────────
                // Dynamic header: returning vs new user
                // ───────────────────────────────────────
                Text(
                    text = if (isReturningUser) {
                        LanguageManager.translate("login_welcome_back", currentLanguage, "Selamat Datang Kembali")
                    } else {
                        LanguageManager.translate("login_welcome_new", currentLanguage, "Selamat Datang di Royaltree")
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (isReturningUser) {
                        LanguageManager.translate("login_subtitle_returning", currentLanguage, "Lanjutkan perjalananmu")
                    } else {
                        LanguageManager.translate("login_subtitle_new", currentLanguage, "Mulai perjalananmu sekarang")
                    },
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // ───────────────────────────────────────
                // Glassmorphism card — semua checkbox
                // ───────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassBg)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Column {
                        val allChecked = termsChecked && privacyChecked && marketingChecked
    val onMasterChange = { checked: Boolean ->
        termsChecked = checked
        privacyChecked = checked
        marketingChecked = checked
    }
                        // MASTER checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onMasterChange(!allChecked) }
                                .clickable {
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            PremiumCheckbox(
                                checked = allChecked,
                                onCheckedChange = { onMasterChange(it) },
                                indeterminate = false,
                                size = 26.dp,
                                accentColor = GoldAccent
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = LanguageManager.translate("login_check_all", currentLanguage, "Centang Semua"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.08f))
                        )
                        Spacer(Modifier.height(12.dp))


                        // Terms + Privacy checkbox
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { termsChecked = !termsChecked; privacyChecked = !privacyChecked }
                                .padding(vertical = 4.dp)
                        ) {
                            PremiumCheckbox(
                                checked = termsChecked && privacyChecked,
                                onCheckedChange = { termsChecked = it; privacyChecked = it },
                                indeterminate = false,
                                size = 22.dp,
                                accentColor = EmeraldLight
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = LanguageManager.translate("login_agree_prefix", currentLanguage, "Saya menyetujui"),
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    lineHeight = 16.sp
                                )
                                Row {
                                    Text(
                                        text = LanguageManager.translate("login_terms_link", currentLanguage, "Syarat & Ketentuan"),
                                        fontSize = 12.sp,
                                        color = EmeraldLight,
                                        fontWeight = FontWeight.SemiBold,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable { showTermsDialog = true }
                                    )
                                    Text(
                                        text = "  ·  ",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = LanguageManager.translate("login_privacy_link", currentLanguage, "Kebijakan Privasi"),
                                        fontSize = 12.sp,
                                        color = EmeraldLight,
                                        fontWeight = FontWeight.SemiBold,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable { showPrivacyDialog = true }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Marketing checkbox (opsional)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { marketingChecked = !marketingChecked }
                                .padding(vertical = 4.dp)
                        ) {
                            PremiumCheckbox(
                                checked = marketingChecked,
                                onCheckedChange = { marketingChecked = it },
                                indeterminate = false,
                                size = 22.dp,
                                accentColor = EmeraldLight
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = LanguageManager.translate("login_marketing_short", currentLanguage, "Email promosi (opsional)"),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f),
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Error message
                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFFFCA5A5),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Google button
                Button(
                    onClick = {
                        if (!termsChecked || !privacyChecked) {
                            android.widget.Toast.makeText(
                                context,
                                LanguageManager.translate("login_must_agree", currentLanguage, "Centang Syarat & Ketentuan dulu"),
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        scope.launch {
                            googleLoading = true
                            try {
                                val credentialManager =
                                    androidx.credentials.CredentialManager.create(context)
                                val googleIdOption =
                                    com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId("969563519125-ff0itejq3fadi95eqno5205grc933bfr.apps.googleusercontent.com")
                                        .setAutoSelectEnabled(false)
                                        .build()
                                val request =
                                    androidx.credentials.GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()
                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request
                                )
                                val credential = result.credential
                                if (credential is androidx.credentials.CustomCredential &&
                                    credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential =
                                        com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)
                                    onLoginGoogle(googleIdTokenCredential.idToken, marketingChecked)
                                } else {
                                    AppLogger.e("LoginScreen", "Credential bukan Google ID Token")
                                }
                            } catch (e: Exception) {
                                AppLogger.e("LoginScreen", "Google Sign-In error: ${e.message}")
                            } finally {
                                googleLoading = false
                            }
                        }
                    },
                    enabled = !googleLoading && termsChecked && privacyChecked,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1F2937),
                        disabledContainerColor = Color.White.copy(alpha = 0.4f),
                        disabledContentColor = Color(0xFF1F2937).copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (googleLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF1F2937),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = LanguageManager.translate("login_loading", currentLanguage, "Memuat..."),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = LanguageManager.translate("login_google", currentLanguage, "Masuk dengan Google"),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Trust badge + versi + merek
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔒", fontSize = 12.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = LanguageManager.translate("login_trust_badge", currentLanguage, "Data Anda aman & terenkripsi"),
                        fontSize = 11.sp,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${LanguageManager.translate("login_version_brand", currentLanguage, "Versi")} ${BuildConfig.VERSION_NAME}",
                        fontSize = 10.sp,
                        color = TextMuted.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "  ·  ",
                        fontSize = 10.sp,
                        color = TextMuted.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${LanguageManager.translate("login_powered_by", currentLanguage, "Didukung oleh")} Inkside Digital",
                        fontSize = 10.sp,
                        color = TextMuted.copy(alpha = 0.7f)
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        // ─────────────────────────────────────────
        // Language modal
        // ─────────────────────────────────────────
        if (showLanguageModal) {
            LanguageSelectionModal(
                currentLanguage = currentLanguage,
                onSelect = { lang ->
                    onLanguageChange(lang)
                    showLanguageModal = false
                },
                onDismiss = { showLanguageModal = false }
            )
        }

        // ─────────────────────────────────────────
        // Terms dialog
        // ─────────────────────────────────────────
        if (showTermsDialog) {
            LegalDialog(
                title = LanguageManager.translate("login_terms_title", currentLanguage, "Syarat & Ketentuan"),
                filePrefix = "terms",
                currentLanguage = currentLanguage,
                onDismiss = { showTermsDialog = false }
            )
        }

        // ─────────────────────────────────────────
        // Privacy dialog
        // ─────────────────────────────────────────
        if (showPrivacyDialog) {
            LegalDialog(
                title = LanguageManager.translate("login_privacy_title", currentLanguage, "Kebijakan Privasi"),
                filePrefix = "privacy",
                currentLanguage = currentLanguage,
                onDismiss = { showPrivacyDialog = false }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// PremiumCheckbox — custom, animated, glow
// ─────────────────────────────────────────────────────────────
@Composable
private fun PremiumCheckbox(
    checked: Boolean,
    indeterminate: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    size: androidx.compose.ui.unit.Dp = 22.dp,
    accentColor: Color = EmeraldLight
) {
    // Scale bounce saat toggle
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.05f else 1f,
        animationSpec = tween(180),
        label = "checkboxScale"
    )

    // Glow pulse
    val infinite = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (checked) {
                    Brush.linearGradient(
                        colors = listOf(accentColor, accentColor.copy(alpha = 0.7f))
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.04f)
                        )
                    )
                }
            )
            .border(
                width = if (checked) 0.dp else 1.5.dp,
                color = if (checked) Color.Transparent
                else Color.White.copy(alpha = 0.4f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        // Glow shadow saat checked
        if (checked) {
            Box(
                modifier = Modifier
                    .size(size)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.4f * glowAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Checkmark icon
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = Color.White,
                modifier = Modifier.size(size * 0.65f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// LegalDialog — baca file markdown per bahasa
// ─────────────────────────────────────────────────────────────
@Composable
private fun LegalDialog(
    title: String,
    filePrefix: String,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val langCode = when (currentLanguage) {
        AppLanguage.INDONESIAN -> "id"
        AppLanguage.ENGLISH -> "en"
        AppLanguage.SPANISH -> "es"
        AppLanguage.CHINESE -> "zh"
        AppLanguage.ARABIC -> "ar"
        AppLanguage.JAPANESE -> "ja"
    }

    var legalContent by remember { mutableStateOf("Memuat...") }

    LaunchedEffect(langCode) {
        legalContent = try {
            context.assets.open("legal/${filePrefix}_${langCode}.md")
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            try {
                context.assets.open("legal/${filePrefix}_en.md")
                    .bufferedReader()
                    .use { it.readText() }
            } catch (e2: Exception) {
                "Failed to load: ${e2.message}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = legalContent,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = LanguageManager.translate("common_close", currentLanguage, "Tutup"),
                    color = EmeraldLight,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}


