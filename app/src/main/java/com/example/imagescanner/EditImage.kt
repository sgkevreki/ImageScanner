package com.example.imagescanner

import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class EditImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)
        var currentPhotoPath: String = ""

        currentPhotoPath = intent.getStringExtra("current_photo_path").toString()
        val selectedImage = findViewById<View>(R.id.displayImageView) as ImageView
        val f = File(currentPhotoPath)
        selectedImage.setImageURI(Uri.fromFile(f))
        Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f))
         MediaScannerConnection.scanFile(applicationContext, arrayOf(f.toString()),
             null, null)
    }

}