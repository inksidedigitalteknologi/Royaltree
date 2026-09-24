package com.inkside.digital.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager

@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    onOpenLanguage: () -> Unit,
    onLogout: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var notifEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(true) }
    val darkModeEnabled = com.inkside.digital.data.preferences.AppThemePreferences.isDarkMode
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Column {
                Text(LanguageManager.translate("settings_title", currentLanguage, "Settings"), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                Text(LanguageManager.translate("settings_subtitle", currentLanguage, "Customize the app"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item { SectionHeader("Preferensi") }

        item { SettingRow(Icons.Default.Language, LanguageManager.translate("settings_language", currentLanguage, "Language"), "Pilih bahasa aplikasi", onOpenLanguage) }
        item { SettingSwitch(Icons.Default.Notifications, LanguageManager.translate("settings_notifications", currentLanguage, "Notifications"), "Aktifkan notifikasi push", notifEnabled) { notifEnabled = it } }
        item { SettingSwitch(Icons.Default.LocationOn, LanguageManager.translate("settings_location", currentLanguage, "Location"), "Izinkan akses lokasi", locationEnabled) { locationEnabled = it } }
        item { SettingSwitch(Icons.Default.DarkMode, LanguageManager.translate("settings_dark_mode", currentLanguage, "Dark Mode"), "Gunakan tema gelap", darkModeEnabled) { 
            onToggleDarkMode(it)
        } }

        item { SectionHeader("Keamanan") }
        item { SettingRow(Icons.Default.Security, LanguageManager.translate("settings_security", currentLanguage, "Account Security"), "2FA, PIN, dan enkripsi", { }) }

        item { SectionHeader("Informasi") }
        item { SettingRow(Icons.Default.Policy, LanguageManager.translate("settings_privacy", currentLanguage, "Privacy Policy"), "Baca kebijakan privasi", { }) }
        item { SettingRow(Icons.Default.Info, LanguageManager.translate("settings_about", currentLanguage, "About Royaltree"), "Versi 1.0.0", { }) }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().clickable { showLogoutDialog = true }
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExitToApp, "Logout", tint = MaterialTheme.colorScheme.error)
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(LanguageManager.translate("settings_logout", currentLanguage, "Logout"), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                        Text(LanguageManager.translate("settings_logout_desc", currentLanguage, "Sign out"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(LanguageManager.translate("settings_logout", currentLanguage, "Logout") + "?") },
            text = { Text(LanguageManager.translate("settings_logout_confirm", currentLanguage, "Are you sure?")) },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text(LanguageManager.translate("settings_logout", currentLanguage, "Logout"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text(LanguageManager.translate("common_cancel", currentLanguage, "Cancel")) }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SettingSwitch(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
