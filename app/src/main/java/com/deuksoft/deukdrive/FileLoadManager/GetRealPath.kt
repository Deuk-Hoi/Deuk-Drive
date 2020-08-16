package com.deuksoft.deukdrive.FileLoadManager

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri

import android.provider.OpenableColumns
import android.util.Log


class GetRealPath {

    fun getName(uri: Uri, cusor: Cursor):String{
        var result : String = ""
        try{
            if(cusor !=null && cusor.moveToFirst()){
                result = cusor.getString(cusor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }finally {
            if(cusor!=null)
            {
                cusor.close()
            }
        }
        if(result == null){
            result = uri.lastPathSegment.toString()
            return result
        }
        return result
    }

    fun getFileSize(uri: Uri, cusor: Cursor):String{
        var result : String = ""
        try{
            if(cusor !=null && cusor.moveToFirst()){
                result = cusor.getString(cusor.getColumnIndex(OpenableColumns.SIZE))
            }
        }finally {
            if(cusor!=null)
            {
                cusor.close()
            }
        }
        if(result == null){
            result = uri.lastPathSegment.toString()
            return result
        }
        return result
    }
}

