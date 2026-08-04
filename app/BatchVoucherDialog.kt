package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TelebirrGreenPrimary
import com.example.viewmodel.AppLanguage

@Composable
fun BatchVoucherDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onAddStock: (denomination: Int, count: String) -> Unit
) {
    var selectedDenom by remember { mutableIntStateOf(100) }
    var countText by remember { mutableStateOf("10") }
    val denominations = listOf(100, 50, 25, 15, 10, 5)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == AppLanguage.AMHARIC) "አዲስ የኤርታይም ካርዶች ክምችት መጨመሪያ" else "Batch Add Airtime Vouchers",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "የካርዱን አይነት ይምረጡ:" else "Select Denomination:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    denominations.take(3).forEach { denom ->
                        FilterChip(
                            selected = selectedDenom == denom,
                            onClick = { selectedDenom = denom },
                            label = { Text("$denom ETB") },
                            modifier = Modifier.weight(1f).testTag("denom_chip_$denom")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    denominations.drop(3).forEach { denom ->
                        FilterChip(
                            selected = selectedDenom == denom,
                            onClick = { selectedDenom = denom },
                            label = { Text("$denom ETB") },
                            modifier = Modifier.weight(1f).testTag("denom_chip_$denom")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = countText,
                    onValueChange = { countText = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "የካርድ ብዛት (Quantity)" else "Quantity of Cards") },
                    leadingIcon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = "Count") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("voucher_count_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddStock(selectedDenom, countText)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreenPrimary),
                modifier = Modifier.testTag("submit_batch_voucher_button")
            ) {
                Text(if (language == AppLanguage.AMHARIC) "ካርዶቹን ጨምር" else "Generate & Upload")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.AMHARIC) "ሰርዝ" else "Cancel")
            }
        }
    )
}
