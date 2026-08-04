package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrinterConfig
import com.example.printer.PrinterDevice
import com.example.ui.theme.TelebirrGreenPrimary
import com.example.viewmodel.AppLanguage

@Composable
fun PrinterSettingsDialog(
    config: PrinterConfig?,
    pairedDevices: List<PrinterDevice>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onRefreshDevices: () -> Unit,
    onSaveConfig: (name: String, address: String, widthMm: Int, autoPrint: Boolean, agentName: String, agentCode: String) -> Unit
) {
    var printerName by remember { mutableStateOf(config?.printerName ?: "POS Thermal Printer") }
    var printerAddress by remember { mutableStateOf(config?.printerAddress ?: "") }
    var paperWidth by remember { mutableStateOf((config?.paperWidthMm ?: 58).toString()) }
    var autoPrint by remember { mutableStateOf(config?.autoPrintEnabled ?: true) }
    var agentName by remember { mutableStateOf(config?.agentName ?: "Ethio Telebirr Agent") }
    var agentCode by remember { mutableStateOf(config?.agentCode ?: "TB-AG0091") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Print, contentDescription = "Printer", tint = TelebirrGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.AMHARIC) "የብሉቱዝ ቴርማል ፕሪንተር መቼት" else "Bluetooth Thermal Printer Setup",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "የተገናኙ የብሉቱዝ ፕሪንተሮች (Paired Devices):" else "Select Paired Bluetooth Printer:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (pairedDevices.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (language == AppLanguage.AMHARIC)
                                "ምንም የተቀናጀ ብሉቱዝ አልተገኘም። እባክዎን በስልክዎ ብሉቱዝ ፕሪንተሩን ፔር ያድርጉ።"
                            else
                                "No paired Bluetooth thermal printer found. Please pair device in phone settings.",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.height(110.dp)) {
                        items(pairedDevices) { dev ->
                            val isSelected = dev.address == printerAddress
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        printerName = dev.name
                                        printerAddress = dev.address
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bluetooth,
                                            contentDescription = "BT",
                                            tint = if (isSelected) TelebirrGreenPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = dev.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = dev.address, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = TelebirrGreenPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = agentName,
                    onValueChange = { agentName = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "የኤጀንቱ ስም (Agent Business Name)" else "Agent Business Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("agent_name_input")
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = agentCode,
                    onValueChange = { agentCode = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "የኤጀንት መለያ ኮድ (Agent Code)" else "Agent Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("agent_code_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "ካርዱ ሲገዛ አውቶማቲክ ይፕረንት" else "Auto Print on Purchase",
                        fontSize = 12.sp
                    )
                    Switch(checked = autoPrint, onCheckedChange = { autoPrint = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = paperWidth.toIntOrNull() ?: 58
                    onSaveConfig(printerName, printerAddress, w, autoPrint, agentName, agentCode)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreenPrimary),
                modifier = Modifier.testTag("save_printer_config_button")
            ) {
                Text(if (language == AppLanguage.AMHARIC) "አስቀምጥ" else "Save Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.AMHARIC) "ሰርዝ" else "Cancel")
            }
        }
    )
}
