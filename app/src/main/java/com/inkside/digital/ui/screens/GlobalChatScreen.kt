package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.viewmodel.ChatMessage
import com.inkside.digital.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==== DESIGN SYSTEM ====
private val SurfaceDark = Color(0xFF0F172A)
private val SurfaceDeep = Color(0xFF020617)
private val EmeraldPrimary = Color(0xFF10B981)
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldDeep = Color(0xFF064E3B)
private val GoldVip = Color(0xFFFBBF24)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFCBD5E1)
private val TextMuted = Color(0xFF64748B)

@Composable
fun GlobalChatScreen(
    currentUserId: String,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    chatViewModel: ChatViewModel = viewModel()
) {
    val messages by chatViewModel.messages.collectAsStateWithLifecycle()
    val isLoading by chatViewModel.isLoading.collectAsStateWithLifecycle()
    val isSending by chatViewModel.isSending.collectAsStateWithLifecycle()
    val errorMessage by chatViewModel.errorMessage.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Set current user + start polling
    LaunchedEffect(currentUserId) {
        chatViewModel.setCurrentUser(currentUserId)
        chatViewModel.startPolling()
    }

    // Auto-scroll ke bawah saat ada pesan baru
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SurfaceDeep, SurfaceDark, SurfaceDeep)
                )
            )
            .imePadding()
    ) {
        // ==== HEADER ====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = LanguageManager.translate("chat_title", currentLanguage, "Global Chat"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = LanguageManager.translate("chat_subtitle", currentLanguage, "50 pesan terakhir"),
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            // Live indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(EmeraldLight)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        // ==== MESSAGES LIST ====
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading && messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EmeraldPrimary)
                }
            } else if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💬", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = LanguageManager.translate("chat_empty", currentLanguage, "Belum ada pesan"),
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages.size) { index ->
                        ChatBubble(messages[index])
                    }
                }
            }
        }

        // ==== ERROR ====
        if (errorMessage != null) {
            Surface(
                color = Color(0xFFEF4444).copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = errorMessage ?: "",
                    fontSize = 11.sp,
                    color = Color(0xFFFCA5A5),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // ==== INPUT FIELD ====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { if (it.length <= 200) inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = LanguageManager.translate("chat_hint", currentLanguage, "Tulis pesan..."),
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = Color(0xFF334155),
                    cursorColor = EmeraldPrimary,
                    focusedContainerColor = SurfaceDark.copy(alpha = 0.5f),
                    unfocusedContainerColor = SurfaceDark.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank() && !isSending) {
                            chatViewModel.sendMessage(inputText)
                            inputText = ""
                            keyboardController?.hide()
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank() && !isSending) {
                        chatViewModel.sendMessage(inputText)
                        inputText = ""
                        keyboardController?.hide()
                    }
                },
                enabled = inputText.isNotBlank() && !isSending,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (inputText.isNotBlank()) EmeraldPrimary else Color(0xFF334155)
                    )
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.Black else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isOwn = message.isOwn
    val alignment = if (isOwn) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (isOwn) EmeraldDeep else SurfaceDark.copy(alpha = 0.8f)
    val borderColor = if (isOwn) EmeraldPrimary.copy(alpha = 0.5f) else Color(0xFF334155)
    val nameColor = if (isOwn) EmeraldLight else GoldVip

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
        ) {
            // Nama + waktu
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isOwn) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(GoldVip.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.userName.take(1).uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldVip
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = if (isOwn) "You" else message.userName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = nameColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatTime(message.timestamp),
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Bubble pesan
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = if (isOwn) 12.dp else 2.dp,
                    bottomEnd = if (isOwn) 2.dp else 12.dp
                ),
                modifier = Modifier.border(1.dp, borderColor, RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = if (isOwn) 12.dp else 2.dp,
                    bottomEnd = if (isOwn) 2.dp else 12.dp
                ))
            ) {
                Text(
                    text = message.message,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    if (timestamp <= 0) return ""
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.US)
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}
