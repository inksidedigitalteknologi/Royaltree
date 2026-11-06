package com.inkside.digital.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.preferences.AppThemePreferences
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary

// Daftar negara (kode, nama, emoji)
data class Country(val code: String, val name: String, val flag: String)

val COUNTRIES = listOf(
    Country("ID", "Indonesia", "🇮🇩"),
    Country("US", "United States", "🇺🇸"),
    Country("GB", "United Kingdom", "🇬🇧"),
    Country("SG", "Singapore", "🇸🇬"),
    Country("MY", "Malaysia", "🇲🇾"),
    Country("JP", "Japan", "🇯🇵"),
    Country("CN", "China", "🇨🇳"),
    Country("SA", "Saudi Arabia", "🇸🇦"),
    Country("ES", "Spain", "🇪🇸"),
    Country("IN", "India", "🇮🇳"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    defaultName: String,
    isLoading: Boolean,
    errorMessage: String?,
    currentLanguage: AppLanguage,
    onSave: (name: String, phone: String, city: String, referralCode: String?) -> Unit,
    onLogout: () -> Unit
) {
    var firstName by remember { mutableStateOf(defaultName.substringBefore(" ")) }
    var lastName by remember { mutableStateOf(defaultName.substringAfter(" ", "")) }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(COUNTRIES[0]) }
    var countryDropdownExpanded by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { showContent = true }

    // Validasi
    val isNameValid = firstName.isNotBlank() && firstName.all { it.isLetter() || it == ' ' }
    val isPhoneValid = phone.length in 9..15 && phone.all { it.isDigit() }
    val canSubmit = isNameValid && isPhoneValid

    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Icon dengan gradient ring
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.linearGradient(
                                    listOf(EmeraldPrimary, Color(0xFFA855F7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = LanguageManager.translate(
                            "profile_setup_title",
                            AppThemePreferences.currentLanguage,
                            "Lengkapi Profil"
                        ),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = LanguageManager.translate(
                            "profile_setup_subtitle",
                            AppThemePreferences.currentLanguage,
                            "Data ini diperlukan untuk memulai"
                        ),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(700, delayMillis = 200)) + slideInVertically(tween(700, delayMillis = 200)) { it / 4 }
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        // ─── Nama Depan & Belakang (2 kolom) ───
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { newValue ->
                                    if (newValue.all { it.isLetter() || it == ' ' }) firstName = newValue
                                },
                                label = { Text("Nama Depan *") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f),
                                isError = firstName.isNotEmpty() && !isNameValid
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { newValue ->
                                    if (newValue.all { it.isLetter() || it == ' ' }) lastName = newValue
                                },
                                label = { Text("Nama Belakang") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ─── Dropdown Negara ───
                        ExposedDropdownMenuBox(
                            expanded = countryDropdownExpanded,
                            onExpandedChange = { countryDropdownExpanded = !countryDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = "${selectedCountry.flag} ${selectedCountry.name}",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Negara") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryDropdownExpanded) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = countryDropdownExpanded,
                                onDismissRequest = { countryDropdownExpanded = false }
                            ) {
                                COUNTRIES.forEach { country ->
                                    DropdownMenuItem(
                                        text = { Text("${country.flag} ${country.name}") },
                                        onClick = {
                                            selectedCountry = country
                                            countryDropdownExpanded = false
                                        },
                                        trailingIcon = {
                                            if (selectedCountry.code == country.code) {
                                                Icon(Icons.Default.Check, null, tint = EmeraldLight)
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ─── Nomor HP ───
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() } && newValue.length <= 15) phone = newValue
                            },
                            label = { Text("Nomor HP *") },
                            placeholder = { Text("Contoh: 81234567890") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            isError = phone.isNotEmpty() && !isPhoneValid,
                            supportingText = {
                                if (phone.isNotEmpty() && !isPhoneValid) {
                                    Text("Nomor HP harus 9–15 digit angka", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                } else {
                                    Text("${phone.length}/15 digit", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ─── Kota ───
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Kota / Wilayah") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ─── Referral ───
                        OutlinedTextField(
                            value = referralCode,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isLetterOrDigit() }) referralCode = newValue.uppercase()
                            },
                            label = { Text("Kode Referral (opsional)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "❌ $errorMessage",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp).fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = EmeraldLight)
            } else {
                // ─── Tombol Simpan Gradient ───
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldPrimary, Color(0xFFA855F7))
                            )
                        )
                        .clickable(enabled = canSubmit) {
                            val fullName = "$firstName ${lastName}".trim()
                            onSave(
                                fullName,
                                "+${selectedCountry.code}${phone.trim()}",
                                city.trim(),
                                referralCode.trim().ifBlank { null }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = LanguageManager.translate(
                            "save_and_continue",
                            AppThemePreferences.currentLanguage,
                            "Simpan & Lanjut"
                        ),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = LanguageManager.translate("profile_setup_logout", AppThemePreferences.currentLanguage, "Logout"),
                    fontSize = 13.sp,
                    color = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout() }
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
