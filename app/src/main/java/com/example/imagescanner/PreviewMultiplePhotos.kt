package com.example.imagescanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class PreviewMultiplePhotos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_multiple_photos)

        val selectedPhotosArray = intent.getStringExtra("selected")
        val ivPreview: ImageView = findViewById(R.id.iv_preview)
    }
}