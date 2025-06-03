package com.example.myapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.io.InputStream
import android.app.Dialog
import android.view.ViewGroup
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val BRCDE = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_SHORT).show()
        } else {
            //Toast.makeText(this, "Результат: ${result.contents}", Toast.LENGTH_LONG).show()
            SHW_DLG(result.contents)
        }

    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { scanBarcodeFromImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window)

        val SCN_BRCD: Button = findViewById(R.id.SCN_BRCDE_BTN)
        val SCN_IM_BRCD: Button = findViewById(R.id.SCN_IM_BRCDE_BTN)

        SCN_BRCD.setOnClickListener { STRT_BRCD_SCN() }
        SCN_IM_BRCD.setOnClickListener { pickImageForBarcodeScan() }
    }
    //region ScannerArea
    private fun pickImageForBarcodeScan() {
        imagePickerLauncher.launch("image/*")
    }

    private fun scanBarcodeFromImage(imageUri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val result = scanBarcode(bitmap)
            if (result.isNotEmpty()) {
                //Toast.makeText(this, "Результат: $result", Toast.LENGTH_LONG).show()
                SHW_DLG(result)
            } else {
                Toast.makeText(this, "Штрих-код не найден", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scanBarcode(bitmap: Bitmap): String {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader()

        return try {
            val result = reader.decode(binaryBitmap)
            result.text
        } catch (e: Exception) {
            ""
        }
    }

    private fun STRT_BRCD_SCN() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
            LNCH_SCN()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                101
            )
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
        BRCDE.launch(options)
    }
    //endregion

    
    private fun SHW_DLG(string: String) {
        val dialog = Dialog(this).apply {
            setCanceledOnTouchOutside(true)
            setContentView(R.layout.dialog_window)

            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            findViewById<TextView>(R.id.RSL_TXTV).text = string
            findViewById<Button>(R.id.PRNT_BTN).setOnClickListener { dismiss() }
            findViewById<Button>(R.id.CNTN_BTN).setOnClickListener { dismiss() }
        }
        dialog.show()
    }
}
