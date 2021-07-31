package com.example.imagescanner.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.imagescanner.MainActivity
import com.example.imagescanner.R
import pl.droidsonroids.gif.GifImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


class OptimizedImage : AppCompatActivity() {
    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_optimized_image)


        val backButton = findViewById<ImageButton>(R.id.back_button)
        val ivShow: ImageView = findViewById(R.id.iv_show)

        val loading = findViewById<GifImageView>(R.id.loading)
        loading.visibility = View.VISIBLE

        val moveToSavedButton = findViewById<ImageButton>(R.id.move_to_saved_button)

        val photoFile: File = intent.getSerializableExtra("Cropped Image") as File
        val bitmap = BitmapFactory.decodeFile(photoFile.path)
        ivShow.setImageBitmap(bitmap)
        

        Thread {
            val thresholdBitmap = grayscaleToBin(bitmap)
            val denoiseBitmap = removeNoise(thresholdBitmap)
            loading.visibility = View.INVISIBLE
            ivShow.setImageBitmap(denoiseBitmap)
            saveMediaToStorage(denoiseBitmap)
        }.start()


        backButton.setOnClickListener { finish() }

        moveToSavedButton.setOnClickListener() {


            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("test","true" )
            startActivity(intent)
        }


    }


    private fun grayscaleToBin(bm2: Bitmap): Bitmap {
        val bm: Bitmap = bm2.copy(Bitmap.Config.RGB_565, true)
        val width = bm.width
        val height = bm.height
        val pixels: IntArray = IntArray(width * height)
        bm.getPixels(pixels, 0, width, 0, 0, width, height)
        val size = width * height
        val s = width / 8
        val s2 = s shr 1
        val t = 0.15
        val it = 1.0 - t
        val integral = IntArray(size)
        val threshold = IntArray(size)
        var i: Int
        var j: Int
        var diff: Int
        var x1: Int
        var y1: Int
        var x2: Int
        var y2: Int
        var ind1: Int
        var ind2: Int
        var ind3: Int
        var sum = 0
        var ind = 0
        while (ind < size) {
            sum += pixels[ind] and 0xFF
            integral[ind] = sum
            ind += width
        }
        x1 = 0
        i = 1
        while (i < width) {
            sum = 0
            ind = i
            ind3 = ind - s2
            if (i > s) {
                x1 = i - s
            }
            diff = i - x1
            j = 0
            while (j < height) {
                sum += pixels[ind] and 0xFF
                integral[ind] = integral[(ind - 1)] + sum
                ind += width
                if (i < s2) {
                    ++j
                    continue
                }
                if (j < s2) {
                    ++j
                    continue
                }
                y1 = if (j < s) 0 else j - s
                ind1 = y1 * width
                ind2 = j * width
                if ((pixels[ind3] and 0xFF) * (diff * (j - y1)) < (integral[(ind2 + i)] - integral[(ind1 + i)] - integral[(ind2 + x1)] + integral[(ind1 + x1)]) * it) {
                    threshold[ind3] = 0x00
                } else {
                    threshold[ind3] = 0xFFFFFF
                }
                ind3 += width
                ++j
            }
            ++i
        }
        y1 = 0
        j = 0
        while (j < height) {
            i = 0
            y2 = height - 1
            if (j < height - s2) {
                i = width - s2
                y2 = j + s2
            }
            ind = j * width + i
            if (j > s2) y1 = j - s2
            ind1 = y1 * width
            ind2 = y2 * width
            diff = y2 - y1
            while (i < width) {
                x1 = if (i < s2) 0 else i - s2
                x2 = i + s2

                // check the border
                if (x2 >= width) x2 = width - 1
                if ((pixels[ind] and 0xFF) * ((x2 - x1) * diff) < (integral[(ind2 + x2)] - integral[(ind1 + x2)] - integral[(ind2 + x1)] + integral[(ind1 + x1)]) * it) {
                    threshold[ind] = 0x00
                } else {
                    threshold[ind] = 0xFFFFFF
                }
                ++i
                ++ind
            }
            ++j
        }
        /*-------------------------------
    * --------------------------------------------*/bm.setPixels(
            threshold,
            0,
            width,
            0,
            0,
            width,
            height
        )
        return bm
    }

    private fun removeNoise(bm: Bitmap): Bitmap {
        for (x in 0 until bm.width) {
            for (y in 0 until bm.height) {
                val pixel = bm.getPixel(x, y)
                val R = Color.red(pixel)
                val G = Color.green(pixel)
                val B = Color.blue(pixel)
                if (R < 162 && G < 162 && B < 162)
                    bm.setPixel(x, y, Color.BLACK)
            }
        }
        for (x in 0 until bm.width) {
            for (y in 0 until bm.height) {
                val pixel = bm.getPixel(x, y)
                val R = Color.red(pixel)
                val G = Color.green(pixel)
                val B = Color.blue(pixel)
                if (R > 162 && G > 162 && B > 162) bm.setPixel(x, y, Color.WHITE)
            }
        }
        return bm
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "imagescanner" + (0..100).random().toString() + ".jpg"

        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }

                Log.d("tag", " Url of Image is " + imageUri?.toString() )
            }

        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }
}




