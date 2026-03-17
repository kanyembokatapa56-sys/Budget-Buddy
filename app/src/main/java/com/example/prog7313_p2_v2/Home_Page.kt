package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

class Home_Page : AppCompatActivity() {

    private lateinit var tvLanguage: TextView
    private lateinit var tvCurrency: TextView
    private lateinit var tvMinBudget: TextView
    private lateinit var tvMaxBudget: TextView
    private lateinit var tvDebtGoal: TextView
    private lateinit var tvSavingsAmount: TextView
    private lateinit var tvAvailableAmount: TextView
    private lateinit var tvBudget: TextView

    private lateinit var rvTransactions: RecyclerView
    private val transactions = mutableListOf<Transaction>()
    private lateinit var adapter: TransactionsAdapter

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        tvLanguage = findViewById(R.id.tvLanguage)
        tvCurrency = findViewById(R.id.tvCurrency)
        tvMinBudget = findViewById(R.id.tvMinBudget)
        tvMaxBudget = findViewById(R.id.tvMaxBudget)
        tvDebtGoal = findViewById(R.id.tvDebtGoalAmount)
        tvSavingsAmount = findViewById(R.id.tvSavingsGoalAmount)
        tvAvailableAmount = findViewById(R.id.tvAvailableAmount)
        tvBudget = findViewById(R.id.tvBudget)


        rvTransactions = findViewById(R.id.rvTransactions)
        adapter = TransactionsAdapter(transactions)
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = adapter


        loadUserSetupFromFirebase()
        loadGoalsFromFirebase()
        loadTransactionsFromFirebase()


        val graphImage: ImageButton = findViewById(R.id.ibGraphs)
        val historyImage: ImageButton = findViewById(R.id.ibTransactions)
        val budgetSettingsImage: ImageButton = findViewById(R.id.ibSettings)
        val profileImage: ImageButton = findViewById(R.id.ibProfile)
        val addTransactionButton: Button = findViewById(R.id.btnAddTransaction)


        graphImage.setOnClickListener {
            startActivity(Intent(this, Graph_Page::class.java))
        }

        historyImage.setOnClickListener {
            startActivity(Intent(this, Transaction_History_Page::class.java))
        }

        budgetSettingsImage.setOnClickListener {
            startActivity(Intent(this, Budget_Settings_Page::class.java))
        }

        profileImage.setOnClickListener {
            startActivity(Intent(this, ProfilePageActivity::class.java))
        }

        addTransactionButton.setOnClickListener {
            startActivity(Intent(this, Add_Transaction_Page::class.java))
        }


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
                val language = snapshot.child("language").getValue(String::class.java) ?: "N/A"
                val currency = snapshot.child("currency").getValue(String::class.java) ?: "N/A"
                val minBudget = snapshot.child("minBudget").getValue(String::class.java) ?: "N/A"
                val maxBudget = snapshot.child("maxBudget").getValue(String::class.java) ?: "N/A"
                val debtGoal = snapshot.child("debitGoal").getValue(String::class.java) ?: "N/A"
                val savingsAmount = snapshot.child("savingsAmount").getValue(String::class.java) ?: "N/A"
                val availableAmount = snapshot.child("availableAmount").getValue(String::class.java) ?: "N/A"
                val budget = snapshot.child("budget").getValue(String::class.java) ?: maxBudget

                tvLanguage.text = "Language: $language"
                tvCurrency.text = "Currency: $currency"
                tvMinBudget.text = "Min Budget: $minBudget"
                tvMaxBudget.text = "Max Budget: $maxBudget"
                tvAvailableAmount.text = "$availableAmount"
                tvBudget.text = " $budget"
                tvDebtGoal.text = "Debt Goal: $debtGoal"
                tvSavingsAmount.text = "Savings Amount: $savingsAmount"
            } else {
                Toast.makeText(this, "No user setup found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load user data: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadGoalsFromFirebase() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            tvSavingsAmount.text = "R 0.00"
            tvDebtGoal.text = "R 0.00"
            return
        }

        val ref = database.reference.child("userSetup").child(uid)
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val savingsAmountStr = snapshot.child("savingsAmount").getValue(String::class.java)
                val debtGoalStr = snapshot.child("debitGoal").getValue(String::class.java)

                val savingsGoal = savingsAmountStr?.toDoubleOrNull() ?: 0.0
                val debtGoal = debtGoalStr?.toDoubleOrNull() ?: 0.0

                val format = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
                tvSavingsAmount.text = format.format(savingsGoal)
                tvDebtGoal.text = format.format(debtGoal)

                if (savingsGoal > 10000 && debtGoal > 50000) {
                    showCongratulationsPopup()
                }
            } else {
                tvSavingsAmount.text = "R 0.00"
                tvDebtGoal.text = "R 0.00"
            }
        }.addOnFailureListener {
            tvSavingsAmount.text = "R 0.00"
            tvDebtGoal.text = "R 0.00"
        }
    }

    private fun showCongratulationsPopup() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("🎉 Congratulations!")
        builder.setMessage("You've reached your goal of R50,000 and savings above R10,000! 🏆")
        builder.setPositiveButton("Awesome!") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun loadTransactionsFromFirebase() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = database.reference.child("transactions").child(userId)
        ref.get().addOnSuccessListener { snapshot ->
            transactions.clear()
            for (child in snapshot.children) {
                val transaction = child.getValue(Transaction::class.java)
                transaction?.let { transactions.add(it) }
            }
            adapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load transactions: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableEdgeToEdge() {

    }
}