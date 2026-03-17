package com.example.prog7313_p2_v2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class Edit_Profile_Info_Page : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var saveChanges: Button
    private lateinit var exitButton: ImageButton
    private lateinit var selectImageBtn: Button
    private lateinit var profileImageView: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private var selectedImageBase64: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri ->
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    profileImageView.setImageBitmap(bitmap)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                    val imageBytes = baos.toByteArray()
                    selectedImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_info_page)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmailAddress)
        etPhone = findViewById(R.id.etPhoneNumber)
        etPassword = findViewById(R.id.etPassword)
        saveChanges = findViewById(R.id.btnSaveProfileInfo)
        exitButton = findViewById(R.id.ibExit)
        selectImageBtn = findViewById(R.id.btnSelectImage)
        profileImageView = findViewById(R.id.ivProfilePreview)

        exitButton.setOnClickListener {
            startActivity(Intent(this, Settings_Page::class.java))
            finish()
        }

        selectImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            pickImageLauncher.launch(intent)
        }

        saveChanges.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun updateUserProfile() {
        val fullName = etFullName.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()

        val user = auth.currentUser
        val uid = user?.uid

        if (user == null || uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required to update email", Toast.LENGTH_SHORT).show()
            return
        }

        val currentEmail = user.email ?: run {
            Toast.makeText(this, "Current email not available", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(currentEmail, password)

        user.reauthenticate(credential)
            .addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    user.updateEmail(newEmail)
                        .addOnCompleteListener { emailUpdateTask ->
                            if (emailUpdateTask.isSuccessful) {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build()

                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener {
                                        updateDatabase(uid, fullName, newEmail, phone)
                                    }

                                if (password.isNotEmpty()) {
                                    user.updatePassword(password)
                                }

                            } else {
                                Toast.makeText(this, "Failed to update email: ${emailUpdateTask.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Re-authentication failed: ${reAuthTask.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateDatabase(uid: String, fullName: String, email: String, phone: String) {
        val updatedUser = mutableMapOf<String, Any>(
            "fullName" to fullName,
            "email" to email,
            "phone" to phone
        )

        selectedImageBase64?.let {
            updatedUser["profileImage"] = it
        }

        database.child("users").child(uid).updateChildren(updatedUser)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile info updated", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, ProfilePageActivity::class.java))
                    finish()
                }, 400)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update database", Toast.LENGTH_SHORT).show()
            }
    }
}








