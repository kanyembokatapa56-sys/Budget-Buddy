package com.example.prog7313_p2_v2

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfilePageActivity : AppCompatActivity() {

    private lateinit var fullNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView
    private lateinit var profileImage: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference


    private lateinit var imgStartDebt: ImageView
    private lateinit var imgDebtFree: ImageView
    private lateinit var imgTransaction: ImageView
    private lateinit var imgStartSaving: ImageView
    private lateinit var imgFinishSaving: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page2)


        imgStartDebt = findViewById(R.id.imgStartDebt)
        imgDebtFree = findViewById(R.id.imgDebtFree)
        imgTransaction = findViewById(R.id.imgTransaction)
        imgStartSaving = findViewById(R.id.imgStartSaving)
        imgFinishSaving = findViewById(R.id.imgFinishSaving)


        fullNameText = findViewById(R.id.fullNameText)
        emailText = findViewById(R.id.emailText)
        phoneText = findViewById(R.id.phoneText)
        profileImage = findViewById(R.id.profileImage)

        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = database.child("users").child(uid)
        val userSetupRef = database.child("userSetup").child(uid)
        val transactionsRef = database.child("transactions").child(uid)


        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                fullNameText.text = snapshot.child("fullName").value.toString()
                emailText.text = "Email: ${snapshot.child("email").value}"
                phoneText.text = "Phone: ${snapshot.child("phone").value}"

                val base64Image = snapshot.child("profileImage").value?.toString()
                if (!base64Image.isNullOrEmpty()) {
                    try {
                        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        profileImage.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to load profile image", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show()
        }


        userSetupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(goalSnapshot: DataSnapshot) {
                val savingsGoalStr = goalSnapshot.child("savingsAmount").getValue(String::class.java) ?: "0"
                val debtGoalStr = goalSnapshot.child("debitGoal").getValue(String::class.java) ?: "0"

                val savingsGoal = savingsGoalStr.toDoubleOrNull() ?: 0.0
                val debtGoal = debtGoalStr.toDoubleOrNull() ?: 0.0


                userRef.get().addOnSuccessListener { userSnapshot ->
                    val currentSavings = userSnapshot.child("currentSavings").getValue(Double::class.java) ?: 0.0
                    val currentDebt = userSnapshot.child("currentDebt").getValue(Double::class.java) ?: 0.0


                    imgStartSaving.visibility = if (savingsGoal > 10000) View.VISIBLE else View.GONE
                    imgStartDebt.visibility = if (debtGoal > 50000) View.VISIBLE else View.GONE
                    imgFinishSaving.visibility = if (currentSavings > 300) View.VISIBLE else View.GONE
                    imgDebtFree.visibility = if (currentDebt <= 0) View.VISIBLE else View.GONE


                    transactionsRef.get().addOnSuccessListener { transSnapshot ->
                        imgTransaction.visibility = if (transSnapshot.exists() && transSnapshot.hasChildren()) View.VISIBLE else View.GONE
                    }.addOnFailureListener {
                        imgTransaction.visibility = View.GONE
                    }
                }.addOnFailureListener {

                    imgFinishSaving.visibility = View.GONE
                    imgDebtFree.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfilePageActivity", "Failed to listen to userSetup goals", error.toException())
            }
        })


        findViewById<Button?>(R.id.btnLogout)?.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<Button?>(R.id.btnSettings)?.setOnClickListener {
            startActivity(Intent(this, Settings_Page::class.java))
        }

        findViewById<ImageButton?>(R.id.ibHome)?.setOnClickListener {
            startActivity(Intent(this, Home_Page::class.java))
            finish()
        }

        findViewById<ImageButton?>(R.id.ibGraphs)?.setOnClickListener {
            startActivity(Intent(this, Graph_Page::class.java))
            finish()
        }

        findViewById<ImageButton?>(R.id.ibTransactions)?.setOnClickListener {
            startActivity(Intent(this, Transaction_History_Page::class.java))
            finish()
        }

        findViewById<ImageButton?>(R.id.ibSettings)?.setOnClickListener {
            startActivity(Intent(this, Budget_Settings_Page::class.java))
            finish()
        }
    }
}