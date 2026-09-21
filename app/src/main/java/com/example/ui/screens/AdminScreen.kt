package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SystemSettingsEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserLocationLogEntity
import com.example.data.model.WithdrawalEntity
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldVip
import com.example.ui.theme.PurpleSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    user: UserEntity?,
    allUsers: List<UserEntity> = emptyList(),
    pendingWithdrawals: List<WithdrawalEntity>,
    allWithdrawals: List<WithdrawalEntity>,
    locationLogs: List<UserLocationLogEntity> = emptyList(),
    systemSettings: SystemSettingsEntity? = null,
    portalBaseUrl: String = "http://45.41.204.21:5000/api/v1/",
    portalApiKey: String = "rt_secret_portal_key_2026",
    portalStatus: String = "IDLE",
    portalStatusMessage: String = "Belum diuji",
    isSyncingWithPortal: Boolean = false,
    onReviewWithdrawal: (WithdrawalEntity) -> Unit,
    onSaveSystemSettings: (SystemSettingsEntity) -> Unit = {},
    onAddSimulatedPin: (userId: String, name: String, tier: String, lat: Double, lng: Double, city: String, prov: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onTestConnection: (url: String, apiKey: String) -> Unit = { _, _ -> },
    onSavePortalConfig: (url: String, apiKey: String) -> Unit = { _, _ -> },
    onSyncPortal: () -> Unit = {},
    onBackToUserMode: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Antrean Payout, 1: Pengguna & Saldo, 2: Lacak Lokasi & Heatmap, 3: Pengaturan Platform, 4: Endpoint & Portal
    val context = LocalContext.current

    val effectiveSettings = systemSettings ?: SystemSettingsEntity()

    var inputPortalUrl by remember(portalBaseUrl) { mutableStateOf(portalBaseUrl) }
    var inputPortalKey by remember(portalApiKey) { mutableStateOf(portalApiKey) }

    // Local form state for editable system settings
    var minEWallet by remember(systemSettings) { mutableStateOf(effectiveSettings.minWithdrawalEWallet.toInt().toString()) }
    var minBank by remember(systemSettings) { mutableStateOf(effectiveSettings.minWithdrawalBank.toInt().toString()) }
    var minCrypto by remember(systemSettings) { mutableStateOf(effectiveSettings.minWithdrawalCrypto.toInt().toString()) }
    var freeRate by remember(systemSettings) { mutableStateOf(effectiveSettings.freeTierCommissionRate.toString()) }
    var vipRate by remember(systemSettings) { mutableStateOf(effectiveSettings.vipTierCommissionRate.toString()) }
    var qrFee by remember(systemSettings) { mutableStateOf(effectiveSettings.qrTransferFeePoints.toString()) }
    var qrDailyLimit by remember(systemSettings) { mutableStateOf(effectiveSettings.qrTransferDailyLimitPoints.toString()) }
    var fraudVelocity by remember(systemSettings) { mutableStateOf(effectiveSettings.antiFraudVelocitySeconds.toString()) }
    var locationTrackingAllowed by remember(systemSettings) { mutableStateOf(effectiveSettings.isLocationTrackingEnabled) }
    var maintenanceMode by remember(systemSettings) { mutableStateOf(effectiveSettings.maintenanceMode) }

    // Map filter state
    var selectedCityFilter by remember { mutableStateOf("ALL") }

    val userList = if (allUsers.isNotEmpty()) allUsers else listOfNotNull(user)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Admin Header Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PurpleSecondary.copy(alpha = 0.12f))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PurpleSecondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Portal Admin & Provider Royaltree", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("Pusat Pengaturan, Keuangan & Pelacakan Wilayah", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Button(
                            onClick = onBackToUserMode,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Keluar Admin", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminStatPill(title = "Antrean Pending", value = "${pendingWithdrawals.size}", modifier = Modifier.weight(1f))
                        AdminStatPill(title = "Total Pengguna", value = "${userList.size} User", modifier = Modifier.weight(1.1f))
                        AdminStatPill(title = "Pin Lokasi Terlacak", value = "${locationLogs.size} Pin", modifier = Modifier.weight(1.2f))
                        AdminStatPill(title = "Status Gateway", value = if (maintenanceMode) "MAINTENANCE 🟠" else "ONLINE 🟢", modifier = Modifier.weight(1.3f))
                    }
                }
            }
        }

        // 4 Tabs: Antrean, Pengguna, Lacak Lokasi (Heatmap), Pengaturan Sistem
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PurpleSecondary,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PurpleSecondary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Antrean Payout (${pendingWithdrawals.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Data Pengguna (${userList.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("📍 Lacak Lokasi & Heatmap", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("⚙️ Pengaturan Platform", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("🔌 Endpoint & Portal", fontSize = 11.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Pending Withdrawals
                if (pendingWithdrawals.isEmpty()) {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Semua Penarikan Telah Diproses!", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Tidak ada permintaan penarikan yang tertunda saat ini.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(pendingWithdrawals) { withdrawal ->
                        AdminWithdrawalCard(
                            withdrawal = withdrawal,
                            onReview = { onReviewWithdrawal(withdrawal) }
                        )
                    }
                }
            }

            1 -> {
                // User Management
                items(userList) { u ->
                    AdminUserCard(user = u)
                }
            }

            2 -> {
                // User Location Tracking Pinpoint & Heatmap for Provider
                item {
                    AdminLocationTrackingSection(
                        locationLogs = locationLogs,
                        selectedCityFilter = selectedCityFilter,
                        onSelectCity = { selectedCityFilter = it },
                        onSimulatePing = { city, prov, lat, lng ->
                            onAddSimulatedPin(
                                "usr_pin_${System.currentTimeMillis().toString().takeLast(4)}",
                                "User $city",
                                if (lat > -6.5) "PREMIUM" else "FREE",
                                lat,
                                lng,
                                city,
                                prov
                            )
                        }
                    )
                }
            }

            3 -> {
                // Centralized System & Platform Settings Editor
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = PurpleSecondary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Pusat Pengaturan Platform", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("Kelola semua parameter sistem, batas penarikan & keamanan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            HorizontalDivider()

                            // Group 1: Batas Penarikan Dana
                            Text("1. Batas Minimum Penarikan Dana (IDR)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PurpleSecondary)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = minEWallet,
                                    onValueChange = { minEWallet = it },
                                    label = { Text("Min. E-Wallet", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = minBank,
                                    onValueChange = { minBank = it },
                                    label = { Text("Min. Bank", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = minCrypto,
                                    onValueChange = { minCrypto = it },
                                    label = { Text("Min. Kripto", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }

                            // Group 2: Persentase Komisi
                            Text("2. Tarif Komisi Affiliasi (%)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PurpleSecondary)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = freeRate,
                                    onValueChange = { freeRate = it },
                                    label = { Text("Tier Free (%)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = vipRate,
                                    onValueChange = { vipRate = it },
                                    label = { Text("Tier VIP (%)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true
                                )
                            }

                            // Group 3: Batas & Keamanan Transfer RTP
                            Text("3. Keamanan & Batas Transfer QR (RTP)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PurpleSecondary)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = qrFee,
                                    onValueChange = { qrFee = it },
                                    label = { Text("Biaya Admin (RTP)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = qrDailyLimit,
                                    onValueChange = { qrDailyLimit = it },
                                    label = { Text("Limit Harian (RTP)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = fraudVelocity,
                                    onValueChange = { fraudVelocity = it },
                                    label = { Text("Jeda Mutex (Detik)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }

                            // Group 4: Kebijakan Operasional Platform
                            Text("4. Fitur Pelacakan & Status Server", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PurpleSecondary)

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Pelacakan Pinpoint Pengguna", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Mengumpulkan koordinat persebaran untuk analitik provider", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Switch(
                                            checked = locationTrackingAllowed,
                                            onCheckedChange = { locationTrackingAllowed = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = PurpleSecondary,
                                                checkedTrackColor = PurpleSecondary.copy(alpha = 0.5f)
                                            )
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Mode Pemeliharaan (Maintenance)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Nonaktifkan sementara penarikan dana untuk audit", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Switch(
                                            checked = maintenanceMode,
                                            onCheckedChange = { maintenanceMode = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color(0xFFFF9800),
                                                checkedTrackColor = Color(0xFFFF9800).copy(alpha = 0.5f)
                                            )
                                        )
                                    }
                                }
                            }

                            // Save Button
                            Button(
                                onClick = {
                                    val updated = effectiveSettings.copy(
                                        minWithdrawalEWallet = minEWallet.toDoubleOrNull() ?: 50000.0,
                                        minWithdrawalBank = minBank.toDoubleOrNull() ?: 100000.0,
                                        minWithdrawalCrypto = minCrypto.toDoubleOrNull() ?: 250000.0,
                                        freeTierCommissionRate = freeRate.toDoubleOrNull() ?: 12.0,
                                        vipTierCommissionRate = vipRate.toDoubleOrNull() ?: 30.0,
                                        qrTransferFeePoints = qrFee.toIntOrNull() ?: 5,
                                        qrTransferDailyLimitPoints = qrDailyLimit.toIntOrNull() ?: 5000,
                                        antiFraudVelocitySeconds = fraudVelocity.toIntOrNull() ?: 10,
                                        isLocationTrackingEnabled = locationTrackingAllowed,
                                        maintenanceMode = maintenanceMode,
                                        updatedTimestamp = System.currentTimeMillis()
                                    )
                                    onSaveSystemSettings(updated)
                                    Toast.makeText(context, "Pengaturan sistem berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_save_system_settings")
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simpan Perubahan Pengaturan", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            4 -> {
                // Endpoint Portal & Server Integration
                item {
                    AdminPortalEndpointSection(
                        portalUrl = inputPortalUrl,
                        portalApiKey = inputPortalKey,
                        portalStatus = portalStatus,
                        portalStatusMessage = portalStatusMessage,
                        isSyncing = isSyncingWithPortal,
                        onUrlChange = { inputPortalUrl = it },
                        onApiKeyChange = { inputPortalKey = it },
                        onTestConnection = { onTestConnection(inputPortalUrl, inputPortalKey) },
                        onSaveConfig = { onSavePortalConfig(inputPortalUrl, inputPortalKey) },
                        onSyncPortal = onSyncPortal
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun AdminUserCard(user: UserEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(user.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (user.tier == "PREMIUM") GoldVip else MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (user.tier == "PREMIUM") "👑 VIP" else "FREE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user.tier == "PREMIUM") Color.Black else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Saldo Komisi Tersedia:", fontSize = 11.sp)
                        Text("Rp ${String.format("%,.0f", user.balance)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EmeraldLight)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Poin Loyalitas (RTP):", fontSize = 11.sp)
                        Text("${user.points} RTP", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Status 2FA & Enkripsi:", fontSize = 11.sp)
                        Text(if (user.is2FAEnabled) "AKTIF (Aman) 🔒" else "NONAKTIF", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pinpoint Lokasi Terakhir:", fontSize = 11.sp)
                        Text("${user.locationCity}, ${user.locationProvince} 📍", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ElectricBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminLocationTrackingSection(
    locationLogs: List<UserLocationLogEntity>,
    selectedCityFilter: String,
    onSelectCity: (String) -> Unit,
    onSimulatePing: (city: String, prov: String, lat: Double, lng: Double) -> Unit
) {
    val context = LocalContext.current

    // Group locations by city to calculate density
    val cityDensity = locationLogs.groupBy { it.cityName }
    val totalPins = locationLogs.size

    // Determine crowded vs quiet cities
    // Cities sorted by user count descending
    val sortedCities = cityDensity.entries.sortedByDescending { it.value.size }
    val crowdedCity = sortedCities.firstOrNull()
    val quietCity = sortedCities.lastOrNull()

    val filteredLogs = if (selectedCityFilter == "ALL") locationLogs else locationLogs.filter { it.cityName == selectedCityFilter }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Density Analytics Card (Ramai vs Sepi)
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = ElectricBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Analisis Densitas Wilayah (Provider)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Pelacakan otomatis kepadatan pengguna platform secara realtime", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Ramai Box
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔥", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WILAYAH RAMAI", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(crowdedCity?.key ?: "Jakarta", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B5E20))
                            Text("${crowdedCity?.value?.size ?: 0} Pengguna Aktif", fontSize = 10.sp, color = Color(0xFF388E3C))
                        }
                    }

                    // Sepi Box
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("❄️", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WILAYAH SEPI", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(quietCity?.key ?: "Pontianak", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFBF360C))
                            Text("${quietCity?.value?.size ?: 0} Pengguna (Peluang Ekspansi)", fontSize = 10.sp, color = Color(0xFFF57C00))
                        }
                    }
                }
            }
        }

        // Visual Heatmap Canvas (Interactive Radar / Grid Map)
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = EmeraldLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Peta Persebaran & Heatmap", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Text("Total $totalPins Titik Terlacak", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas Map Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Draw Grid Lines for Radar / Map coordinates
                        val gridStep = 35f
                        var x = 0f
                        while (x < canvasWidth) {
                            drawLine(
                                color = Color(0xFF1E293B),
                                start = Offset(x, 0f),
                                end = Offset(x, canvasHeight),
                                strokeWidth = 1f
                            )
                            x += gridStep
                        }
                        var y = 0f
                        while (y < canvasHeight) {
                            drawLine(
                                color = Color(0xFF1E293B),
                                start = Offset(0f, y),
                                end = Offset(canvasWidth, y),
                                strokeWidth = 1f
                            )
                            y += gridStep
                        }

                        // Project GPS Coordinates (Indonesia bounds approx: Lat -11 to +6, Lng 95 to 141)
                        val minLat = -11.0
                        val maxLat = 6.0
                        val minLng = 95.0
                        val maxLng = 141.0

                        locationLogs.forEach { log ->
                            val normX = ((log.longitude - minLng) / (maxLng - minLng)).toFloat().coerceIn(0.08f, 0.92f)
                            val normY = (1f - ((log.latitude - minLat) / (maxLat - minLat)).toFloat()).coerceIn(0.08f, 0.92f)

                            val pinX = normX * canvasWidth
                            val pinY = normY * canvasHeight

                            // Heatmap pulse aura
                            drawCircle(
                                color = if (log.userTier == "PREMIUM") GoldVip.copy(alpha = 0.35f) else EmeraldLight.copy(alpha = 0.3f),
                                radius = 18f,
                                center = Offset(pinX, pinY)
                            )
                            // Solid center pinpoint
                            drawCircle(
                                color = if (log.userTier == "PREMIUM") GoldVip else EmeraldLight,
                                radius = 6f,
                                center = Offset(pinX, pinY)
                            )
                        }
                    }

                    // Map overlay labels
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("📍 Radar Pinpoint Indonesia", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("🔴 Ramai: Jabodetabek, Jabar, Jatim", fontSize = 9.sp, color = EmeraldLight)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xCC000000).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🟢 User Free  👑 VIP User", fontSize = 9.sp, color = Color.White)
                            Text("Zona Sepi: Peluang Promo Tambahan", fontSize = 9.sp, color = Color(0xFFFFB74D))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Simulate new pinpoint button for provider testing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Uji Simulasi Pin Provider:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                onSimulatePing("Surabaya", "Jawa Timur", -7.2575, 112.7521)
                                Toast.makeText(context, "Pin baru masuk di Surabaya (+1 Ramai)", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ Surabaya", fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                onSimulatePing("Jayapura", "Papua", -2.5337, 140.7181)
                                Toast.makeText(context, "Pin baru masuk di Jayapura (Wilayah Sepi)", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ Jayapura", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Filter by City & Pin Log Stream
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Daftar Detail Pinpoint Pengguna", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Menampilkan data koordinat GPS yang terekam oleh server", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(10.dp))

                // City Filter Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL", "Jakarta Pusat", "Bandung", "Surabaya", "Yogyakarta", "Medan").forEach { city ->
                        val isSelected = selectedCityFilter == city
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) PurpleSecondary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onSelectCity(city) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (city == "ALL") "Semua" else city,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    filteredLogs.take(15).forEach { log ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (log.userTier == "PREMIUM") GoldVip else ElectricBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("${log.userName} • ${log.cityName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${log.provinceOrRegion} • Lat: ${String.format("%.4f", log.latitude)}, Lng: ${String.format("%.4f", log.longitude)}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(EmeraldLight.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(log.activityStatus, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(sdf.format(Date(log.timestamp)), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatPill(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun AdminWithdrawalCard(
    withdrawal: WithdrawalEntity,
    onReview: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(withdrawal.requestedAt))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(withdrawal.accountHolderName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${withdrawal.providerName} • ${withdrawal.accountDestination}", fontSize = 11.sp, color = ElectricBlue)
                    Text("Ref: ${withdrawal.txRef} • $dateStr", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldVip.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("PENDING REVIEW", fontSize = 9.sp, fontWeight = FontWeight.Black, color = GoldVip)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nominal Cair Bersih:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Rp ${String.format("%,.0f", withdrawal.netAmount)}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = EmeraldLight)
                }

                Button(
                    onClick = onReview,
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_admin_review_${withdrawal.id}")
                ) {
                    Text("Proses / Validasi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminPortalEndpointSection(
    portalUrl: String,
    portalApiKey: String,
    portalStatus: String,
    portalStatusMessage: String,
    isSyncing: Boolean,
    onUrlChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onTestConnection: () -> Unit,
    onSaveConfig: () -> Unit,
    onSyncPortal: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedCodeTab by remember { mutableIntStateOf(0) } // 0: Node.js (server.js), 1: PHP (api_portal.php), 2: cURL
    var expandedEndpointIndex by remember { mutableIntStateOf(0) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. Connection Setting & Gateway Status Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lan, contentDescription = null, tint = ElectricBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Koneksi Portal Server Anda", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Hubungkan aplikasi Royaltree ke sistem portal pusat Anda", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Status Pill
                    val (statusBg, statusFg, statusText) = when (portalStatus) {
                        "ONLINE" -> Triple(EmeraldLight.copy(alpha = 0.2f), EmeraldLight, "ONLINE 🟢")
                        "TESTING" -> Triple(Color(0xFFFFF9C4), Color(0xFFF57F17), "TESTING 🟡")
                        "ERROR" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "TERPUTUS 🔴")
                        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "SIAP ⚪")
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(statusText, fontSize = 10.sp, fontWeight = FontWeight.Black, color = statusFg)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Base URL Input
                OutlinedTextField(
                    value = portalUrl,
                    onValueChange = onUrlChange,
                    label = { Text("Base URL Portal Server API", fontSize = 11.sp) },
                    placeholder = { Text("Contoh: https://portal-anda.com/api/v1/ atau http://10.0.2.2:3000/api/v1/") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_portal_url"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // API Key / Bearer Secret Input
                OutlinedTextField(
                    value = portalApiKey,
                    onValueChange = onApiKeyChange,
                    label = { Text("API Secret Key (Bearer Token)", fontSize = 11.sp) },
                    placeholder = { Text("Contoh: rt_secret_portal_key_2026") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_portal_api_key"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Status: $portalStatusMessage",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (portalStatus == "ERROR") Color(0xFFE53935) else if (portalStatus == "ONLINE") EmeraldLight else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onTestConnection,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_test_portal_conn")
                    ) {
                        if (portalStatus == "TESTING") {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Uji Koneksi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onSaveConfig()
                            Toast.makeText(context, "URL & Key Portal disimpan!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_save_portal_config")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan URL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Full Data Sync Button
                Button(
                    onClick = onSyncPortal,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_sync_portal_now")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sedang Sinkronisasi...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sinkronisasi Penuh Sekarang (Data User & Poin)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // 2. REST API Endpoints Directory (Katalog Endpoint untuk Server Anda)
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = PurpleSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Katalog REST API Endpoints", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Spesifikasi lengkap untuk ditaruh di portal/server backend Anda", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val endpoints = listOf(
                    EndpointSpec(
                        method = "GET",
                        path = "/api/v1/health",
                        title = "1. Health Check (Ping Server)",
                        desc = "Memverifikasi apakah server portal Anda aktif & merespons dengan normal.",
                        curl = "curl -X GET \"http://localhost:3000/api/v1/health\"",
                        sampleResponse = "{\n  \"success\": true,\n  \"status\": \"ONLINE\",\n  \"message\": \"Royaltree Portal API Server aktif.\"\n}"
                    ),
                    EndpointSpec(
                        method = "GET",
                        path = "/api/v1/config",
                        title = "2. Remote Config (Pengaturan Sistem)",
                        desc = "Mengatur batas penarikan, kurs RTP, komisi VIP/Free, dan mode maintenance.",
                        curl = "curl -X GET \"http://localhost:3000/api/v1/config\"",
                        sampleResponse = "{\n  \"success\": true,\n  \"data\": {\n    \"maintenanceMode\": false,\n    \"minWithdrawalEWallet\": 50000,\n    \"freeTierCommissionRate\": 12.0,\n    \"vipTierCommissionRate\": 30.0\n  }\n}"
                    ),
                    EndpointSpec(
                        method = "POST",
                        path = "/api/v1/users/sync",
                        title = "3. User Sync (Sinkronisasi Data Pengguna)",
                        desc = "Mengunggah profil user, saldo, poin RTP, langkah kaki, dan referral ke database portal.",
                        curl = "curl -X POST \"http://localhost:3000/api/v1/users/sync\" \\\n  -H \"Authorization: Bearer rt_secret_portal_key_2026\" \\\n  -H \"Content-Type: application/json\" \\\n  -d '{\"id\":\"user_001\",\"name\":\"Hendra Wijaya\",\"points\":1850,\"balance\":4750000}'",
                        sampleResponse = "{\n  \"success\": true,\n  \"message\": \"Data pengguna berhasil disinkronkan ke portal.\"\n}"
                    ),
                    EndpointSpec(
                        method = "GET/PUT",
                        path = "/api/v1/withdrawals",
                        title = "4. Penarikan Dana & Approval",
                        desc = "Mengambil daftar permohonan withdraw (GET) dan menyetujui / membayar penarikan (PUT).",
                        curl = "curl -X PUT \"http://localhost:3000/api/v1/withdrawals/WD-90412/status\" \\\n  -H \"Authorization: Bearer rt_secret_portal_key_2026\" \\\n  -H \"Content-Type: application/json\" \\\n  -d '{\"status\":\"PAID\",\"adminNotes\":\"Ditransfer via BCA Bisnis\"}'",
                        sampleResponse = "{\n  \"success\": true,\n  \"message\": \"Status penarikan berhasil diubah menjadi PAID.\"\n}"
                    ),
                    EndpointSpec(
                        method = "GET/POST",
                        path = "/api/v1/campaigns",
                        title = "5. Kelola Kampanye Afiliasi",
                        desc = "Menampilkan dan menambahkan kampanye affiliate (Shopee, Tokopedia, dll.) dari portal.",
                        curl = "curl -X GET \"http://localhost:3000/api/v1/campaigns\"",
                        sampleResponse = "{\n  \"success\": true,\n  \"total\": 2,\n  \"data\": [\n    {\"id\": \"camp_001\", \"title\": \"Shopee Affiliate\", \"baseCommissionRate\": 15.0}\n  ]\n}"
                    ),
                    EndpointSpec(
                        method = "GET",
                        path = "/api/v1/missions",
                        title = "6. Kelola Misi Interaktif",
                        desc = "Mengontrol misi like medsos, subscribe, tonton video, dan surfing web langsung dari portal.",
                        curl = "curl -X GET \"http://localhost:3000/api/v1/missions\"",
                        sampleResponse = "{\n  \"success\": true,\n  \"total\": 2,\n  \"data\": [\n    {\"id\": \"m_sub\", \"title\": \"Langganan YouTube\", \"rtpReward\": 50}\n  ]\n}"
                    ),
                    EndpointSpec(
                        method = "GET",
                        path = "/api/v1/postback",
                        title = "7. Webhook Postback Vendor Iklan / Offerwall",
                        desc = "Menerima notifikasi otomatis dari provider survei (BitLabs, Torox, Pollfish) saat user selesai tugas.",
                        curl = "curl \"http://localhost:3000/api/v1/postback?user_id=user_001&reward_points=100&tx_id=tx_998\"",
                        sampleResponse = "OK"
                    )
                )

                endpoints.forEachIndexed { index, ep ->
                    val isExpanded = expandedEndpointIndex == index
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedEndpointIndex = if (isExpanded) -1 else index }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val methodColor = when (ep.method) {
                                        "GET" -> EmeraldLight
                                        "POST" -> ElectricBlue
                                        "PUT" -> Color(0xFFFF9800)
                                        else -> PurpleSecondary
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(methodColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(ep.method, fontSize = 10.sp, fontWeight = FontWeight.Black, color = methodColor)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(ep.path, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(if (isExpanded) "▲" else "▼", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Text(ep.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                            Text(ep.desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Perintah cURL Uji Coba:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(ep.curl))
                                            Toast.makeText(context, "Perintah cURL disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Salin", modifier = Modifier.size(14.dp))
                                    }
                                }

                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        ep.curl,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Contoh Respons JSON (200 OK):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        ep.sampleResponse,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF4ADE80),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // 3. Ready-To-Deploy Server Source Code Card (Node.js & PHP)
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Script Server Siap Pakai", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Tersedia di folder /backend/ project Anda", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Selector Tab
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = { selectedCodeTab = 0 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedCodeTab == 0) PurpleSecondary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Node.js", fontSize = 10.sp, color = if (selectedCodeTab == 0) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                        Button(
                            onClick = { selectedCodeTab = 1 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedCodeTab == 1) PurpleSecondary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("PHP", fontSize = 10.sp, color = if (selectedCodeTab == 1) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val (fileName, codeSnippet) = if (selectedCodeTab == 0) {
                    Pair(
                        "backend/server.js (Node.js Express)",
                        """
// 1. Install dependencies: npm install express cors
// 2. Jalankan: node server.js
const express = require('express');
const cors = require('cors');
const app = express();
app.use(cors());
app.use(express.json());

const SECRET = "rt_secret_portal_key_2026";

// Health Check
app.get('/api/v1/health', (req, res) => {
    res.json({ success: true, status: "ONLINE", message: "Royaltree API Aktif" });
});

// Remote Config
app.get('/api/v1/config', (req, res) => {
    res.json({ success: true, data: { maintenanceMode: false, minWithdrawalEWallet: 50000 } });
});

// User Sync
app.post('/api/v1/users/sync', (req, res) => {
    console.log("Sync User:", req.body);
    res.json({ success: true, message: "User disinkronkan" });
});

app.listen(3000, () => console.log("Server aktif di port 3000"));
                        """.trimIndent()
                    )
                } else {
                    Pair(
                        "backend/api_portal.php (PHP cPanel/Apache)",
                        """
<?php
// Letakkan di public_html/api/index.php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Authorization, Content-Type');

${'$'}uri = ${'$'}_SERVER['REQUEST_URI'];
if (strpos(${'$'}uri, '/health') !== false) {
    echo json_encode(["success" => true, "status" => "ONLINE", "message" => "Portal PHP Siap"]);
    exit();
}
if (strpos(${'$'}uri, '/config') !== false) {
    echo json_encode(["success" => true, "data" => ["minWithdrawalEWallet" => 50000]]);
    exit();
}
if (strpos(${'$'}uri, '/users/sync') !== false) {
    ${'$'}input = json_decode(file_get_contents('php://input'), true);
    echo json_encode(["success" => true, "message" => "Sinkronisasi user berhasil"]);
    exit();
}
?>
                        """.trimIndent()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(fileName, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = ElectricBlue)
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(codeSnippet))
                            Toast.makeText(context, "Kode $fileName disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Salin Script", modifier = Modifier.size(16.dp))
                    }
                }

                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        codeSnippet,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(codeSnippet))
                        Toast.makeText(context, "Kode $fileName disalin! Siap ditaruh di hosting Anda.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Salin Script Lengkap ke Clipboard", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

data class EndpointSpec(
    val method: String,
    val path: String,
    val title: String,
    val desc: String,
    val curl: String,
    val sampleResponse: String
)


