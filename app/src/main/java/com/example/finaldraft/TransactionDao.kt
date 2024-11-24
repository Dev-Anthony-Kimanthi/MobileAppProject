package com.example.finaldraft

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC") // Updated query to use "transactions"
    fun getAllTransactions(): LiveData<List<Transaction>>
}
