package com.inkside.digital.ui.screens.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.components.AnimatedGradientBackground

@Composable
fun ForgotPasswordScreen(
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onSendReset: (email: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
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

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { -50 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Royaltree",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                brush = Brush.linearGradient(colors = listOf(Color.White, Color(0xFFE0E7FF)))
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Lupa Password", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f), letterSpacing = 1.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(tween(800, delayMillis = 200)) { 100 }
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(24.dp, RoundedCornerShape(24.dp), ambientColor = Color(0xFF6366F1).copy(alpha = 0.5f), spotColor = Color(0xFF8B5CF6).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                            // Tombol back
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.align(Alignment.Start)
                            ) {
                                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Reset Password", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Masukkan email yang terdaftar. Kami akan mengirim link untuk reset password.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email", color = Color.White.copy(alpha = 0.7f)) },
                                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.White.copy(alpha = 0.8f)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
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

                            Spacer(modifier = Modifier.height(16.dp))

                            if (errorMessage != null) {
                                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFEF4444).copy(alpha = 0.2f), RoundedCornerShape(10.dp)).padding(12.dp)) {
                                    Text(errorMessage, color = Color(0xFFFECACA), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (successMessage != null) {
                                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(10.dp)).padding(12.dp)) {
                                    Text(successMessage, color = Color(0xFFA7F3D0), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (isLoading) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = { onSendReset(email.trim()) },
                                    enabled = email.isNotBlank(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF6366F1),
                                        disabledContainerColor = Color.White.copy(alpha = 0.3f),
                                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.fillMaxWidth().height(52.dp)
                                ) {
                                    Text("Kirim Link Reset", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                Text("Ingat password? ", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("Masuk", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.clickable { onNavigateBack() })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
