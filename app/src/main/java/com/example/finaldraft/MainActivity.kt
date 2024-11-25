package com.example.finaldraft

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finaldraft.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val transactionViewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val expensesAdapter = TransactionAdapter()
        val incomesAdapter = TransactionAdapter()

        binding.recyclerViewExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewExpenses.adapter = expensesAdapter

        binding.recyclerViewIncomes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewIncomes.adapter = incomesAdapter

        transactionViewModel.allTransactions.observe(this) { transactions ->
            val expenses = transactions.filter { it.type == "Expense" }
            val incomes = transactions.filter { it.type == "Income" }

            expensesAdapter.submitList(expenses)
            incomesAdapter.submitList(incomes)
            updateBalance(transactions) // Now works because updateBalance is in the right scope
        }

        //binding.buttonAddTransaction.setOnClickListener {
        //    startActivity(Intent(this, AddTransactionActivity::class.java))
        //}

        //binding.buttonViewAnalytics.setOnClickListener {
        //    startActivity(Intent(this, AnalyticsActivity::class.java))
        //}

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

                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    // Move updateBalance function outside onCreate() method to be accessible globally in the class
    private fun updateBalance(transactions: List<Transaction>) {
        val income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
        val balance = income - expense
        binding.textViewTotalBalance.text = "Balance: ${income - expense}"

        if (balance < 0){
            showBalanceNotification()
        }
    }
    private fun showBalanceNotification(){
        //check for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above, check for POST_NOTIFICATIONS permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.POST_NOTIFICATIONS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission()
                return
            }
        }

        val channelId = "low_balance_channel"
        val notificationId = 1

        //creating notifictation channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = "Low Balance Warning"
            val descriptionText = "Notification about low account balance"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        //creating the notification
        val builder =  NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle("Low Balance Warning")
            .setContentText("You might want to be careful on your expenses.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        //showing the notification
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }
    private fun requestNotificationPermission(){
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
                if(isGranted){
                    //shows notification if permission is granted
                    showBalanceNotification()
                }
                else{
                    //
                }
            }
        requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
    }
}
