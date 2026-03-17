package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Budget_Settings_Page : AppCompatActivity() {

    private lateinit var tvBudgetAmount: TextView
    private lateinit var tvSavingsGoalAmount: TextView
    private lateinit var tvDebtGoalAmount: TextView

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget_settings_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvDebtGoalAmount = findViewById(R.id.tvDebtGoalAmount)
        tvSavingsGoalAmount = findViewById(R.id.tvSavingsGoalAmount)
        tvBudgetAmount = findViewById(R.id.tvBudgetAmount)

     
        val homeImage: ImageButton = findViewById(R.id.ibHome)
        val graphImage: ImageButton = findViewById(R.id.ibGraphs)
        val historyImage: ImageButton = findViewById(R.id.ibTransactions)
        val profileImage: ImageButton = findViewById(R.id.ibProfile)

        val btnEditCategories: Button = findViewById(R.id.btnEditCategories)

        homeImage.setOnClickListener {
            startActivity(Intent(this, Home_Page::class.java))
            finish()
        }

        graphImage.setOnClickListener {
            startActivity(Intent(this, Graph_Page::class.java))
            finish()
        }

        historyImage.setOnClickListener {
            startActivity(Intent(this, Transaction_History_Page::class.java))
            finish()
        }

        profileImage.setOnClickListener {
            startActivity(Intent(this, ProfilePageActivity::class.java))
            finish()
        }

        btnEditCategories.setOnClickListener {
            startActivity(Intent(this, Edit_Categories_Page::class.java))
            finish()
        }

        loadUserSetupFromFirebase()
    }

    private fun loadUserSetupFromFirebase() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = database.reference.child("userSetup").child(user.uid)
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val debtGoal = snapshot.child("debitGoal").getValue(String::class.java) ?: "N/A"
                val savingsAmount = snapshot.child("savingsAmount").getValue(String::class.java) ?: "N/A"
                val maxBudget = snapshot.child("maxBudget").getValue(String::class.java) ?: "N/A"

                tvDebtGoalAmount.text = " $debtGoal"
                tvSavingsGoalAmount.text = " $savingsAmount"
                tvBudgetAmount.text = "$maxBudget"
            } else {
                Toast.makeText(this, "No user setup found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load data: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}
