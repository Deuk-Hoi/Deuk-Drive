package com.deuksoft.deukdrive.ItemAdapter

import android.app.DownloadManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deuksoft.deukdrive.FileDownloadManager.FileDownload
import com.deuksoft.deukdrive.FileRemoveManager.FileRemove
import com.deuksoft.deukdrive.MainActivity
import com.deuksoft.deukdrive.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*

class BottomItemAdapter (val context: Context, val itemlist : ArrayList<BottomMenuItem>, val itemClick : (BottomMenuItem)-> Unit): RecyclerView.Adapter<BottomItemAdapter.Holder>(){

    inner class Holder(itemView: View, itemClick: (BottomMenuItem) -> Unit): RecyclerView.ViewHolder(itemView){
        var itemimg = itemView.findViewById<ImageView>(R.id.itemimg)
        var itemtxt = itemView.findViewById<TextView>(R.id.itemtxt)

        fun bind(bottomMenuItem : BottomMenuItem)
        {
            if(bottomMenuItem.Itemimg != 0){
                //val resourceId = context.resources.getIdentifier(bottomMenuItem.Itemimg.toString(), "drawable", context.packageName)
                itemimg.setImageResource(bottomMenuItem.Itemimg)
            }else{
                itemimg.setImageResource(R.mipmap.ic_launcher)
            }
            itemtxt.text = bottomMenuItem.usetxt

            itemView.setOnClickListener { itemClick(bottomMenuItem) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.bottomitemlayout, parent, false)
        return Holder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return itemlist.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemlist[position])
    }

}