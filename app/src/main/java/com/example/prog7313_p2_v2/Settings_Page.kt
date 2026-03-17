package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Settings_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val exitButton: ImageButton = findViewById(R.id.ibExit)


        val currencyButton: Button = findViewById(R.id.btnCurrency)//
        val languageButton: Button = findViewById(R.id.btnLanguage)//
        val budgetReportButton: Button = findViewById(R.id.btnBudgetReport)//
        val editBudgetButton: Button = findViewById(R.id.btnEditBudget)
        val editCategoriesButton: Button = findViewById(R.id.btnEditCategories)
        val editGoalsButton: Button = findViewById(R.id.btnEditGoals)
        val editProfileInfoButton: Button = findViewById(R.id.btnEditProfileInfo)


        exitButton.setOnClickListener {
            val intent = Intent(this, ProfilePageActivity::class.java)
            startActivity(intent)
            finish()
        }


        currencyButton.setOnClickListener {

            val intent = Intent(this, Change_Currency_Page::class.java)
            startActivity(intent)
            finish()
        }


        languageButton.setOnClickListener {

            val intent = Intent(this, Change_Language_Page::class.java)
            startActivity(intent)
            finish()
        }


        budgetReportButton.setOnClickListener {

            val intent = Intent(this, Budget_Report_Date_Select_Page::class.java)
            startActivity(intent)
            finish()
        }


        editBudgetButton.setOnClickListener {

            val intent = Intent(this, Edit_Budget_Page::class.java)
            startActivity(intent)
            finish()
        }


        editCategoriesButton.setOnClickListener {

            val intent = Intent(this, Edit_Categories_Page::class.java)
            startActivity(intent)
            finish()
        }


        editGoalsButton.setOnClickListener {

            val intent = Intent(this, Edit_Goals_Page::class.java)
            startActivity(intent)
            finish()
        }


        editProfileInfoButton.setOnClickListener {

            val intent = Intent(this, Edit_Profile_Info_Page::class.java)
            startActivity(intent)
            finish()
        }
    }
}