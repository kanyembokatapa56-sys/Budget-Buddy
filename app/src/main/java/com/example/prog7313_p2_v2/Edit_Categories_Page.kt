
package com.example.prog7313_p2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Edit_Categories_Page : AppCompatActivity() {

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var adapter: CategoryAdapter
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_categories_page)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val backButton: ImageButton = findViewById(R.id.ibExit)
        val addCategoryButton: Button = findViewById(R.id.btnAddCategory)
        categoryRecyclerView = findViewById(R.id.rvCategories)


        backButton.setOnClickListener {
            startActivity(Intent(this, Budget_Settings_Page::class.java))
            finish()
        }

        addCategoryButton.setOnClickListener {
            startActivity(Intent(this, Add_Category_Page::class.java))
            finish()
        }


        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryList = ArrayList()
        adapter = CategoryAdapter(categoryList)
        categoryRecyclerView.adapter = adapter


        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        dbRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .child("categories")


        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()
                for (categorySnap in snapshot.children) {
                    val category = categorySnap.getValue(Category::class.java)
                    if (category != null) {
                        categoryList.add(category)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@Edit_Categories_Page,
                    "Failed to load categories: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
