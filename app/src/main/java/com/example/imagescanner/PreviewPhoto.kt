package com.example.imagescanner

import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PreviewPhoto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_photo)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        val sendButton = findViewById<ImageButton>(R.id.sending_options_button)
        val ivPreview: ImageView = findViewById(R.id.iv_preview)

        val photoId = intent.getStringExtra("preview").toString()
        var photoIdUri = Uri.parse(photoId)
        ivPreview.setImageURI(photoIdUri)


        backButton.setOnClickListener { finish() }

        sendButton.setOnClickListener {

            val formats = arrayOf("JPG", "PDF")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Send Image As:")
            builder.setItems(formats) { dialog, which ->
                // the user clicked on colors[which]
                if ("JPG" == formats[which]) {
                    shareJPG(ivPreview)
                } else if ("PDF" == formats[which]) {
                    sharePDF(ivPreview)
                }
            }
            builder.show()
        }

    }

    private fun shareJPG(view: View) {

        val mBitmap: Bitmap = loadBitmapFromView(view)!!

        val path: String =
            MediaStore.Images.Media.insertImage(contentResolver, mBitmap, "Image Description", null)
        val uri = Uri.parse(path)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    private fun sharePDF(view: View) {
//        val mBitmap: Bitmap = loadBitmapFromView(view)!!
//
//        val path: String =
//            MediaStore.Images.Media.insertImage(contentResolver, mBitmap, "Image Description", null)
//        val uri = Uri.parse(path)
//
//        val shareIntent = Intent(Intent.ACTION_SEND)
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        shareIntent.type = "application/pdf"
//        /** set the corresponding mime type of the file to be shared */
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//
//        startActivity(Intent.createChooser(shareIntent, "Share the file..."))

    }
    private fun loadBitmapFromView(view: View): Bitmap? {

        // width measure spec
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            view.measuredWidth, View.MeasureSpec.EXACTLY
        )
        // height measure spec
        val heightSpec = View.MeasureSpec.makeMeasureSpec(
            view.measuredHeight, View.MeasureSpec.EXACTLY
        )
        // measure the view
        view.measure(widthSpec, heightSpec)
        // set the layout sizes
        view.layout(
            view.left,
            view.top,
            view.measuredWidth + view.left,
            view.measuredHeight + view.top
        )
        // create the bitmap
        val bitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        // create a canvas used to get the view's image and draw it on the bitmap
        val c = Canvas(bitmap)
        // position the image inside the canvas
        c.translate((-view.scrollX).toFloat(), (-view.scrollY).toFloat())
        // get the canvas
        view.draw(c)
        return bitmap
    }

}



