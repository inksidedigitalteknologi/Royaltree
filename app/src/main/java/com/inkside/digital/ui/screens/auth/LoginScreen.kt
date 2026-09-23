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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.inkside.digital.ui.components.AnimatedGradientBackground
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onLoginEmail: (email: String, password: String) -> Unit,
    onLoginGoogle: (idToken: String) -> Unit,
    onNavigateRegister: () -> Unit,
    onNavigateForgotPassword: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var googleLoading by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { contentVisible = true }

    AnimatedGradientBackground {
        Box(modifier = Modifier.fillMaxSize()) {
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
                                    colors = listOf(Color.White, Color(0xFFE0E7FF))
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
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0xFF6366F1).copy(alpha = 0.5f),
                                spotColor = Color(0xFF8B5CF6).copy(alpha = 0.5f)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "Selamat Datang",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Masuk untuk melanjutkan",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Email
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email", color = Color.White.copy(alpha = 0.7f)) },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, null, tint = Color.White.copy(alpha = 0.8f))
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                                    cursorColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Password
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password", color = Color.White.copy(alpha = 0.7f)) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = 0.8f))
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password",
                                            tint = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                                    cursorColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Lupa Password
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Lupa Password?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.clickable { onNavigateForgotPassword() }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Error
                            if (errorMessage != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFFEF4444).copy(alpha = 0.2f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = errorMessage,
                                        color = Color(0xFFFECACA),
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Tombol Masuk
                            if (isLoading) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = { onLoginEmail(email.trim(), password) },
                                    enabled = email.isNotBlank() && password.isNotBlank(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF6366F1),
                                        disabledContainerColor = Color.White.copy(alpha = 0.3f),
                                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text("Masuk", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Divider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(Color.White.copy(alpha = 0.3f))
                                )
                                Text(
                                    text = "  atau  ",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(Color.White.copy(alpha = 0.3f))
                                )
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
                                                onLoginGoogle(googleIdTokenCredential.idToken)
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
                                enabled = !googleLoading,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text("G", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Masuk dengan Google", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Daftar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Belum punya akun? ",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Daftar",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.clickable { onNavigateRegister() }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
