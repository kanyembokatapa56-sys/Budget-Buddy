package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class NewUserSetup : AppCompatActivity() {

    private lateinit var spCurrency: Spinner
    private lateinit var spLanguage: Spinner
    private lateinit var etMinBudget: EditText
    private lateinit var etMaxBudget: EditText
    private lateinit var etDebtGoal: EditText
    private lateinit var etSavingsGoal: EditText
    private lateinit var etAvalable: EditText
    private lateinit var btnContinue: Button

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user_setup)


        auth = FirebaseAuth.getInstance()


        spCurrency = findViewById(R.id.spCurrency)
        spLanguage = findViewById(R.id.spLanguage)
        etMinBudget = findViewById(R.id.etMinBudget)
        etMaxBudget = findViewById(R.id.etMaxBudget)
        etDebtGoal = findViewById(R.id.etDebtGoal)
        etAvalable= findViewById(R.id.etAmount)
        etSavingsGoal = findViewById(R.id.etSavingsGoal)
        btnContinue = findViewById(R.id.btnContinue)

        setupSpinners()

        btnContinue.setOnClickListener {
            saveUserSetup()
        }
    }

    private fun setupSpinners() {

        val currencies = arrayOf("USD", "EUR", "ZAR")
        val languages = arrayOf(
            "English",
            "Spanish",
            "French",
            "German",
            "Chinese (Simplified)",
            "Chinese (Traditional)",
            "Japanese",
            "Korean",
            "Portuguese",
            "Russian",
            "Arabic",
            "Hindi",
            "Italian",
            "Dutch",
            "Turkish",
            "Swedish",
            "Polish",
            "Vietnamese",
            "Thai",
            "Indonesian"
        )

        spCurrency.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        spLanguage.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
    }

    private fun saveUserSetup() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val currency = spCurrency.selectedItem.toString()
        val language = spLanguage.selectedItem.toString()
        val minBudget = etMinBudget.text.toString()
        val maxBudget = etMaxBudget.text.toString()
        val debtGoal = etDebtGoal.text.toString()
        val savingsAmount = etSavingsGoal.text.toString()
        val availableAmount  = etAvalable.text.toString()


        val budgetedAmount = "0.00"


        if (minBudget.isBlank() || maxBudget.isBlank() || debtGoal.isBlank() || savingsAmount.isBlank()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val userSetup = UserSetup(
            language = language,
            currency = currency,
            minBudget = minBudget,
            maxBudget = maxBudget,
            budgetedAmount = budgetedAmount,
            availableAmount = availableAmount,
            savingsAmount = savingsAmount,
            debitGoal = debtGoal
        )


        val ref = database.reference.child("userSetup").child(user.uid)
        ref.setValue(userSetup).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Setup saved!", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, Home_Page::class.java))
                finish()
            } else {
                Toast.makeText(this, "Failed to save: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}