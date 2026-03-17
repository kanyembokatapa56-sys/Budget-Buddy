package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp_Page : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_page)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")


        val termsConditions: TextView = findViewById(R.id.tvTermsConditions)
        val privacyPolicy: TextView = findViewById(R.id.tvPrivacyPolicy)
        val signUpButton: Button = findViewById(R.id.btnSignUp)
        val loginButton: Button = findViewById(R.id.btnLogin)

        val fullNameInput = findViewById<EditText>(R.id.etFullName)
        val phoneInput = findViewById<EditText>(R.id.etPhoneNumber)
        val emailInput = findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)


        termsConditions.setOnClickListener {
            startActivity(Intent(this, Terms_And_Conditions_Page::class.java))
            finish()
        }

        privacyPolicy.setOnClickListener {
            startActivity(Intent(this, Privacy_Policy_Page::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        signUpButton.setOnClickListener {
            val fullName = fullNameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || !cbTerms.isChecked) {
                if (fullName.isEmpty()) fullNameInput.error = "Please enter your full name"
                if (phone.isEmpty()) phoneInput.error = "Please enter your phone number"
                if (email.isEmpty()) emailInput.error = "Please enter your email address"
                if (password.isEmpty()) passwordInput.error = "Please enter your password"
                if (!cbTerms.isChecked) cbTerms.error = "Please accept the terms and conditions"

                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = firebaseAuth.currentUser?.uid
                            Log.d("SIGNUP", "User UID: $uid")

                            if (uid != null) {
                                val accomplishments = hashMapOf(
                                    "firstTransaction" to false,
                                    "startedSaving" to false,
                                    "gotTheSavings" to false,
                                    "debtFree" to false,
                                    "debtPrison" to false
                                )

                                val user = hashMapOf(
                                    "fullName" to fullName,
                                    "email" to email,
                                    "phone" to phone,
                                    "accomplishments" to accomplishments
                                )

                                database.child(uid).setValue(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Sign-up successful", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, NewUserSetup::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Error saving user info: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        Log.e("SIGNUP", "Database write failed", exception)
                                    }
                            }
                        } else {
                            Toast.makeText(this, task.exception?.message ?: "Sign-up failed", Toast.LENGTH_LONG).show()
                            Log.e("SIGNUP", "Signup failed", task.exception)
                        }
                    }
            }
        }
    }
}