package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.ui.theme.TelebirrGreenPrimary
import com.example.viewmodel.AppLanguage

@Composable
fun TopUpDialog(
    customer: Customer,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onTopUp: (amount: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == AppLanguage.AMHARIC) "የደንበኛ ሂሳብ መሙያ (Deposit Balance)" else "Top Up Customer Balance",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${customer.name} (${customer.phone})",
                    fontWeight = FontWeight.Bold,
                    color = TelebirrGreenPrimary
                )
                Text(
                    text = if (language == AppLanguage.AMHARIC) "የአሁኑ ቀሪ ሂሳብ: ${"%.2f".format(customer.balance)} ብር" else "Current Balance: ${"%.2f".format(customer.balance)} ETB",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "የሚጨመረው የብር መጠን (ETB)" else "Deposit Amount (ETB)") },
                    leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Deposit") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("topup_amount_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTopUp(amountText)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreenPrimary),
                modifier = Modifier.testTag("submit_topup_button")
            ) {
                Text(if (language == AppLanguage.AMHARIC) "መዝግብ / Deposit" else "Deposit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.AMHARIC) "ሰርዝ" else "Cancel")
            }
        }
    )
}
