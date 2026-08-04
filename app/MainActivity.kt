package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Customer
import com.example.ui.components.AddCustomerDialog
import com.example.ui.components.BatchVoucherDialog
import com.example.ui.components.ForgetPasswordDialog
import com.example.ui.components.HeaderBar
import com.example.ui.components.PrinterSettingsDialog
import com.example.ui.components.ThermalReceiptDialog
import com.example.ui.components.TopUpDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CustomerDashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.TelebirrTheme
import com.example.viewmodel.TelebirrViewModel
import com.example.viewmodel.UserRole
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: TelebirrViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TelebirrTheme {
                TelebirrApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TelebirrApp(viewModel: TelebirrViewModel) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val activeCustomer by viewModel.activeCustomer.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val allVouchers by viewModel.allVouchers.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val printerConfig by viewModel.printerConfig.collectAsStateWithLifecycle()
    val totalSales by viewModel.totalSalesAmount.collectAsStateWithLifecycle()
    val totalDiscounts by viewModel.totalDiscountsGiven.collectAsStateWithLifecycle()
    val pairedPrinters by viewModel.pairedPrinters.collectAsStateWithLifecycle()
    val latestReceipt by viewModel.latestReceipt.collectAsStateWithLifecycle()
    val resetPinResult by viewModel.resetPinResult.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog visibilities
    var showForgetPasswordDialog by remember { mutableStateOf(false) }
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var topUpTargetCustomer by remember { mutableStateOf<Customer?>(null) }
    var showBatchVoucherDialog by remember { mutableStateOf(false) }
    var showPrinterSettingsDialog by remember { mutableStateOf(false) }

    // Collect UI toast/snackbar messages
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = "${msg.title}: ${msg.message}"
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            HeaderBar(
                language = language,
                userRole = userRole,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onLogout = { viewModel.logout() },
                onOpenPrinterSettings = {
                    viewModel.refreshBluetoothDevices()
                    showPrinterSettingsDialog = true
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (userRole) {
                UserRole.LOGGED_OUT -> {
                    LoginScreen(
                        language = language,
                        onCustomerLogin = { phone, pin -> viewModel.loginCustomer(phone, pin) },
                        onAdminLogin = { pin -> viewModel.loginAdmin(pin) },
                        onOpenForgetPassword = { showForgetPasswordDialog = true }
                    )
                }

                UserRole.CUSTOMER -> {
                    activeCustomer?.let { customer ->
                        CustomerDashboardScreen(
                            customer = customer,
                            transactions = allTransactions,
                            printerConfig = printerConfig,
                            language = language,
                            onPurchaseAndPrint = { denom -> viewModel.purchaseAndPrintVoucher(denom) },
                            onOpenPrinterSettings = {
                                viewModel.refreshBluetoothDevices()
                                showPrinterSettingsDialog = true
                            }
                        )
                    }
                }

                UserRole.ADMIN -> {
                    AdminDashboardScreen(
                        customers = allCustomers,
                        vouchers = allVouchers,
                        transactions = allTransactions,
                        printerConfig = printerConfig,
                        totalSales = totalSales ?: 0.0,
                        totalDiscounts = totalDiscounts ?: 0.0,
                        language = language,
                        onOpenAddCustomer = { showAddCustomerDialog = true },
                        onOpenTopUp = { target -> topUpTargetCustomer = target },
                        onOpenBatchAddVouchers = { showBatchVoucherDialog = true },
                        onOpenPrinterSettings = {
                            viewModel.refreshBluetoothDevices()
                            showPrinterSettingsDialog = true
                        }
                    )
                }
            }

            // Thermal Receipt Modal (Voucher print slip)
            latestReceipt?.let { (tx, voucher) ->
                ThermalReceiptDialog(
                    transaction = tx,
                    voucher = voucher,
                    customerName = activeCustomer?.name ?: tx.customerName,
                    language = language,
                    onDismiss = { viewModel.dismissReceipt() },
                    onPrintPhysical = {
                        printerConfig?.printerAddress?.let { addr ->
                            viewModel.purchaseAndPrintVoucher(tx.denomination)
                        }
                    }
                )
            }

            // Forget Password Dialog (Requirement #2)
            if (showForgetPasswordDialog) {
                ForgetPasswordDialog(
                    language = language,
                    resetPinResult = resetPinResult,
                    onDismiss = {
                        showForgetPasswordDialog = false
                        viewModel.clearResetPinResult()
                    },
                    onRequestReset = { phone -> viewModel.requestForgetPassword(phone) }
                )
            }

            // Add Customer Dialog
            if (showAddCustomerDialog) {
                AddCustomerDialog(
                    language = language,
                    onDismiss = { showAddCustomerDialog = false },
                    onAddCustomer = { name, phone, email, initBal, customPin ->
                        viewModel.registerNewCustomer(name, phone, email, initBal, customPin)
                    }
                )
            }

            // Top-up Customer Balance Dialog
            topUpTargetCustomer?.let { customer ->
                TopUpDialog(
                    customer = customer,
                    language = language,
                    onDismiss = { topUpTargetCustomer = null },
                    onTopUp = { amount -> viewModel.topUpCustomerBalance(customer.id, amount) }
                )
            }

            // Batch Voucher Stock Dialog
            if (showBatchVoucherDialog) {
                BatchVoucherDialog(
                    language = language,
                    onDismiss = { showBatchVoucherDialog = false },
                    onAddStock = { denom, count -> viewModel.batchAddStockVouchers(denom, count) }
                )
            }

            // Printer Settings Dialog
            if (showPrinterSettingsDialog) {
                PrinterSettingsDialog(
                    config = printerConfig,
                    pairedDevices = pairedPrinters,
                    language = language,
                    onDismiss = { showPrinterSettingsDialog = false },
                    onRefreshDevices = { viewModel.refreshBluetoothDevices() },
                    onSaveConfig = { name, addr, width, autoPrint, agentName, agentCode ->
                        viewModel.updatePrinterConfig(name, addr, width, autoPrint, agentName, agentCode)
                    }
                )
            }
        }
    }
}
