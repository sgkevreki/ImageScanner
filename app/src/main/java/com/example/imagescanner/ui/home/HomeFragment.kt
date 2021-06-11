package com.example.imagescanner.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.imagescanner.EditImage
import com.example.imagescanner.MainActivity
import com.example.imagescanner.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {


        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val imageButton = view.findViewById<View>(R.id.scan_button) as ImageButton


        val activity: MainActivity? = activity as MainActivity?
        val myDataFromActivity: String? = activity?.getMyData()

        if (myDataFromActivity == "true") {
            dispatchTakePictureIntent()
        }

        imageButton.setOnClickListener() {

            //  askCameraPermissions()
            dispatchTakePictureIntent()
        }
        return view
    }


    private val CAMERA_PERM_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val GALLERY_REQUEST_CODE = 105
    // private val intent3 =Intent(activity, EditImage::class.java)


    var currentPhotoPath: String? = null
    private fun askCameraPermissions() {
        if (context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.CAMERA) } != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERM_CODE)
        } else {
            dispatchTakePictureIntent()
        }
    }
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
//        if (requestCode == CAMERA_PERM_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                dispatchTakePictureIntent()
//            } else {
//                Toast.makeText(context, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val intent = Intent(activity, EditImage::class.java)
            intent.putExtra("current_photo_path", currentPhotoPath)
            startActivity(intent)
//        activity?.finish()
        }
    }


    private fun getFileExt(contentUri: Uri?): String? {
        val c: ContentResolver? = activity?.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(c?.getType(contentUri!!))
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (activity?.let { takePictureIntent.resolveActivity(it.packageManager) } != null) {
            // Create the File where the photo should go


            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI: Uri? = context?.let {
                    FileProvider.getUriForFile(it,
                        "com.example.android.fileprovider",
                        photoFile)
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)

            }
        }
    }


}