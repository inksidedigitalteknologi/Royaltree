package com.inkside.digital.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val AffiliateGold = Color(0xFFFFD700)
private val AffiliateEmerald = Color(0xFF10B981)

/**
 * AffiliateWithdrawDialog — dialog untuk withdraw balance affiliate.
 */
@Composable
fun AffiliateWithdrawDialog(
    availableBalance: Double,
    minWithdraw: Double = 10.0,
    userEmail: String = "",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onSubmit: (amount: Double, destination: String) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember {
        mutableStateOf(String.format("%.2f", availableBalance).replace(",", "."))
    }
    var destination by remember { mutableStateOf(userEmail) }
    var localError by remember { mutableStateOf<String?>(null) }

    val amount = amountText.toDoubleOrNull() ?: 0.0

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Withdraw Affiliate",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Tarik balance ke PayPal",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Available",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "$${String.format("%.2f", availableBalance)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = AffiliateEmerald
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { c -> c.isDigit() || c == '.' }
                        localError = null
                    },
                    label = { Text("Jumlah (USD)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = localError != null
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = destination,
                    onValueChange = {
                        destination = it
                        localError = null
                    },
                    label = { Text("Email PayPal") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Min: $${String.format("%.0f", minWithdraw)} · Proses 1-3 hari kerja",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val displayError = localError ?: errorMessage
                if (displayError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        displayError,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            when {
                                amount <= 0 -> localError = "Jumlah tidak valid."
                                amount < minWithdraw -> localError = "Min withdraw $${String.format("%.0f", minWithdraw)}."
                                amount > availableBalance -> localError = "Melebihi balance available."
                                destination.isBlank() -> localError = "Email PayPal wajib diisi."
                                !destination.contains("@") -> localError = "Email tidak valid."
                                else -> onSubmit(amount, destination.trim())
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AffiliateGold),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text(
                            if (isLoading) "Proses..." else "Withdraw",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
