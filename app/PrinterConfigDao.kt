package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PrinterConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface PrinterConfigDao {
    @Query("SELECT * FROM printer_config WHERE id = 1 LIMIT 1")
    fun getPrinterConfig(): Flow<PrinterConfig?>

    @Query("SELECT * FROM printer_config WHERE id = 1 LIMIT 1")
    suspend fun getPrinterConfigSync(): PrinterConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePrinterConfig(config: PrinterConfig)
}
