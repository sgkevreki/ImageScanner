package com.example.imagescanner.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagescanner.PreviewPhoto
import com.example.imagescanner.R

import android.widget.Toast
import com.example.imagescanner.PreviewMultiplePhotos
import com.example.imagescanner.ui.saved_files.Saved_FilesFragment
import kotlin.collections.ArrayList


class GridAdapter(tileList: ArrayList<Uri>, context: Context) :
    RecyclerView.Adapter<GridAdapter.MyViewHolder>() {
    private val mTiles: ArrayList<Uri> = tileList
    private val mContext: Context = context
    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)}

    class MyViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var mImageView: ImageView = itemView.findViewById(R.id.photo_img_id)
        var checked: ImageView = itemView.findViewById(R.id.check)
        var send: ImageView = itemView.findViewById(R.id.send)

        init {
                itemView.setOnClickListener {
                    Log.d("OnClick", "GridAdapter.java, Got here")
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
        var flag = 0
        val arraySelectedIDs: ArrayList<Uri> = ArrayList()
        holder.mImageView.setImageURI(currentPhotoID)
        holder.mImageView.setOnClickListener() {
            if (flag==0) {
                val intent = Intent(mContext, PreviewPhoto::class.java)
                intent.putExtra("preview", currentPhotoID.toString())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                mContext.startActivity(intent)
            }
            else {
                holder.checked.visibility = View.INVISIBLE
                holder.send.visibility = View.INVISIBLE
                arraySelectedIDs.remove(currentPhotoID)
            }
        }
        holder.mImageView.setOnLongClickListener() {
            holder.checked.visibility = View.VISIBLE
            holder.send.visibility = View.VISIBLE
            flag = 1
            arraySelectedIDs.add(currentPhotoID)
            true
        }

        holder.send.setOnClickListener() {
            val intent = Intent(mContext, PreviewMultiplePhotos::class.java)
            intent.putExtra("selected", arraySelectedIDs)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(intent)
        }
        return
    }

    override fun getItemCount(): Int {
        return mTiles.size
    }


}