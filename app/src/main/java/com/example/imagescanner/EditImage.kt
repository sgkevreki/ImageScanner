package com.example.imagescanner

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import java.io.File

import me.pqpo.smartcropperlib.SmartCropper;
import me.pqpo.smartcropperlib.view.CropImageView;

class EditImage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)

        var currentPhotoPath: String = ""
        val rightButton = findViewById<ImageButton>(R.id.right_button)
        val leftButton = findViewById<ImageButton>(R.id.left_button)
        val mirrorButton = findViewById<ImageButton>(R.id.mirror_button)

        val ivCrop: CropImageView = findViewById(R.id.iv_crop)


        currentPhotoPath = intent.getStringExtra("current_photo_path").toString()
        val selectedImage = findViewById<View>(R.id.displayImageView) as ImageView
        val f = File(currentPhotoPath)
        selectedImage.setImageURI(Uri.fromFile(f))
        Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f))
         MediaScannerConnection.scanFile(applicationContext, arrayOf(f.toString()),
             null, null)

        val bitmap = selectedImage.drawable.toBitmap()

        ivCrop.setImageToCrop(bitmap)
        val crop: Bitmap = ivCrop.crop()

        rightButton.setOnClickListener() {
            //rotate right the image
            selectedImage.animate().rotationBy(90f).start()
        }
        leftButton.setOnClickListener() {
            //rotate left the image
            selectedImage.animate().rotationBy(-90f).start()
        }
        mirrorButton.setOnClickListener() {
            //mirror/flip the image
            selectedImage.animate().rotationYBy(180f).start()
        }
    }

}
