package com.example.finaldraft

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.finaldraft.databinding.ActivityAnalyticsBinding
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView

class AnalyticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalyticsBinding
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // Observe transactions and update the PieChart
        transactionViewModel.allTransactions.observe(this) { transactions ->
            if (transactions.isNotEmpty()) {
                updatePieChart(transactions)
            } else {
                binding.pieChart.clear()
                binding.pieChart.centerText = "No data available"
            }
        }

        // Set up bottom navigation
        setupBottomNavigation()

        // Add interactivity to the PieChart
        setupPieChartInteractivity()
    }

    private fun updatePieChart(transactions: List<Transaction>) {
        val income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }

        val entries = listOf(
            PieEntry(income.toFloat(), "Income"),
            PieEntry(expense.toFloat(), "Expense")
        )

        val dataSet = PieDataSet(entries, "Transactions").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
        }

        val data = PieData(dataSet).apply {
            setValueTextSize(14f)
            setValueTextColor(android.graphics.Color.BLACK)
        }

        binding.pieChart.apply {
            this.data = data
            setCenterText("Transaction Summary")
            setCenterTextSize(16f)
            description.isEnabled = false
            isRotationEnabled = true
            setEntryLabelTextSize(12f)
            animateY(1000)
            invalidate() // Refresh the chart
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_add_transactions -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                R.id.nav_analytics -> {
                    // Already on this screen
                    true
                }

                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun setupPieChartInteractivity() {
        binding.pieChart.setOnChartValueSelectedListener(object : com.github.mikephil.charting.listener.OnChartValueSelectedListener {
            override fun onValueSelected(e: com.github.mikephil.charting.data.Entry?, h: com.github.mikephil.charting.highlight.Highlight?) {
                if (e is PieEntry) {
                    val type = e.label
                    val amount = e.value
                    Toast.makeText(this@AnalyticsActivity, "$type: $amount", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected() {
                Toast.makeText(this@AnalyticsActivity, "No slice selected", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
