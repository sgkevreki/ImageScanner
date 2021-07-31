package com.example.imagescanner.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.imagescanner.MainActivity
import com.example.imagescanner.PreviewPhoto
import com.example.imagescanner.R
import java.util.*

class GridAdapter(tileList: ArrayList<Uri>, context: Context) :
    RecyclerView.Adapter<GridAdapter.MyViewHolder>() {
    private val mTiles: ArrayList<Uri> = tileList
    private val mContext: Context = context
    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int) }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    class MyViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var mImageView: ImageView = itemView.findViewById(R.id.photo_img_id)
        init {
            itemView.setOnClickListener {
                Log.d("OnClick", "GridAdapter.java, Got here")
//                Toast.makeText(itemView.context, mTiles[].toString(), Toast.LENGTH_SHORT).show()
                if (listener != null) {
                    Log.d("OnClick", "GridAdapter.java, Got in first if")
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        Log.d("OnClick", "GridAdapter.java, Got in second if")
                        listener.onItemClick(position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.grid_photo_layout, parent, false)
        return MyViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPhotoID: Uri = mTiles[position]
        holder.mImageView.setImageURI(currentPhotoID)
        holder.mImageView.setOnClickListener() {
            val intent = Intent(mContext, PreviewPhoto::class.java)
            intent.putExtra("preview",currentPhotoID.toString() )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            mContext.startActivity(intent)
        }

        return
    }

    override fun getItemCount(): Int {
        return mTiles.size
    }

}