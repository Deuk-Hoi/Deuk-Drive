package com.deuksoft.deukdrive.FileDownloadManager

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.deuksoft.deukdrive.ImageFreeView
import com.deuksoft.deukdrive.MainActivity
import java.io.File


class FileDownload {

    fun FileOpen(context: Context, filePath : String, fileName : String){
        val intent = Intent(context, ImageFreeView::class.java)
        intent.putExtra("filePath",filePath)
        intent.putExtra("fileName",fileName)
        startActivity(context, intent, null)
    }

    fun DownloadFile( filePath: String, fileName: String, downloadManager: DownloadManager) {
        var downloadId : Long = -1L
        val file = File("/storage/emulated/0/Deuk Drive/", fileName)
        val fileurl = "http://118.42.168.26:3000/${filePath}${fileName}"

        val request = DownloadManager.Request(Uri.parse(fileurl))
            .setTitle(fileName)
            .setDescription("Downloading...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(file))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        downloadId = downloadManager.enqueue(request)
        Log.d("path", "path : " + file.path)
    }
}