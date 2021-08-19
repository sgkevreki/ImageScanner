package com.example.imagescanner

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PreviewMultiplePhotos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_multiple_photos)

        val selectedPhotosArray = intent.getStringArrayListExtra("selected")
        val uri = Uri.parse(selectedPhotosArray?.get(0))

        val sendButton = findViewById<ImageButton>(R.id.sending_options_button)

        val ivPreview: ImageView = findViewById(R.id.iv_preview)
        ivPreview.setImageURI(uri)

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
                  //  sharePDF(ivPreview)
                }
            }
            builder.show()
        }


    }

    private fun shareJPG(arrayList: ArrayList<String>) {
        val sendSelected: ArrayList<Uri> = ArrayList()

            for (i in 0 until arrayList.size) {
            val uri = Uri.parse(arrayList.get(i))
                sendSelected.add(uri)
        }

        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, sendSelected)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    var pdfDocument // to create .pdf file
            : PdfDocument? = null

//    private fun sharePDF(view: View) {
//
//
//
//        val bitmap: Bitmap = loadBitmapFromView(view)!!
//
//        pdfDocument = PdfDocument()
//        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, ).create()
//        val page: PdfDocument.Page = pdfDocument!!.startPage(pageInfo)
//
//        val canvas = page.canvas
//        val paint = Paint()
//        paint.color = Color.parseColor("#FFFFFF")
//        canvas.drawBitmap(bitmap, 0.toFloat(), 0.toFloat(), null)
//        pdfDocument!!.finishPage(page)
//
//        if (pdfDocument == null) {
//            Log.i("local-dev", "pdfDocument in 'saveFile' function is null")
//            return
//        }
//
//        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
////        var isDirectoryCreated: Boolean = root.exists()
////        if (!isDirectoryCreated) {
////            isDirectoryCreated = root.mkdir()
////        }
//
//        val fileName = "ImageScanner.pdf"
//        val file = File(storageDir, fileName)
//        try {
//            val fileOutputStream = FileOutputStream(file)
//            pdfDocument!!.writeTo(fileOutputStream)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        pdfDocument!!.close()
//
//        val contentUri = FileProvider.getUriForFile(applicationContext,"com.example.android.fileprovider",file)
//
//        Log.i("local-dev", "'saveFile' function successfully done")
//
//        val shareIntent = Intent(Intent.ACTION_SEND)
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        shareIntent.type = "application/pdf"
//        /** set the corresponding mime type of the file to be shared */
//        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
//
//        startActivity(Intent.createChooser(shareIntent, "Share the file..."))
//
//    }

}