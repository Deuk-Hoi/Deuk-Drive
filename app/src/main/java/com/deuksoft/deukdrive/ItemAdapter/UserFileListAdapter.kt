package com.deuksoft.deukdrive.ItemAdapter

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deuksoft.deukdrive.FileRemoveManager.FileRemove
import com.deuksoft.deukdrive.MainActivity
import com.deuksoft.deukdrive.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_main.*


class UserFileListAdapter(var context: Context,var filelist: ArrayList<UserFileList>, var itemClick: (UserFileList) -> Unit): RecyclerView.Adapter<UserFileListAdapter.Holder>(){

    inner class Holder(itemView: View, itemClick: (UserFileList) -> Unit?): RecyclerView.ViewHolder(itemView){
        var Fileimg = itemView.findViewById<ImageView>(R.id.Fileimg)
        var FileName = itemView.findViewById<TextView>(R.id.FileName)
        var UploadDate = itemView.findViewById<TextView>(R.id.UploadDate)
        var FileSize = itemView.findViewById<TextView>(R.id.FileSize)
        var moreicon = itemView.findViewById<ImageButton>(R.id.moreicon)

        fun bind(userFileList: UserFileList, context: Context)
        {
            Log.e("ds", "dsfd")
            if(userFileList.Fileimg != ""){
                val resourceId = context.resources.getIdentifier(
                    userFileList.Fileimg,
                    "drawable",
                    context.packageName
                )
                Fileimg.setImageResource(resourceId)
            }else{
                Fileimg.setImageResource(R.mipmap.ic_launcher)
            }

            FileName.text = userFileList.FileName
            UploadDate.text = userFileList.UploadDate
            FileSize.text = userFileList.FileSize + " MB"

            itemView.setOnClickListener { itemClick(userFileList) }
            moreicon.setOnClickListener(View.OnClickListener {
                //Log.e("Image", userFileList.Fileimg)

            })
            /*itemView.setOnLongClickListener(View.OnLongClickListener {
                Log.e("Image", userFileList.FileName)
                Log.e("FilePath", userFileList.FilePath)
                var m = MainActivity()
                m.removeFile(context, userFileList.FilePath, userFileList.FileName, userFileList)
                true
            })*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.userfilelistlayout, parent, false)
        return Holder(view, itemClick)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(filelist[position], context)
    }

    override fun getItemCount(): Int {
        return filelist.size
    }


}