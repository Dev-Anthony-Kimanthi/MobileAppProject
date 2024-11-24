package com.example.finaldraft

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.finaldraft.databinding.ActivityAddTransactionBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private val transactionViewModel: TransactionViewModel by viewModels()

    private var selectedDate: Date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val types = listOf("Income", "Expense")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        binding.spinnerType.adapter = adapter

        binding.editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDate = calendar.time
                    binding.editTextDate.setText(selectedDate.toString())
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.buttonSave.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val amount = binding.editTextAmount.text.toString().toDouble()
            val type = binding.spinnerType.selectedItem.toString()

            val transaction = Transaction(0, name, amount, type, selectedDate)
            transactionViewModel.insert(transaction)
            finish()
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Handle Home tab click
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.nav_add_transactions -> {
                    // Handle Transactions tab click
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }

                R.id.nav_analytics -> {
                    // Handle Analytics tab click
                    startActivity(Intent(this, AnalyticsActivity::class.java))
                    true
                }

                else -> false
            }
        }

    }
}
