package com.inkside.digital.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.viewmodel.AppScreen

@Composable
fun AppDrawer(
    user: UserEntity?,
    isAdminMode: Boolean,
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    onToggleRole: () -> Unit,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // ============ HEADER ============
            DrawerHeader(user = user)

            Spacer(modifier = Modifier.height(8.dp))

            // ============ MENU UTAMA ============
            DrawerItem(
                icon = Icons.Default.Home,
                label = "Beranda",
                selected = currentScreen == AppScreen.HOME,
                onClick = { onNavigate(AppScreen.HOME); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.TaskAlt,
                label = "Misi",
                selected = currentScreen == AppScreen.MISSIONS,
                onClick = { onNavigate(AppScreen.MISSIONS); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.SportsEsports,
                label = "Game Room",
                selected = currentScreen == AppScreen.GAME_ROOM,
                onClick = { onNavigate(AppScreen.GAME_ROOM); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.Analytics,
                label = "Analitik",
                selected = currentScreen == AppScreen.ANALYTICS,
                onClick = { onNavigate(AppScreen.ANALYTICS); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.MonetizationOn,
                label = "Penarikan",
                selected = currentScreen == AppScreen.WITHDRAW,
                onClick = { onNavigate(AppScreen.WITHDRAW); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.History,
                label = "Riwayat",
                selected = currentScreen == AppScreen.HISTORY,
                onClick = { onNavigate(AppScreen.HISTORY); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.CardGiftcard,
                label = "Undang Teman",
                selected = currentScreen == AppScreen.REFERRAL,
                onClick = { onNavigate(AppScreen.REFERRAL); onCloseDrawer() }
            )

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(4.dp))

            // ============ MENU PROFIL ============
            DrawerItem(
                icon = Icons.Default.Person,
                label = "Profil",
                selected = currentScreen == AppScreen.PROFILE,
                onClick = { onNavigate(AppScreen.PROFILE); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.Settings,
                label = "Pengaturan",
                selected = currentScreen == AppScreen.SETTINGS,
                onClick = { onNavigate(AppScreen.SETTINGS); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.HelpOutline,
                label = "Bantuan",
                selected = currentScreen == AppScreen.FAQ,
                onClick = { onNavigate(AppScreen.FAQ); onCloseDrawer() }
            )

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(4.dp))

            // ============ MENU BAWAH ============
            DrawerItem(
                icon = Icons.Default.ExitToApp,
                label = "Keluar",
                selected = false,
                tint = MaterialTheme.colorScheme.error,
                onClick = { showLogoutDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ============ DIALOG LOGOUT ============
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar dari Akun?") },
            text = { Text("Anda yakin ingin keluar dari Royaltree?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onCloseDrawer()
                    onLogout()
                }) {
                    Text("Keluar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

// ============ HEADER: FOTO, NAMA, EMAIL, TIER, SALDO, POIN ============
@Composable
private fun DrawerHeader(user: UserEntity?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ElectricBlue.copy(alpha = 0.1f))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (user?.name?.firstOrNull()?.uppercase() ?: "U"),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user?.name ?: "User",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user?.email ?: "-",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Tier badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (user?.tier == "PREMIUM") GoldVip else MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (user?.tier == "PREMIUM") {
                                Icon(Icons.Default.Star, null, tint = Color.Black, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = if (user?.tier == "PREMIUM") "VIP" else "FREE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user?.tier == "PREMIUM") Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Saldo & Poin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(
                    modifier = Modifier.weight(1f),
                    label = "Saldo",
                    value = "Rp " + formatNumber(user?.balance ?: 0.0)
                )
                InfoChip(
                    modifier = Modifier.weight(1f),
                    label = "Poin",
                    value = formatNumber((user?.points ?: 0).toDouble())
                )
            }
        }
    }
}

@Composable
private fun InfoChip(modifier: Modifier, label: String, value: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp)
    ) {
        Column {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
        }
    }
}

// ============ ITEM MENU ============
@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tint: Color? = null
) {
    val itemTint = tint ?: if (selected) ElectricBlue else MaterialTheme.colorScheme.onSurface
    val bgColor = if (selected) ElectricBlue.copy(alpha = 0.1f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = itemTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = itemTint
        )
    }
}

// ============ HELPER: FORMAT ANGKA ============
private fun formatNumber(value: Double): String {
    return try {
        val longVal = value.toLong()
        val formatted = longVal.toString().reversed().chunked(3).joinToString(".").reversed()
        formatted
    } catch (e: Exception) {
        value.toString()
    }
}
