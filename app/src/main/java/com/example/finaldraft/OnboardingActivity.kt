package com.example.finaldraft

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var btnFinish: View // Reference to the Finish button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)  // Make sure this is the correct layout file

        // Initialize the Finish button
        btnFinish = findViewById(R.id.btnGetStarted)  // Ensure the ID matches the XML

        // Set up the button click listener to navigate to the main activity
        btnFinish.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)  // Transition to MainActivity
            startActivity(intent)
            finish()  // Finish the onboarding activity to prevent going back to it
        }
    }
}
