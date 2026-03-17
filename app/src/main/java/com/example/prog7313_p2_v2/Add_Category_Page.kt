
package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Add_Category_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_category_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinner: Spinner = findViewById(R.id.spColors)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.category_colors,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val backButton: ImageButton = findViewById(R.id.ibExit)
        val createCategory: Button = findViewById(R.id.btnCreateCategory)
        val etCategoryName: EditText = findViewById(R.id.etCategoryName)
        val etAmount: EditText = findViewById(R.id.etAmount)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val categoriesRef: DatabaseReference = database.getReference("users").child(uid).child("categories")

        backButton.setOnClickListener {
            startActivity(Intent(this, Edit_Categories_Page::class.java))
            finish()
        }

        createCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            val amount = etAmount.text.toString().trim()
            val color = spinner.selectedItem.toString()

            if (name.isEmpty() || amount.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoryId = categoriesRef.push().key
            val category = Category(name, amount, color)

            if (categoryId != null) {
                categoriesRef.child(categoryId).setValue(category)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Category saved", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Edit_Categories_Page::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save category", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
