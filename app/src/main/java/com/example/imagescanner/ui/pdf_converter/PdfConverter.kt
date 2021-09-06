package com.example.imagescanner.ui.pdf_converter

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.imagescanner.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Bitmap




class PdfConverter : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view: View = inflater.inflate(R.layout.pdf_converter_fragment, container, false)
        val uploadButton = view.findViewById<View>(R.id.upload_button) as ImageButton
        val uploadMultipleButton = view.findViewById<View>(R.id.upload_multiple_button) as ImageButton

        Toast.makeText(context, "Pick images from gallery to convert them to PDF", Toast.LENGTH_SHORT).show()

        uploadButton.setOnClickListener() {
            galleryIntent()
        }
        uploadMultipleButton.setOnClickListener() {
            galleryMultipleIntent()
        }

        return view
    }

    private val RESULT_LOAD_IMG = 106
    private val RESULT_LOAD_MULT_IMG = 102


    private fun galleryIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
    }

    private fun galleryMultipleIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, RESULT_LOAD_MULT_IMG)
    }
    private val uriArray: ArrayList<String> = ArrayList()
    private val imagesEncodedList = ArrayList<String>()
    private lateinit var imageEncoded: String


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMG) {
                try {
                    val imageUri: Uri? = data?.data
                    val formats = arrayOf("Yes", "Pick an another image")

                    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                    builder.setTitle("Convert and Sent Image as PDF?")
                    builder.setItems(formats) { dialog, which ->
                        // the user clicked on colors[which]
                        if ("Yes" == formats[which]) {
                            if (imageUri != null) {
                                sharePDF(imageUri)
                            }
                        } else if ("Pick an another image" == formats[which]) {
                            galleryIntent()
                        }
                    }
                    builder.show()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(context, "wrong", Toast.LENGTH_LONG).show()
                }
            }
            if (requestCode == RESULT_LOAD_MULT_IMG) {
                try {
                    val imageUri: ClipData? = data?.clipData
                    if (imageUri != null) {
                        for (i in 0 until imageUri.itemCount) {
                            val item: ClipData.Item = imageUri.getItemAt(i)
                            uriArray.add(item.uri.toString())
                        }
                    }
                    val formats = arrayOf("Yes", "Pick an another image")
                    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                    builder.setTitle("Convert and Sent Image as PDF?")
                    builder.setItems(formats) { dialog, which ->
                        // the user clicked on colors[which]
                        if ("Yes" == formats[which]) {
                                shareMultPDF(uriArray)
                        } else if ("Pick an another image" == formats[which]) {
                            galleryMultipleIntent()
                        }
                    }
                    builder.show()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(context, "wrong", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    var pdfDocument // to create .pdf file
            : PdfDocument? = null

    private fun sharePDF(imageUri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)

        pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page: PdfDocument.Page = pdfDocument!!.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()
        paint.color = Color.parseColor("#FFFFFF")
        canvas.drawBitmap(bitmap, 0.toFloat(), 0.toFloat(), null)
        pdfDocument!!.finishPage(page)

        if (pdfDocument == null) {
            Log.i("local-dev", "pdfDocument in 'saveFile' function is null")
            return
        }

        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)


        val fileName = "imagescanner" + (0..100).random().toString() + ".pdf"
        val file = File(storageDir, fileName)
        try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument!!.writeTo(fileOutputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        pdfDocument!!.close()

        val contentUri = activity?.applicationContext?.let { FileProvider.getUriForFile(it,"com.example.android.fileprovider",file) }

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        shareIntent.type = "application/pdf"
        /** set the corresponding mime type of the file to be shared */
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

        startActivity(Intent.createChooser(shareIntent, "Share the file..."))
    }

    private fun shareMultPDF(arrayList: ArrayList<String>) {
        val sendSelected: ArrayList<Uri> = ArrayList()

        pdfDocument = PdfDocument()

        for (i in 0 until arrayList.size) {

            val uri = Uri.parse(arrayList[i])
            //val uri = Uri.fromFile(File(arrayList[i]))
            sendSelected.add(uri)

            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap, 600, 800,
                false
            )
            val pageInfo = PdfDocument.PageInfo.Builder(scaledBitmap.width, scaledBitmap.height, 1).create()
            val page: PdfDocument.Page = pdfDocument!!.startPage(pageInfo)

            val canvas = page.canvas
            val paint = Paint()
            paint.color = Color.parseColor("#FFFFFF")
            canvas.drawBitmap(scaledBitmap, 0.toFloat(), 0.toFloat(), null)
            pdfDocument!!.finishPage(page)
        }

        val storageDir: File? =  activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)


        val fileName = "ImageScanner" + (0..100).random().toString() + ".pdf"
        val file = File(storageDir, fileName)
        try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument!!.writeTo(fileOutputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        pdfDocument!!.close()

        val contentUri = activity?.applicationContext?.let { FileProvider.getUriForFile(it,"com.example.android.fileprovider",file) }

        Log.i("local-dev", "'saveFile' function successfully done")


        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        shareIntent.type = "application/pdf"
        /** set the corresponding mime type of the file to be shared */
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

        startActivity(Intent.createChooser(shareIntent, "Share the file..."))
    }
}