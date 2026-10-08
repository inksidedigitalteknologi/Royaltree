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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.CoralRed
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.ui.theme.ElectricBlue
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.inkside.digital.ui.components.AnimatedBackground
import com.inkside.digital.ui.components.PasswordValidator
import kotlinx.coroutines.launch
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.components.LanguageSelectionModal

@Composable
fun RegisterScreen(
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onRegisterEmail: (email: String, password: String, name: String) -> Unit,
    onRegisterGoogle: (idToken: String) -> Unit,
    onNavigateLogin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var googleLoading by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    var showLanguageModal by remember { mutableStateOf(false) }

    val validation = PasswordValidator.validate(password)

    LaunchedEffect(Unit) { contentVisible = true }

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
                    modifier = Modifier.clickable { showLanguageModal = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(currentLanguage.flag, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentLanguage.code,
                            fontSize = 12.sp,
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

                // Logo
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { -50 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Royaltree",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.White, Color(0xFFFFD700))
                                )
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = LanguageManager.translate("register_subtitle", currentLanguage, "Daftar Akun Baru"),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

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
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                            Text(LanguageManager.translate("register_create", currentLanguage, "Buat Akun"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(LanguageManager.translate("register_tagline", currentLanguage, "Gratis, mulai hasilkan komisi"), fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))

                            Spacer(modifier = Modifier.height(20.dp))

                            // Nama
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text(LanguageManager.translate("register_name", currentLanguage, "Nama Lengkap"), color = Color.White.copy(alpha = 0.7f)) },
                                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color.White.copy(alpha = 0.8f)) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = outlinedColors(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Email
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text(LanguageManager.translate("register_email", currentLanguage, "Email"), color = Color.White.copy(alpha = 0.7f)) },
                                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.White.copy(alpha = 0.8f)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = outlinedColors(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Password
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text(LanguageManager.translate("register_password", currentLanguage, "Password"), color = Color.White.copy(alpha = 0.7f)) },
                                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = 0.8f)) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle",
                                            tint = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = outlinedColors(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Strength Indicator
                            if (password.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                StrengthBar(strength = validation.strength, label = validation.strengthLabel)
                                Spacer(modifier = Modifier.height(8.dp))
                                PasswordChecklist(validation = validation)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Error / Success
                            if (errorMessage != null) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().background(CoralRed.copy(alpha = 0.2f), RoundedCornerShape(10.dp)).padding(12.dp)
                                ) {
                                    Text(errorMessage, color = Color(0xFFFECACA), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (successMessage != null) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().background(EmeraldLight.copy(alpha = 0.2f), RoundedCornerShape(10.dp)).padding(12.dp)
                                ) {
                                    Text(successMessage, color = Color(0xFFA7F3D0), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Tombol Daftar
                            if (isLoading) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = { onRegisterEmail(email.trim(), password, name.trim()) },
                                    enabled = name.isNotBlank() && email.isNotBlank() && validation.isValid,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = ElectricBlue,
                                        disabledContainerColor = Color.White.copy(alpha = 0.3f),
                                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.fillMaxWidth().height(52.dp)
                                ) {
                                    Text(LanguageManager.translate("register_button", currentLanguage, "Daftar"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Divider
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.3f)))
                                Text("  atau  ", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.3f)))
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Tombol Google
                            Button(
                                onClick = {
                                    scope.launch {
                                        googleLoading = true
                                        try {
                                            val credentialManager = CredentialManager.create(context)
                                            val googleIdOption = GetGoogleIdOption.Builder()
                                                .setFilterByAuthorizedAccounts(false)
                                                .setServerClientId("969563519125-ff0itejq3fadi95eqno5205grc933bfr.apps.googleusercontent.com")
                                                .setAutoSelectEnabled(false)
                                                .build()
                                            val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
                                            val result = credentialManager.getCredential(context = context, request = request)
                                            val credential = result.credential
                                            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                                val g = GoogleIdTokenCredential.createFrom(credential.data)
                                                onRegisterGoogle(g.idToken)
                                            }
                                        } catch (e: Exception) {
                                            Log.e("RegisterScreen", "Google error: \${e.message}")
                                        } finally {
                                            googleLoading = false
                                        }
                                    }
                                },
                                enabled = !googleLoading,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            ) {
                                Text("G", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(LanguageManager.translate("register_google", currentLanguage, "Daftar dengan Google"), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                Text(LanguageManager.translate("register_have_account", currentLanguage, "Sudah punya akun? "), fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                                Text(LanguageManager.translate("register_login", currentLanguage, "Masuk"), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.clickable { onNavigateLogin() })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }

            // ===== LANGUAGE MODAL =====
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
        }
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
    cursorColor = Color.White
)

@Composable
private fun StrengthBar(strength: Int, label: String) {
    val (color, _) = when (strength) {
        1 -> CoralRed to 0.25f
        2 -> Color(0xFFF59E0B) to 0.5f
        3 -> EmeraldLight to 0.75f
        4 -> Color(0xFF059669) to 1f
        else -> Color(0xFF6B7280) to 0f
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Kekuatan Password", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(3.dp))) {
            Box(modifier = Modifier.fillMaxWidth(strength / 4f).height(6.dp).background(color, RoundedCornerShape(3.dp)))
        }
    }
}

@Composable
private fun PasswordChecklist(validation: PasswordValidator.ValidationResult) {
    val items = listOf(
        Triple(validation.minLength, "Minimal 8 karakter", true),
        Triple(validation.hasUppercase, "Huruf besar (A-Z)", true),
        Triple(validation.hasLowercase, "Huruf kecil (a-z)", true),
        Triple(validation.hasDigit, "Angka (0-9)", true),
        Triple(validation.hasSymbol, "Simbol (!@#\$%) — opsional", false)
    )

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items.forEach { (ok, text, required) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (ok) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = when {
                        ok -> EmeraldLight
                        required -> Color.White.copy(alpha = 0.5f)
                        else -> Color.White.copy(alpha = 0.3f)
                    },
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = text,
                    fontSize = 10.sp,
                    color = if (ok) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
