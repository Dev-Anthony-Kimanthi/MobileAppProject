package com.example.finaldraft

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.finaldraft.databinding.ActivityAnalyticsBinding
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.google.android.material.bottomnavigation.BottomNavigationView

class AnalyticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalyticsBinding
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        transactionViewModel.allTransactions.observe(this) { transactions ->
            val income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
            val expense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }

            val entries = listOf(
                PieEntry(income.toFloat(), "Income"),
                PieEntry(expense.toFloat(), "Expense")
            )

            val dataSet = PieDataSet(entries, "Transactions")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            val data = PieData(dataSet)

            binding.pieChart.data = data
            binding.pieChart.invalidate()
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