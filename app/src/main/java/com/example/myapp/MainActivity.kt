package com.example.myapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {
    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) { Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_SHORT).show() }
        else { Toast.makeText(this, "Результат: ${result.contents}", Toast.LENGTH_LONG).show() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val SCN_BRCD: Button = findViewById(R.id.SCN_BRCDE_BTN) // - - Обычный сканнер штрих кода
        SCN_BRCD.setOnClickListener { STRT_BRCD_SCN() }

        // val SCN_IM_BRCDE: Button = findViewById(R.id.SCN_IM_BRCDE_BTN) // - - Сканер изображения
        // val CNCT_DVCE: Button = findViewById(R.id.CNCT_DVCE_BTN) // - - Состояние подключенного устройства

    }

    //region Scanner
    private fun STRT_BRCD_SCN() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) { LNCH_SCN() }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                101 )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LNCH_SCN()
                } else {
                    Toast.makeText(
                        this,
                        "Для сканирования необходимо разрешение на использование камеры",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun LNCH_SCN() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            setPrompt("Наведите камеру на штрих-код")
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
            setOrientationLocked(false)
        }
        barcodeLauncher.launch(options)
    }
    //endregion Scanner
}