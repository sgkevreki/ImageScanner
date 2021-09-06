package com.example.imagescanner

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.imagescanner.ui.OptimizedImage
import me.pqpo.smartcropperlib.view.CropImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class EditImage : AppCompatActivity() {
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var ivZoom: ImageView? = null
    var type: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)

        var currentPhotoPath: String = ""
        val rightButton = findViewById<ImageButton>(R.id.right_button)
        val leftButton = findViewById<ImageButton>(R.id.left_button)
        val mirrorButton = findViewById<ImageButton>(R.id.mirror_button)
        val optimizeButton = findViewById<ImageButton>(R.id.optimize_button)
        val retakeButton = findViewById<ImageButton>(R.id.retake_button)

        val mCroppedFile = File(getExternalFilesDir("img"), "scan.jpg")


        val ivCrop: CropImageView = findViewById(R.id.iv_crop)

        //pinch to zoom in or zoom out
        //im using a new variable for this
        ivZoom = ivCrop

        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        currentPhotoPath = intent.getStringExtra("current_photo_path").toString()
        type = intent.getStringExtra("type").toString()
        var uri3 = Uri.parse(currentPhotoPath)
        ivCrop.setImageURI(uri3)

        val bitmap = ivCrop.drawable.toBitmap()

        ivCrop.setImageToCrop(bitmap)


        rightButton.setOnClickListener() {
            //rotate right the image
            ivCrop.animate().rotationBy(90f).start()
        }
        leftButton.setOnClickListener() {
            //rotate left the image
            ivCrop.animate().rotationBy(-90f).start()
        }
        mirrorButton.setOnClickListener() {
            //mirror/flip the image
            ivCrop.animate().rotationYBy(180f).start()
        }

        retakeButton.setOnClickListener() {
            onBackPressed()
        }

        //Optimize button!
        optimizeButton.setOnClickListener() {
            if (ivCrop.canRightCrop()) {
                val crop: Bitmap = ivCrop.crop()
                saveImage(crop, mCroppedFile)
                setResult(RESULT_OK)
            } else {
                Toast.makeText(applicationContext,"Cannot crop correctly!",Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, OptimizedImage::class.java)
            intent.putExtra("Cropped Image", mCroppedFile)
            startActivity(intent)
        }

    }
    private fun saveImage(bitmap: Bitmap, saveFile: File) {
        try {
            val fos = FileOutputStream(saveFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {

        // Create the object of AlertDialog Builder class
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@EditImage)

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false)


        // Set the message show for the Alert time
        builder.setMessage("This one will be deleted.")

        // Set Alert Title
        if (type == "camera") {
            builder.setTitle("Do you wish to retake the image?")
        }  else if (type == "import") {
            builder.setTitle("Do you wish to pick an another image?")
        }

        builder
            .setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, which -> // When the user click yes button
                    // then app will close
                    if (type == "camera") {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("cameraState", "true")
                        startActivity(intent)
                    }
                    else {
                        finish()
                    }
                })

        builder
            .setNegativeButton(
                "CANCEL",
                DialogInterface.OnClickListener { dialog, which -> // If user click no
                    // then dialog box is canceled.
                    dialog.cancel()
                })


        // Show the Alert Dialog box
        builder.create()?.show()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mScaleGestureDetector!!.onTouchEvent(event)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        var mScaleFactor = 1.0f


        // when a scale gesture is detected, use it to resize the image
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = 0.5f.coerceAtLeast(mScaleFactor.coerceAtMost(2.0f));

            ivZoom?.scaleX = mScaleFactor
            ivZoom?.scaleY = mScaleFactor

            return true
        }
    }


}
