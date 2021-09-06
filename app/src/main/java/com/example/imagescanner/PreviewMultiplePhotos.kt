package com.example.imagescanner

import android.R.attr
import android.app.AlertDialog
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Bitmap

import android.R.attr.data




class PreviewMultiplePhotos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_multiple_photos)

        val selectedPhotosArray = intent.getStringArrayListExtra("selected")
        val uri = Uri.parse(selectedPhotosArray?.get(0))

        val sendButton = findViewById<ImageButton>(R.id.sending_options_button)
        val backButton = findViewById<ImageButton>(R.id.back_button)

        val ivPreview: ImageView = findViewById(R.id.iv_preview)
        ivPreview.setImageURI(uri)

        backButton.setOnClickListener { finish() }

        sendButton.setOnClickListener {

            val formats = arrayOf("JPGs", "PDF")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Send Selected Images As:")
            builder.setItems(formats) { dialog, which ->
                // the user clicked on colors[which]
                if ("JPGs" == formats[which]) {
                    if (selectedPhotosArray != null) {
                        shareJPG(selectedPhotosArray)
                    }
                } else if ("PDF" == formats[which]) {
                    if (selectedPhotosArray != null) {
                        sharePDF(selectedPhotosArray)
                    }
                }
            }
            builder.show()
        }


    }

    private fun shareJPG(arrayList: ArrayList<String>) {
        val sendSelected: ArrayList<Uri> = ArrayList()

        for (i in 0 until arrayList.size) {
            val uri = Uri.parse(arrayList[i])
            sendSelected.add(uri)
        }


        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, sendSelected)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    private var pdfDocument // to create .pdf file
            : PdfDocument? = null

    private fun sharePDF(arrayList: ArrayList<String>) {
        val sendSelected: ArrayList<Uri> = ArrayList()

        pdfDocument = PdfDocument()

        for (i in 0 until arrayList.size) {
            val uri = Uri.parse(arrayList[i])
            sendSelected.add(uri)

            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page: PdfDocument.Page = pdfDocument!!.startPage(pageInfo)

            val canvas = page.canvas
            val paint = Paint()
            paint.color = Color.parseColor("#FFFFFF")
            canvas.drawBitmap(bitmap, 0.toFloat(), 0.toFloat(), null)
            pdfDocument!!.finishPage(page)
        }


        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)


        val fileName = "ImageScanner" + (0..100).random().toString() + ".pdf"
        val file = File(storageDir, fileName)
        try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument!!.writeTo(fileOutputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        pdfDocument!!.close()

        val contentUri = FileProvider.getUriForFile(applicationContext,"com.example.android.fileprovider",file)

        Log.i("local-dev", "'saveFile' function successfully done")


        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        shareIntent.type = "application/pdf"
        /** set the corresponding mime type of the file to be shared */
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

        startActivity(Intent.createChooser(shareIntent, "Share the file..."))
    }

}