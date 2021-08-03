
package com.example.imagescanner.ui.saved_files

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagescanner.R
import com.example.imagescanner.ui.GridAdapter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class Saved_FilesFragment : Fragment() {

    private lateinit var galleryViewModel: Saved_FilesViewModel
    private var photos: ArrayList<Uri> = ArrayList()
    private var finalphotos: ArrayList<Uri> = ArrayList()



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {

            photos = getAllImages()

//        for (i in 0..1) {
//           // if (photos[i].toString().contains( "imagescanner")) {
//                finalphotos.add(photos[i])
//                Toast.makeText(context, photos[i].toString(), Toast.LENGTH_LONG).show()
//           // }
//        }


        var newString ="content://media/external/images/media/52240"
        var uri3 = Uri.parse(newString)
        var newString2 ="content://media/external/images/media/52242"
        var uri4 = Uri.parse(newString2)
        var newString3 ="content://media/external/images/media/52252"
        var uri5 = Uri.parse(newString3)

        finalphotos.add(uri3)
        finalphotos.add(uri4)
        finalphotos.add(uri5)
        //  Toast.makeText(context, testi[0].toString(), Toast.LENGTH_LONG).show()


        val view = inflater.inflate(R.layout.fragment_saved_files, container, false)
        val mRecyclerView : RecyclerView = view!!.findViewById(R.id.recyclerview_id)
        val mAdapter = GridAdapter(finalphotos, requireActivity().applicationContext)
        val mLayoutManager = GridLayoutManager(requireActivity().applicationContext, 2 /* Pictures per line */)

        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter



        return view
    }
    private fun getAllImages(): ArrayList<Uri> {
        val allImages = ArrayList<Uri>()

        val imageProjection = arrayOf(
            MediaStore.Images.Media._ID
        )

        val imageSortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = activity?.application?.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            imageSortOrder
        )

        cursor.use {

            if (cursor != null)
            {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext())
                {
                    allImages.add(
                        ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            cursor.getLong(idColumn))
                    )
                }
            }
            else
            {
                Log.d("AddViewModel", "Cursor is null!")
            }
        }
        return allImages
    }

}
