package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.TelebirrGreenPrimary
import com.example.viewmodel.AppLanguage

@Composable
fun AddCustomerDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onAddCustomer: (name: String, phone: String, email: String, initialBalance: String, customPin: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("0") }
    var customPin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == AppLanguage.AMHARIC) "አዲስ ደንበኛ መመዝገቢያ" else "Register New Customer",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "ሙሉ ስም (Full Name)" else "Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_customer_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "ስልክ ቁጥር (Phone Number)" else "Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_customer_phone_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "ኢሜይል (Email)" else "Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_customer_email_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = initialBalance,
                    onValueChange = { initialBalance = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "የመጀመሪያ ቀሪ ሂሳብ (Initial Balance ETB)" else "Initial Balance (ETB)") },
                    leadingIcon = { Icon(Icons.Default.Money, contentDescription = "Balance") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_customer_balance_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customPin,
                    onValueChange = { if (it.length <= 4) customPin = it },
                    label = { Text(if (language == AppLanguage.AMHARIC) "4 አሃዝ ፒን (ባዶ ከተወ አውቶማቲክ ይፈጠራል)" else "4-digit PIN (blank = auto generate)") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_customer_pin_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddCustomer(name, phone, email, initialBalance, customPin.ifBlank { null })
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreenPrimary),
                modifier = Modifier.testTag("submit_add_customer_button")
            ) {
                Text(if (language == AppLanguage.AMHARIC) "መዝግብ (Register)" else "Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.AMHARIC) "ሰርዝ" else "Cancel")
            }
        }
    )
}
