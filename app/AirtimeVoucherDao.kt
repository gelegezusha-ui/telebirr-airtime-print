package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AirtimeVoucher
import kotlinx.coroutines.flow.Flow

@Dao
interface AirtimeVoucherDao {
    @Query("SELECT * FROM airtime_vouchers ORDER BY id DESC")
    fun getAllVouchers(): Flow<List<AirtimeVoucher>>

    @Query("SELECT * FROM airtime_vouchers WHERE denomination = :denomination AND isUsed = 0 LIMIT 1")
    suspend fun getAvailableVoucher(denomination: Int): AirtimeVoucher?

    @Query("SELECT COUNT(*) FROM airtime_vouchers WHERE denomination = :denomination AND isUsed = 0")
    fun getAvailableCount(denomination: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM airtime_vouchers WHERE isUsed = 0")
    fun getTotalAvailableCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVouchers(vouchers: List<AirtimeVoucher>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: AirtimeVoucher): Long

    @Update
    suspend fun updateVoucher(voucher: AirtimeVoucher)

    @Query("DELETE FROM airtime_vouchers WHERE isUsed = 1")
    suspend fun clearUsedVouchers()
}
