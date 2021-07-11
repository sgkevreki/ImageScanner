package com.example.imagescanner.ui.saved_files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imagescanner.R

class Saved_FilesFragment : Fragment() {

    private lateinit var galleryViewModel: Saved_FilesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_saved_files, container, false)
    }


}