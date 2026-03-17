package com.example.prog7313_p2_v2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*
class Graph_Page : AppCompatActivity() {

    private lateinit var incomePieChart: PieChart
    private lateinit var expensePieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var savingsLineChart: LineChart
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var btnGeneratePDF: Button


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            generatePdf()
        } else {
            Toast.makeText(this, "Storage permission denied. Cannot save PDF.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_graph_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        incomePieChart = findViewById(R.id.incomePieChart)
        expensePieChart = findViewById(R.id.expensePieChart)
        barChart = findViewById(R.id.mixedBarChart)
        savingsLineChart = findViewById(R.id.savingsLineChart)

        setupCharts()


        val homeImage: ImageButton = findViewById(R.id.ibHome)
        val historyImage: ImageButton = findViewById(R.id.ibTransactions)
        val budgetSettingsImage: ImageButton = findViewById(R.id.ibSettings)
        val profileImage: ImageButton = findViewById(R.id.ibProfile)

        homeImage.setOnClickListener {
            startActivity(Intent(this, Home_Page::class.java))
            finish()
        }

        historyImage.setOnClickListener {
            startActivity(Intent(this, Transaction_History_Page::class.java))
            finish()
        }

        budgetSettingsImage.setOnClickListener {
            startActivity(Intent(this, Budget_Settings_Page::class.java))
            finish()
        }

        profileImage.setOnClickListener {
            startActivity(Intent(this, ProfilePageActivity::class.java))
            finish()
        }

        // PDF Generation Button
        btnGeneratePDF = findViewById(R.id.btnGeneratePDF)
        btnGeneratePDF.setOnClickListener {
            checkPermissionAndGeneratePdf()
        }
    }

    private fun setupCharts() {
        val user = auth.currentUser

        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = database.reference.child("transactions").child(user.uid)
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                var salaryTotal = 0.0 // Initialize a variable to store the sum
                var giftTotal = 0.0
                var bonusTotal = 0.0
                var interestTotal = 0.0
                var foodTotal = 0.0
                var transportTotal = 0.0
                var shoppingTotal = 0.0
                var utilitiesTotal = 0.0
                var entertainmentTotal = 0.0
                // Iterate through the transactions
                for (transactionSnapshot in snapshot.children) {
                    // Assuming your transaction data has a "category" and "amount" field
                    val category = transactionSnapshot.child("category").getValue(String::class.java)
                    val amount = transactionSnapshot.child("amount").getValue(Double::class.java)

                    // Check if the category matches "salary" and if the amount is not null
                    if (category == "Salary" && amount != null) {
                        salaryTotal += amount // Add the amount to the total
                    }

                    if (category == "Gift" && amount != null) {
                        giftTotal += amount // Add the amount to the total
                    }

                    if (category == "Bonus" && amount != null) {
                        bonusTotal += amount // Add the amount to the total
                    }

                    if (category == "Interest" && amount != null) {
                        interestTotal += amount // Add the amount to the total
                    }

                    if (category == "Food" && amount != null) {
                        foodTotal += amount // Add the amount to the total
                    }

                    if (category == "Transport" && amount != null) {
                        transportTotal += amount // Add the amount to the total
                    }

                    if (category == "Shopping" && amount != null) {
                        shoppingTotal += amount // Add the amount to the total
                    }

                    if (category == "Utilities" && amount != null) {
                        utilitiesTotal += amount // Add the amount to the total
                    }

                    if (category == "Entertainment" && amount != null) {
                        entertainmentTotal += amount // Add the amount to the total
                    }

                    val incomeEntries = listOf(
                        PieEntry(salaryTotal.toFloat(), "Salary"),
                        PieEntry(giftTotal.toFloat(), "Gift"),
                        PieEntry(bonusTotal.toFloat(), "Bonus"),
                        PieEntry(interestTotal.toFloat(), "Interest")

                    )

                    val incomeDataSet = PieDataSet(incomeEntries, "Income Distribution").apply {
                        colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
                    }
                    incomePieChart.data = PieData(incomeDataSet)
                    incomePieChart.description.isEnabled = false
                    incomePieChart.setEntryLabelColor(Color.BLACK)
                    incomePieChart.setUsePercentValues(true)
                    incomePieChart.invalidate()


                }

                // Expense Pie Chart
                val expenseEntries = listOf(
                    PieEntry(foodTotal.toFloat(), "Food"),
                    PieEntry(shoppingTotal.toFloat(), "Shopping"),
                    PieEntry(utilitiesTotal.toFloat(), "Utilities"),
                    PieEntry(transportTotal.toFloat(), "Transport"),
                    PieEntry(entertainmentTotal.toFloat(), "Entertainment")
                )
                val expenseDataSet = PieDataSet(expenseEntries, "Expense Distribution").apply {
                    colors = listOf(Color.MAGENTA, Color.CYAN, Color.LTGRAY, Color.DKGRAY, Color.GREEN)
                }
                expensePieChart.data = PieData(expenseDataSet)
                expensePieChart.description.isEnabled = false
                expensePieChart.setUsePercentValues(true)
                expensePieChart.setEntryLabelColor(Color.BLACK)
                expensePieChart.invalidate()



                val incomeVals = listOf(giftTotal.toFloat(), salaryTotal.toFloat(), bonusTotal.toFloat(),interestTotal.toFloat())
                val expenseVals = listOf(foodTotal.toFloat(), transportTotal.toFloat(), utilitiesTotal.toFloat(), shoppingTotal.toFloat(),entertainmentTotal.toFloat())

                val incomeEntriesBar = incomeVals.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
                val expenseEntriesBar = expenseVals.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }

                val incomeSet = BarDataSet(incomeEntriesBar, "Income").apply { color = Color.GREEN }
                val expenseSet = BarDataSet(expenseEntriesBar, "Expenses").apply { color = Color.RED }

                val barData = BarData(incomeSet, expenseSet)
                barChart.data = barData
                barChart.groupBars(0f, 0.3f, 0.05f)
                barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                barChart.axisRight.isEnabled = false
                barChart.description.isEnabled = false
                barChart.invalidate()

                //line graph logic
                val savingsVals = incomeVals.zip(expenseVals) { income, expense -> income - expense }
                val savingsEntries = savingsVals.mapIndexed { i, v -> Entry(i.toFloat(), v) }
                val savingsSet = LineDataSet(savingsEntries, "Savings Trend").apply {
                    color = Color.BLUE
                    setCircleColor(Color.BLUE)
                    valueTextColor = Color.BLACK
                    lineWidth = 2f
                }
                savingsLineChart.data = LineData(savingsSet)
                savingsLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                savingsLineChart.axisRight.isEnabled = false
                savingsLineChart.description.isEnabled = false
                savingsLineChart.invalidate()

            } else {
                Toast.makeText(this, "No user setup found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load user data: ${it.message}", Toast.LENGTH_LONG).show()
        }






        

    }

    private fun checkPermissionAndGeneratePdf() {
       
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            generatePdf()
        } else {
            
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                generatePdf()
            } else {
              
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun generatePdf() {
      
        val pageWidth = 595
        val pageHeight = 842

        val pdfDocument = PdfDocument()

        val charts = listOf(incomePieChart, expensePieChart, barChart, savingsLineChart)

        charts.forEachIndexed { index, chart ->
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)

            val bitmap = getBitmapFromChart(chart)
            val scale = Math.min(pageWidth.toFloat() / bitmap.width, pageHeight.toFloat() / bitmap.height)

            val canvas = page.canvas
            canvas.save()
            canvas.scale(scale, scale)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.restore()

            pdfDocument.finishPage(page)
        }

      
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val file = File(downloadsDir, "GraphsReport_${System.currentTimeMillis()}.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF saved to Downloads folder: ${file.name}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }

    private fun getBitmapFromChart(chart: com.github.mikephil.charting.charts.Chart<*>): Bitmap {
  
        val width = if (chart.width > 0) chart.width else 800
        val height = if (chart.height > 0) chart.height else 600

        
        chart.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(width, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(height, android.view.View.MeasureSpec.EXACTLY)
        )
        chart.layout(0, 0, chart.measuredWidth, chart.measuredHeight)

        val bitmap = Bitmap.createBitmap(chart.measuredWidth, chart.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        chart.draw(canvas)
        return bitmap
    }
}
