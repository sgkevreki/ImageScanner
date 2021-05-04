package com.example.imagescanner.ui.saved_files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.imagescanner.R

class Saved_FilesFragment : Fragment() {

    private lateinit var galleryViewModel: Saved_FilesViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
                ViewModelProvider(this).get(Saved_FilesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_saved_files, container, false)

        return root
    }
}