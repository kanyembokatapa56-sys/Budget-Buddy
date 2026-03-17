package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Budget_Report_Date_Select_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget_report_date_select_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val exitButton: ImageButton = findViewById(R.id.ibExit)


        val createReportButton: Button = findViewById(R.id.btnCreateReport)


        exitButton.setOnClickListener {
            val intent = Intent(this, Settings_Page::class.java)
            startActivity(intent)
            finish()
        }


        createReportButton.setOnClickListener {
            val intent = Intent(this, Budget_Report_Page::class.java)
            startActivity(intent)
            finish()
        }
    }
}