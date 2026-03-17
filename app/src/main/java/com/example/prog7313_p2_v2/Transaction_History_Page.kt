package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Transaction_History_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_history_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // finds the imageViews for navigation
        val homeImage: ImageButton = findViewById(R.id.ibHome)
        val graphImage: ImageButton = findViewById(R.id.ibGraphs)
        //val historyImage: ImageButton = findViewById(R.id.ibTransactions)
        val budgetSettingsImage: ImageButton = findViewById(R.id.ibSettings)
        val profileImage: ImageButton = findViewById(R.id.ibProfile)

        // sets the onClickListener for the imageView
        homeImage.setOnClickListener {
            // creates an intent to navigate to the home activity
            val intent = Intent(this, Home_Page::class.java)
            startActivity(intent)
            finish()
        }

        graphImage.setOnClickListener {
            // creates an intent to navigate to the history activity
            val intent = Intent(this, Graph_Page::class.java)
            startActivity(intent)
            finish()
        }

        budgetSettingsImage.setOnClickListener {
            // creates an intent to navigate to the settings activity
            val intent = Intent(this, Budget_Settings_Page::class.java)
            startActivity(intent)
            finish()
        }

        profileImage.setOnClickListener {
            // creates an intent to navigate to the profile activity
            val intent = Intent(this, ProfilePageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}