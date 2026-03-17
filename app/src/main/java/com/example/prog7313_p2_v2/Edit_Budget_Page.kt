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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Edit_Budget_Page : AppCompatActivity() {

    private lateinit var etNewBudgetAmount: EditText
    private lateinit var tvCurrentBudget: TextView

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_budget_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvCurrentBudget = findViewById(R.id.tvCurrentBudget)
        etNewBudgetAmount = findViewById(R.id.edNewBudgetAmount)

        val exitButton: ImageButton = findViewById(R.id.ibExit)
        val setNewBudget: Button = findViewById(R.id.btnSetNewBudget)

        exitButton.setOnClickListener {
            startActivity(Intent(this, Settings_Page::class.java))
            finish()
        }

        setNewBudget.setOnClickListener {
            val newBudgetStr = etNewBudgetAmount.text.toString().trim()

            if (newBudgetStr.isEmpty()) {
                Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val newBudget = newBudgetStr.toDoubleOrNull()
            if (newBudget == null) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                val ref = database.reference.child("userSetup").child(user.uid).child("maxBudget")
                ref.setValue(newBudgetStr).addOnSuccessListener {
                    Toast.makeText(this, "New budget saved", Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed({

                        startActivity(Intent(this, Settings_Page::class.java))
                        finish()
                    }, 400)
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to save budget", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        loadCurrentBudget()
    }

    private fun loadCurrentBudget() {
        val user = auth.currentUser ?: run {
            tvCurrentBudget.text = "R 0.00"
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = database.reference.child("userSetup").child(user.uid).child("maxBudget")
        ref.get().addOnSuccessListener { snapshot ->
            val budgetValue = snapshot.getValue(String::class.java) ?: "0.00"
            tvCurrentBudget.text = "R $budgetValue"
        }.addOnFailureListener {
            tvCurrentBudget.text = "R 0.00"
            Toast.makeText(this, "Failed to load current budget", Toast.LENGTH_SHORT).show()
        }
    }
}