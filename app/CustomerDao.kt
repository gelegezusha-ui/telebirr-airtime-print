package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY createdTimestamp DESC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE phone = :phone LIMIT 1")
    suspend fun getCustomerByPhone(phone: String): Customer?

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Long): Customer?

    @Query("SELECT * FROM customers WHERE phone = :phone LIMIT 1")
    fun getCustomerByPhoneFlow(phone: String): Flow<Customer?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("UPDATE customers SET balance = balance + :amount WHERE id = :customerId")
    suspend fun topUpBalance(customerId: Long, amount: Double)

    @Query("UPDATE customers SET balance = balance - :amount WHERE id = :customerId AND balance >= :amount")
    suspend fun deductBalance(customerId: Long, amount: Double): Int

    @Query("UPDATE customers SET pin = :newPin WHERE phone = :phone")
    suspend fun resetCustomerPin(phone: String, newPin: String): Int

    @Query("DELETE FROM customers WHERE id = :customerId")
    suspend fun deleteCustomer(customerId: Long)
}
