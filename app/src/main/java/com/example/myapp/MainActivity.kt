package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        val scanButton: Button = findViewById(R.id.scanButton)
        resultTextView = findViewById(R.id.resultTextView)

        scanButton.setOnClickListener {
            startBarcodeScanner()
        }
        */
    }

    private fun startBarcodeScanner() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            setPrompt("Наведите камеру на штрих-код")
            setCameraId(0)
            setBeepEnabled(true)
            setBarcodeImageEnabled(true)
            setOrientationLocked(false)
        }

        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_LONG).show()
        } else {
            resultTextView.text = "Результат сканирования: ${result.contents}"
        }
    }
}