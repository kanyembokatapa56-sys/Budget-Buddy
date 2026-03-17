package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

class Edit_Goals_Page : AppCompatActivity() {

    private lateinit var savingsCurrentTextView: TextView
    private lateinit var debtCurrentTextView: TextView
    private lateinit var savingsEditText: EditText
    private lateinit var debtEditText: EditText
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_goals_page)


        savingsCurrentTextView = findViewById(R.id.saving)
        debtCurrentTextView = findViewById(R.id.Debt)
        savingsEditText = findViewById(R.id.editTextText3)
        debtEditText = findViewById(R.id.editTextText4)
        val exitButton: ImageButton = findViewById(R.id.ibExit)
        val setNewGoalsButton: Button = findViewById(R.id.btnSetNewGoals)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadCurrentGoals()

        exitButton.setOnClickListener {
            startActivity(Intent(this, Settings_Page::class.java))
            finish()
        }

        setNewGoalsButton.setOnClickListener {
            saveNewGoals()
        }
    }

    private fun loadCurrentGoals() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val userSetupRef = database.reference.child("userSetup").child(uid)
            userSetupRef.get().addOnSuccessListener { snapshot ->
                val savingsAmountStr = snapshot.child("savingsAmount").getValue(String::class.java) ?: "0"
                val debitGoalStr = snapshot.child("debitGoal").getValue(String::class.java) ?: "0"

                val savingsAmount = savingsAmountStr.toDoubleOrNull() ?: 0.0
                val debitGoal = debitGoalStr.toDoubleOrNull() ?: 0.0

                val format = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
                savingsCurrentTextView.text = format.format(savingsAmount)
                debtCurrentTextView.text = format.format(debitGoal)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load saved goals", Toast.LENGTH_SHORT).show()
                savingsCurrentTextView.text = "R 0.00"
                debtCurrentTextView.text = "R 0.00"
            }
        } else {
            savingsCurrentTextView.text = "R 0.00"
            debtCurrentTextView.text = "R 0.00"
        }
    }

    private fun saveNewGoals() {
        val savingsGoalStr = savingsEditText.text.toString().trim()
        val debtGoalStr = debtEditText.text.toString().trim()

        if (savingsGoalStr.isEmpty() || debtGoalStr.isEmpty()) {
            Toast.makeText(this, "Please enter both goal amounts", Toast.LENGTH_SHORT).show()
            return
        }

        val savingsGoal = savingsGoalStr.toDoubleOrNull()
        val debtGoal = debtGoalStr.toDoubleOrNull()

        if (savingsGoal == null || debtGoal == null) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Goals not saved (user not logged in)", Toast.LENGTH_SHORT).show()
            return
        }

        val userSetupRef = database.reference.child("userSetup").child(uid)
        val updatedValues = mapOf(
            "savingsAmount" to savingsGoal.toString(),
            "debitGoal" to debtGoal.toString()
        )

        userSetupRef.updateChildren(updatedValues).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "New goals saved", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, Home_Page::class.java))
                    finish()
                }, 400)
            } else {
                Toast.makeText(this, "Failed to save goals", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableEdgeToEdge() {

    }
}