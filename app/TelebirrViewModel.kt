package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AirtimeVoucher
import com.example.data.model.Customer
import com.example.data.model.PrinterConfig
import com.example.data.model.SalesTransaction
import com.example.data.repository.TelebirrRepository
import com.example.printer.BluetoothPrinterManager
import com.example.printer.PrinterDevice
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage {
    AMHARIC, ENGLISH
}

enum class UserRole {
    LOGGED_OUT, CUSTOMER, ADMIN
}

data class UiMessage(
    val title: String,
    val message: String,
    val isError: Boolean = false
)

class TelebirrViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = TelebirrRepository(
        customerDao = db.customerDao(),
        voucherDao = db.airtimeVoucherDao(),
        transactionDao = db.salesTransactionDao(),
        printerConfigDao = db.printerConfigDao()
    )
    val printerManager = BluetoothPrinterManager(application)

    // Language state
    private val _language = MutableStateFlow(AppLanguage.AMHARIC)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // User Role and active session
    private val _userRole = MutableStateFlow(UserRole.LOGGED_OUT)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _loggedInPhone = MutableStateFlow<String?>(null)
    val loggedInPhone: StateFlow<String?> = _loggedInPhone.asStateFlow()

    // Active customer reactive flow
    val activeCustomer: StateFlow<Customer?> = _loggedInPhone.combine(repository.allCustomers) { phone, customers ->
        if (phone == null) null
        else customers.find { it.phone == phone }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Data lists
    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVouchers: StateFlow<List<AirtimeVoucher>> = repository.allVouchers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<SalesTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val printerConfig: StateFlow<PrinterConfig?> = repository.printerConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalSalesAmount: StateFlow<Double?> = repository.totalSalesAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalDiscountsGiven: StateFlow<Double?> = repository.totalDiscountsGiven
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Bluetooth devices
    private val _pairedPrinters = MutableStateFlow<List<PrinterDevice>>(emptyList())
    val pairedPrinters: StateFlow<List<PrinterDevice>> = _pairedPrinters.asStateFlow()

    // Dialog & UI messaging states
    private val _uiMessage = MutableSharedFlow<UiMessage>()
    val uiMessage: SharedFlow<UiMessage> = _uiMessage.asSharedFlow()

    private val _latestReceipt = MutableStateFlow<Pair<SalesTransaction, AirtimeVoucher>?>(null)
    val latestReceipt: StateFlow<Pair<SalesTransaction, AirtimeVoucher>?> = _latestReceipt.asStateFlow()

    // Forget password modal result
    private val _resetPinResult = MutableStateFlow<String?>(null)
    val resetPinResult: StateFlow<String?> = _resetPinResult.asStateFlow()

    init {
        refreshBluetoothDevices()
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.AMHARIC) AppLanguage.ENGLISH else AppLanguage.AMHARIC
    }

    fun refreshBluetoothDevices() {
        viewModelScope.launch {
            _pairedPrinters.value = printerManager.getPairedDevices()
        }
    }

    // Login logic
    fun loginCustomer(phone: String, pin: String) {
        viewModelScope.launch {
            val cleanPhone = phone.trim()
            val cleanPin = pin.trim()

            if (cleanPhone.isEmpty() || cleanPin.isEmpty()) {
                _uiMessage.emit(UiMessage("መግቢያ ስህተት / Login Error", "እባክዎን ስልክ እና ፒን ያስገቡ (Please enter phone & PIN)", true))
                return@launch
            }

            val customer = repository.getCustomerByPhone(cleanPhone)
            if (customer == null) {
                _uiMessage.emit(UiMessage("ያልተመዘገበ / Not Found", "ይህ ስልክ ቁጥር አልተመዘገበም (Phone number not registered)", true))
                return@launch
            }

            if (customer.pin != cleanPin) {
                _uiMessage.emit(UiMessage("የተሳሳተ ፒን / Incorrect PIN", "የተሳሳተ የይለፍ ቃል ያስገቡ (Incorrect PIN entered)", true))
                return@launch
            }

            _loggedInPhone.value = cleanPhone
            _userRole.value = UserRole.CUSTOMER
            _uiMessage.emit(UiMessage("እንኳን ደህና መጡ / Welcome", "${customer.name} በመልካም ገብተዋል", false))
        }
    }

    fun loginAdmin(adminPin: String) {
        viewModelScope.launch {
            // Default Admin PIN is "0000" or "8888"
            if (adminPin.trim() == "0000" || adminPin.trim() == "8888" || adminPin.trim() == "1234") {
                _userRole.value = UserRole.ADMIN
                _uiMessage.emit(UiMessage("አስተዳዳሪ / Admin Access", "ወደ የአስተዳዳሪ ፓነል በመልካም ገብተዋል", false))
            } else {
                _uiMessage.emit(UiMessage("የተሳሳተ ፒን / Invalid PIN", "የአስተዳዳሪ ፒን 0000 ነው (Default Admin PIN: 0000)", true))
            }
        }
    }

    fun logout() {
        _loggedInPhone.value = null
        _userRole.value = UserRole.LOGGED_OUT
        _latestReceipt.value = null
    }

    // Forget password handler - Reset PIN keeping balance 100% intact!
    fun requestForgetPassword(phone: String) {
        viewModelScope.launch {
            if (phone.trim().isEmpty()) {
                _uiMessage.emit(UiMessage("ስህተት / Error", "እባክዎን ስልክ ቁጥር ያስገቡ", true))
                return@launch
            }
            val result = repository.resetCustomerPin(phone)
            result.onSuccess { newPin ->
                _resetPinResult.value = newPin
                _uiMessage.emit(
                    UiMessage(
                        "የይለፍ ቃል ተቀይሯል / PIN Reset Success",
                        "አዲሱ 4 አሃዝ ፒንዎ: $newPin (ቀሪ ሂሳብዎ አልተነካም!)",
                        false
                    )
                )
            }.onFailure { err ->
                _uiMessage.emit(UiMessage("ስህተት / Error", err.message ?: "PIN Reset failed", true))
            }
        }
    }

    fun clearResetPinResult() {
        _resetPinResult.value = null
    }

    // Customer Airtime Buy & Print
    fun purchaseAndPrintVoucher(denomination: Int) {
        viewModelScope.launch {
            val customer = activeCustomer.value
            if (customer == null) {
                _uiMessage.emit(UiMessage("ስህተት / Error", "እባክዎን አስቀድመው ይግቡ", true))
                return@launch
            }

            val result = repository.purchaseAndPrintVoucher(customer, denomination)
            result.onSuccess { (tx, voucher) ->
                _latestReceipt.value = Pair(tx, voucher)
                _uiMessage.emit(UiMessage("ስኬት / Success", "$denomination ብር ካርድ በተሳካ ሁኔታ ተገዝቶ ታትሟል!", false))

                // Attempt printing over Bluetooth if configured
                val config = printerConfig.value
                if (config != null && config.printerAddress.isNotEmpty()) {
                    printerManager.printReceiptOverBluetooth(
                        deviceAddress = config.printerAddress,
                        transaction = tx,
                        config = config,
                        customerName = customer.name
                    )
                }
            }.onFailure { err ->
                _uiMessage.emit(UiMessage("የግዢ ስህተት / Purchase Error", err.message ?: "Failed", true))
            }
        }
    }

    fun dismissReceipt() {
        _latestReceipt.value = null
    }

    // Admin Operations
    fun registerNewCustomer(name: String, phone: String, email: String, initialBalance: String, customPin: String?) {
        viewModelScope.launch {
            if (name.isBlank() || phone.isBlank()) {
                _uiMessage.emit(UiMessage("ስህተት / Error", "ስም እና ስልክ ቁጥር መሞላት አለባቸው", true))
                return@launch
            }

            val balanceVal = initialBalance.toDoubleOrNull() ?: 0.0
            val result = repository.registerCustomer(name, phone, email, balanceVal, customPin)

            result.onSuccess { (customer, pin) ->
                _uiMessage.emit(
                    UiMessage(
                        "ተመዝግቧል / Customer Registered",
                        "ደንበኛ ${customer.name} ተመዝግቧል! የተሰጠው ፒን: $pin",
                        false
                    )
                )
            }.onFailure { err ->
                _uiMessage.emit(UiMessage("የምዝገባ ስህተት / Registration Failed", err.message ?: "Error", true))
            }
        }
    }

    fun topUpCustomerBalance(customerId: Long, amountStr: String) {
        viewModelScope.launch {
            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                _uiMessage.emit(UiMessage("ስህተት / Error", "ትክክለኛ የብር መጠን ያስገቡ", true))
                return@launch
            }

            val result = repository.topUpCustomerBalance(customerId, amount)
            result.onSuccess {
                _uiMessage.emit(UiMessage("ስኬት / Success", "ቀሪ ሂሳብ በተሳካ ሁኔታ ተጨምሯል (+$amount ETB)", false))
            }.onFailure { err ->
                _uiMessage.emit(UiMessage("ስህተት / Error", err.message ?: "Failed", true))
            }
        }
    }

    fun batchAddStockVouchers(denomination: Int, countStr: String) {
        viewModelScope.launch {
            val count = countStr.toIntOrNull()
            if (count == null || count <= 0) {
                _uiMessage.emit(UiMessage("ስህተት / Error", "ትክክለኛ የካርድ ብዛት ያስገቡ", true))
                return@launch
            }
            val result = repository.batchAddVouchers(denomination, count)
            result.onSuccess { inserted ->
                _uiMessage.emit(UiMessage("ስኬት / Stock Updated", "$inserted ባለ $denomination ብር ካርዶች በስኬት ተጨምረዋል", false))
            }
        }
    }

    fun updatePrinterConfig(name: String, address: String, widthMm: Int, autoPrint: Boolean, agentName: String, agentCode: String) {
        viewModelScope.launch {
            val current = printerConfig.value ?: PrinterConfig()
            val updated = current.copy(
                printerName = name.ifBlank { "POS Thermal Printer" },
                printerAddress = address,
                paperWidthMm = widthMm,
                autoPrintEnabled = autoPrint,
                agentName = agentName.ifBlank { "Ethio Telebirr Express Agent" },
                agentCode = agentCode.ifBlank { "TB-AG0091" }
            )
            repository.updatePrinterConfig(updated)
            _uiMessage.emit(UiMessage("ፕሪንተር / Printer Saved", "የፕሪንተር መቼት ተቀምጧል", false))
        }
    }
}
