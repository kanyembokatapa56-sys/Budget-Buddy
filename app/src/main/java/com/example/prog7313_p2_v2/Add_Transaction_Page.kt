package com.example.prog7313_p2_v2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Add_Transaction_Page : AppCompatActivity() {

    private lateinit var imgAddPhoto: ImageView
    private lateinit var saveButton: Button
    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextDay: EditText
    private lateinit var editTextMonth: EditText
    private lateinit var editTextYear: EditText
    private lateinit var editTextAmount: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var spinnerTransactionType: Spinner
    private lateinit var spinnerSubCategory: Spinner

    private val pickImageLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
        uri?.let {
            imgAddPhoto.setImageURI(uri)
        }
    }

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_transaction_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinnerTransactionType = findViewById(R.id.spinnerTransactionType)
        spinnerSubCategory = findViewById(R.id.spinnerSubCategory)


        val transactionTypes = listOf("Income", "Expense")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, transactionTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTransactionType.adapter = typeAdapter


        spinnerTransactionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = parent.getItemAtPosition(position).toString()
                val subcategories = if (selectedType == "Income") {
                    listOf("Salary", "Gift", "Bonus", "Interest")
                } else {
                    listOf("Food", "Transport", "Shopping", "Utilities", "Entertainment")
                }
                val subAdapter = ArrayAdapter(this@Add_Transaction_Page, android.R.layout.simple_spinner_item, subcategories)
                subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSubCategory.adapter = subAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerCategory = findViewById(R.id.spCategorySelect)
        editTextDay = findViewById(R.id.editTextNumberSigned)
        editTextMonth = findViewById(R.id.editTextNumberSigned2)
        editTextYear = findViewById(R.id.editTextNumberSigned3)
        editTextAmount = findViewById(R.id.editTextNumberDecimal2)
        editTextNotes = findViewById(R.id.editTextTextMultiLine)
        saveButton = findViewById(R.id.btnAddTransaction)
        imgAddPhoto = findViewById(R.id.imgAddPhoto)


        val categories = listOf("Food", "Transport", "Shopping", "Utilities", "Entertainment")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        findViewById<ImageView>(R.id.imgExitButton).setOnClickListener {
            startActivity(Intent(this, Home_Page::class.java))
            finish()
        }

        saveButton.setOnClickListener {
            saveTransaction()
        }

        imgAddPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun saveTransaction() {
        val type = spinnerTransactionType.selectedItem?.toString() ?: ""
        val category = spinnerSubCategory.selectedItem?.toString() ?: ""
        val day = editTextDay.text.toString().padStart(2, '0')
        val month = editTextMonth.text.toString().padStart(2, '0')
        val year = editTextYear.text.toString()
        val amountText = editTextAmount.text.toString()
        val notes = editTextNotes.text.toString()
        val date = "$day/$month/$year"

        if (day.isNotEmpty() && month.isNotEmpty() && year.isNotEmpty() &&
            amountText.isNotEmpty() && category.isNotEmpty() && type.isNotEmpty()) {

            val amount = amountText.toDoubleOrNull()
            if (amount != null) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val transaction = Transaction(date, amount, category, notes)
                    val transactionsRef = database.reference.child("transactions").child(userId)
                    val userSetupRef = database.reference.child("userSetup").child(userId)

                    val transactionId = transactionsRef.push().key
                    if (transactionId != null) {
                        transactionsRef.child(transactionId).setValue(transaction)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Transaction added successfully!", Toast.LENGTH_SHORT).show()


                                updateAvailableAmount(userSetupRef, amount, type)


                                startActivity(Intent(this, Home_Page::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add transaction: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Error generating transaction ID", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAvailableAmount(userSetupRef: DatabaseReference, amount: Double, type: String) {

        val adjustedAmount = if (type == "Income") amount else -amount


        userSetupRef.child("availableAmount").get().addOnSuccessListener { snapshot ->
            val currentAmountStr = snapshot.getValue(String::class.java) ?: "0"
            val currentAmount = currentAmountStr.toDoubleOrNull() ?: 0.0
            val newAmount = currentAmount + adjustedAmount
            userSetupRef.child("availableAmount").setValue(newAmount.toString())
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update available amount", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to retrieve current amount", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableEdgeToEdge() {

    }
}