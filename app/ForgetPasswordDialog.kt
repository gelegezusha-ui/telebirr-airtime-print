package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TelebirrGoldAccent
import com.example.ui.theme.TelebirrGreenPrimary
import com.example.viewmodel.AppLanguage

@Composable
fun ForgetPasswordDialog(
    language: AppLanguage,
    resetPinResult: String?,
    onDismiss: () -> Unit,
    onRequestReset: (String) -> Unit
) {
    var phoneInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockReset,
                        contentDescription = "Reset PIN",
                        tint = TelebirrGreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == AppLanguage.AMHARIC) "የይለፍ ቃል ማግኛ (Forget Password)" else "Reset 4-Digit PIN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (resetPinResult == null) {
                    Text(
                        text = if (language == AppLanguage.AMHARIC)
                            "እባክዎን የተመዘገቡበትን ስልክ ቁጥር ያስገቡ። አዲስ 4 አሃዝ ፒን (PIN) ጀነረት ተደርጎ የሚሰጥዎት ሲሆን የቀድሞ ቀሪ ሂሳብዎ (Balance) ፍጹም አይነካም!"
                        else
                            "Enter your registered phone number. A new 4-digit PIN will be generated. Your account balance remains 100% untouched & intact!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = {
                            Text(if (language == AppLanguage.AMHARIC) "የስልክ ቁጥር (09...)" else "Phone Number (09...)")
                        },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forget_password_phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    // Safety Guarantee Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "🔒 " + (if (language == AppLanguage.AMHARIC) "የቀሪ ሂሳብ ደህንነት ማረጋገጫ:" else "Balance Security Guarantee:"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = TelebirrGreenPrimary
                            )
                            Text(
                                text = if (language == AppLanguage.AMHARIC)
                                    "ፒኑን ሲቀይሩ አጠቃላይ ቀሪ ሂሳብዎ ሳንቲም ሳይጎድል ደህንነቱ ተጠብቆ ይቆያል።"
                                else
                                    "Resetting your PIN guarantees your existing ETB wallet balance is completely safe.",
                                fontSize = 11.sp,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                } else {
                    // Result display
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "New PIN",
                                tint = TelebirrGreenPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (language == AppLanguage.AMHARIC) "አዲሱ የይለፍ ቃልዎ (New PIN):" else "Your New 4-Digit PIN:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = resetPinResult,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = TelebirrGreenPrimary,
                                letterSpacing = 4.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (language == AppLanguage.AMHARIC) "ቀሪ ሂሳብዎ በጥንቃቄ አልተነካም!" else "Your Balance Remains Intact!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (resetPinResult == null) {
                Button(
                    onClick = { onRequestReset(phoneInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreenPrimary),
                    modifier = Modifier.testTag("submit_forget_password_button")
                ) {
                    Text(if (language == AppLanguage.AMHARIC) "አዲስ ፒን አውጣ" else "Reset PIN")
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreenPrimary),
                    modifier = Modifier.testTag("close_reset_result_button")
                ) {
                    Text(if (language == AppLanguage.AMHARIC) "ገባኝ / Done" else "Done")
                }
            }
        },
        dismissButton = {
            if (resetPinResult == null) {
                TextButton(onClick = onDismiss) {
                    Text(if (language == AppLanguage.AMHARIC) "ሰርዝ" else "Cancel")
                }
            }
        }
    )
}
