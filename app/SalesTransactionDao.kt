package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SalesTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesTransactionDao {
    @Query("SELECT * FROM sales_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<SalesTransaction>>

    @Query("SELECT * FROM sales_transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsByCustomer(customerId: Long): Flow<List<SalesTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: SalesTransaction): Long

    @Query("SELECT SUM(netPrice) FROM sales_transactions")
    fun getTotalSalesAmount(): Flow<Double?>

    @Query("SELECT SUM(discountAmount) FROM sales_transactions")
    fun getTotalDiscountsGiven(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM sales_transactions")
    fun getTotalTransactionsCount(): Flow<Int>
}
