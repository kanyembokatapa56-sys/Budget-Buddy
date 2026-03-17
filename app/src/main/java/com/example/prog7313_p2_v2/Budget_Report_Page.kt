package com.example.prog7313_p2_v2

import android.content.ContentValues
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import java.io.IOException

class Budget_Report_Page : ComponentActivity() {

    private val reportContent: String = """
        BUDGET REPORT
        =============
        
        Reporting Period: 2025-04-01 to 2025-04-30
        
        Report Generated On: 2025-05-01 10:00:00
        
        --- Income Details ---
        Salary: R5000 on 2025-08-06
        Freelance Project: R1500 on 2025-04-20
        
        Total Income: R6500
        
        --- Expense Details ---
        Rent: R1200 on 2025-04-03
        Groceries: R500 on 2025-04-10
        Transport: R300 on 2025-04-15
        Entertainment: R200 on 2025-04-22
        
        Total Expenses: R2200
        
        --- Summary ---
        Total Savings: R4300
        Number of Transactions: 6
        
        Thank you for using Budget Buddy!
    """.trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_report_page)

        val exitButton: ImageButton = findViewById(R.id.ibExit)
        val exportButton: Button = findViewById(R.id.btnExportReport)
        val textViewReport: TextView = findViewById(R.id.textView4)

        textViewReport.text = reportContent

        exitButton.setOnClickListener {
            val intent = Intent(this, Settings_Page::class.java)
            startActivity(intent)
            finish()
        }

        exportButton.setOnClickListener {
            checkPermissionAndExport()
        }
    }

    private fun checkPermissionAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            exportReportToPDF()
        } else {
            Toast.makeText(this, "Exporting PDF requires Android 10 or higher", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportReportToPDF() {
        val pdfDocument = generatePDF()

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Budget_Report_2025_04.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download")
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let { contentUri ->
            try {
                val outputStream = contentResolver.openOutputStream(contentUri)
                if (outputStream != null) {
                    pdfDocument.writeTo(outputStream)
                    pdfDocument.close()
                    outputStream.close()
                    Toast.makeText(this, "Report exported successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to export the report", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generatePDF(): PdfDocument {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply {
            textSize = 14f
            color = Color.BLACK
        }

        val lines = reportContent.split("\n")
        var y = 50f

        for (line in lines) {
            canvas.drawText(line, 40f, y, paint)
            y += paint.descent() - paint.ascent() + 10f
        }

        pdfDocument.finishPage(page)
        return pdfDocument
    }
}