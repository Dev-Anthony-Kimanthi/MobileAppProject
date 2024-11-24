package com.example.finaldraft

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

@Database(entities = [Transaction::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop the old table
                database.execSQL("DROP TABLE IF EXISTS `transactions`")
                // Create the new table
                database.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `amount` REAL NOT NULL, `type` TEXT NOT NULL, `date` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        fun getDatabase(context: Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction_database"
                ).addMigrations(MIGRATION_1_2)
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(time: Long?): Date? = time?.let { Date(it) }
}