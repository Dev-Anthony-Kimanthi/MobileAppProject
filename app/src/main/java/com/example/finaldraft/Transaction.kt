package com.example.finaldraft

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "transactions") // Change table name to "transactions"
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val amount: Double,
    val type: String,
    val date: Date
)
