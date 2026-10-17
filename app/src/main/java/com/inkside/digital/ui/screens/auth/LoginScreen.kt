package com.inkside.digital.ui.screens.auth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.ui.theme.CoralRed
import com.inkside.digital.ui.theme.GoldVip
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.inkside.digital.ui.components.AnimatedBackground
import kotlinx.coroutines.launch
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.components.LanguageSelectionModal
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Checkbox

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLoginGoogle: (idToken: String, marketingOptIn: Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var googleLoading by remember { mutableStateOf(false) }
    var agreementChecked by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var marketingOptIn by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    var showLanguageModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { contentVisible = true }

    // 🔍 DEBUG: Log bila currentLanguage berubah
    LaunchedEffect(currentLanguage) {
        android.util.Log.d("LoginScreen", "🌐 currentLanguage CHANGED: ${currentLanguage.code}")
    }

    // 🔍 DEBUG: Log bila showLanguageModal berubah
    LaunchedEffect(showLanguageModal) {
        android.util.Log.d("LoginScreen", "🌐 showLanguageModal = $showLanguageModal")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground {
            Box(modifier = Modifier.fillMaxSize()) {
            // ===== LANGUAGE TOGGLE (top-right) =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("language_toggle_button")
                        .clickable { showLanguageModal = true }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌐", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(currentLanguage.flag, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentLanguage.code,
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // ============ LOGO ============
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { -50 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Royaltree",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.White, Color(0xFF10B981))
                                )
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Affiliate Rewarding Platform",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // ============ GLASS CARD ============
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(tween(800, delayMillis = 200)) { 100 }
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.05f)  // ← SANGAT TRANSPARAN
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.15f)  // ← subtle border
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = LanguageManager.translate("login_welcome", currentLanguage, "Selamat Datang"),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = LanguageManager.translate("login_subtitle", currentLanguage, "Masuk untuk melanjutkan"),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Checkbox Terms & Conditions
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { agreementChecked = !agreementChecked }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = agreementChecked,
                                    onCheckedChange = { agreementChecked = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF34D399),
                                        uncheckedColor = Color.White.copy(alpha = 0.5f),
                                        checkmarkColor = Color.White
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = LanguageManager.translate(
                                            "login_agree_prefix",
                                            currentLanguage,
                                            "Saya menyetujui"
                                        ),
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                        lineHeight = 14.sp
                                    )
                                    Row {
                                        Text(
                                            text = LanguageManager.translate(
                                                "login_terms_link",
                                                currentLanguage,
                                                "Syarat & Ketentuan"
                                            ),
                                            fontSize = 11.sp,
                                            color = Color(0xFF34D399),
                                            fontWeight = FontWeight.SemiBold,
                                            textDecoration = TextDecoration.Underline,
                                            modifier = Modifier.clickable { showTermsDialog = true },
                                            lineHeight = 14.sp
                                        )
                                        Text(
                                            text = " & ",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.7f),
                                            lineHeight = 14.sp
                                        )
                                        Text(
                                            text = LanguageManager.translate(
                                                "login_privacy_link",
                                                currentLanguage,
                                                "Kebijakan Privasi"
                                            ),
                                            fontSize = 11.sp,
                                            color = Color(0xFF34D399),
                                            fontWeight = FontWeight.SemiBold,
                                            textDecoration = TextDecoration.Underline,
                                            modifier = Modifier.clickable { showPrivacyDialog = true },
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Checkbox Marketing (opsional)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { marketingOptIn = !marketingOptIn }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = marketingOptIn,
                                    onCheckedChange = { marketingOptIn = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF34D399),
                                        uncheckedColor = Color.White.copy(alpha = 0.5f),
                                        checkmarkColor = Color.White
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = LanguageManager.translate(
                                        "login_marketing_optin",
                                        currentLanguage,
                                        "Saya ingin menerima email promosi, update fitur, dan penawaran khusus"
                                    ),
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    lineHeight = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Tombol Google
                            Button(
                                onClick = {
                                    if (!agreementChecked) {
                                        android.widget.Toast.makeText(
                                            context,
                                            LanguageManager.translate(
                                                "login_must_agree",
                                                currentLanguage,
                                                "Centang Syarat & Ketentuan dulu"
                                            ),
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }
                                    scope.launch {
                                        googleLoading = true
                                        try {
                                            val credentialManager = CredentialManager.create(context)
                                            val googleIdOption = GetGoogleIdOption.Builder()
                                                .setFilterByAuthorizedAccounts(false)
                                                .setServerClientId("969563519125-ff0itejq3fadi95eqno5205grc933bfr.apps.googleusercontent.com")
                                                .setAutoSelectEnabled(false)
                                                .build()
                                            val request = GetCredentialRequest.Builder()
                                                .addCredentialOption(googleIdOption)
                                                .build()
                                            val result = credentialManager.getCredential(
                                                context = context,
                                                request = request
                                            )
                                            val credential = result.credential
                                            if (credential is CustomCredential &&
                                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                            ) {
                                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                                onLoginGoogle(googleIdTokenCredential.idToken, marketingOptIn)
                                            } else {
                                                Log.e("LoginScreen", "Credential bukan Google ID Token")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("LoginScreen", "Google Sign-In error: \${e.message}")
                                        } finally {
                                            googleLoading = false
                                        }
                                    }
                                },
                                enabled = !googleLoading && agreementChecked,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF1F2937),
                                    disabledContainerColor = Color.White.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                if (googleLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color(0xFF1F2937),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = LanguageManager.translate("login_loading", currentLanguage, "Memuat..."),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                } else {
                                    androidx.compose.foundation.Image(
                                        painter = androidx.compose.ui.res.painterResource(id = com.inkside.digital.R.drawable.ic_google_logo),
                                        contentDescription = "Google",
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = LanguageManager.translate("login_google", currentLanguage, "Masuk dengan Google"),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Footer: privasi (opsional, saat ini kosong)

                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }

            // Modal DIPINDAH ke LUAR AnimatedBackground
            }
        }

        // ===== LANGUAGE MODAL (LUAR AnimatedBackground) =====
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

        // ===== TERMS & CONDITIONS DIALOG (baca file per bahasa) =====
        if (showTermsDialog) {
            val langCode = when (currentLanguage) {
                AppLanguage.ID -> "id"
                AppLanguage.EN -> "en"
                AppLanguage.ES -> "es"
                AppLanguage.ZH -> "zh"
                AppLanguage.AR -> "ar"
                AppLanguage.JA -> "ja"
            }
            var legalContent by remember { mutableStateOf("Memuat...") }
            LaunchedEffect(langCode) {
                legalContent = try {
                    context.assets.open("legal/terms_${langCode}.md")
                        .bufferedReader()
                        .use { it.readText() }
                } catch (e: Exception) {
                    try {
                        context.assets.open("legal/terms_en.md")
                            .bufferedReader()
                            .use { it.readText() }
                    } catch (e2: Exception) {
                        "Failed to load Terms: ${e2.message}"
                    }
                }
            }

            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = {
                    Text(
                        text = LanguageManager.translate("login_terms_title", currentLanguage, "Syarat & Ketentuan"),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())
                    ) {
                        Text(
                            text = legalContent,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { showTermsDialog = false }
                    ) {
                        Text(
                            text = LanguageManager.translate("common_close", currentLanguage, "Tutup"),
                            color = Color(0xFF34D399),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        }

        // ===== PRIVACY POLICY DIALOG (baca file per bahasa) =====
        if (showPrivacyDialog) {
            val langCode = when (currentLanguage) {
                AppLanguage.ID -> "id"
                AppLanguage.EN -> "en"
                AppLanguage.ES -> "es"
                AppLanguage.ZH -> "zh"
                AppLanguage.AR -> "ar"
                AppLanguage.JA -> "ja"
            }
            var privacyContent by remember { mutableStateOf("Memuat...") }
            LaunchedEffect(langCode) {
                privacyContent = try {
                    context.assets.open("legal/privacy_${langCode}.md")
                        .bufferedReader()
                        .use { it.readText() }
                } catch (e: Exception) {
                    try {
                        context.assets.open("legal/privacy_en.md")
                            .bufferedReader()
                            .use { it.readText() }
                    } catch (e2: Exception) {
                        "Failed to load Privacy Policy: ${e2.message}"
                    }
                }
            }

            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = {
                    Text(
                        text = LanguageManager.translate("login_privacy_title", currentLanguage, "Kebijakan Privasi"),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())
                    ) {
                        Text(
                            text = privacyContent,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { showPrivacyDialog = false }
                    ) {
                        Text(
                            text = LanguageManager.translate("common_close", currentLanguage, "Tutup"),
                            color = Color(0xFF34D399),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        }
    }
}

