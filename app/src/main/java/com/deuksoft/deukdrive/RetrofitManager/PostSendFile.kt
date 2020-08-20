package com.deuksoft.deukdrive.RetrofitManager

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.*
import java.lang.Exception


fun getRealPathFromURI(context: Context, uri: Uri ): String? {
    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {

        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            return if ("primary".equals(type, ignoreCase = true)) {
                (Environment.getExternalStorageDirectory().toString() + "/"
                        + split[1])
            } else {
                val SDcardpath =
                    getRemovableSDCardPath(
                        context
                    ).split("/Android".toRegex()).toTypedArray()[0]
                SDcardpath + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {
            Log.e("?", "hi")
            val id = DocumentsContract.getDocumentId(uri)
            if(!TextUtils.isEmpty(id)) {
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "")
                }
                val fileId: Long? =
                    getFileId(uri)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        val inputStream: InputStream =
                            context.contentResolver.openInputStream(uri)
                        val file =
                            File(context.cacheDir.absolutePath.toString() + "/" + fileId)
                        writeFile(
                            inputStream,
                            file
                        )
                        return file.getAbsolutePath()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                } else {
                    val contentUriPrefixesToTry =
                        arrayOf(
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads",
                            "content://downloads/all_downloads"
                        )
                    for (contentUriPrefix in contentUriPrefixesToTry) {
                        try {
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse(contentUriPrefix),
                                fileId!!
                            )
                            val path =
                                getDataColumn(
                                    context,
                                    contentUri,
                                    null,
                                    null
                                )
                            if (path != null) {
                                return path
                            }
                        } catch (e: Exception) {
                            Log.d("content-Provider", e.message)
                        }
                    }
                }
            }
            /*val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                java.lang.Long.valueOf(id)
            )
            return getDataColumn(context, contentUri, null, null)*/
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(
                context, contentUri, selection,
                selectionArgs
            )
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        // Return the remote address
        return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
            context,
            uri,
            null,
            null
        )
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}
fun getFileId(uri: Uri): Long? {
    var strReulst: Long = 0
    try {
        val path = uri.path
        val paths = path.split("/".toRegex()).toTypedArray()
        Log.e("?dsdas?", paths[0])
        if (paths.size >= 3) {
            strReulst = paths[2].toLong()
        }
    } catch (e: NumberFormatException) {
        strReulst = File(uri.path).name.toLong()
    } catch (e: IndexOutOfBoundsException) {
        strReulst = File(uri.path).name.toLong()
    }
    return strReulst
}
fun writeFile(`in`: InputStream, file: File) {
    var out: OutputStream? = null
    try {
        out = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (`in`.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (out != null) {
                out.close()
            }
            `in`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
fun getRemovableSDCardPath(context: Context?): String {
    val storages = ContextCompat.getExternalFilesDirs(context!!, null)
    return if (storages.size > 1 && storages[0] != null && storages[1] != null) storages[1].toString() else ""
}


fun getDataColumn(
    context: Context, uri: Uri?,
    selection: String?, selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(
            uri, projection,
            selection, selectionArgs, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}


fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri
        .authority
}


fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri
        .authority
}


fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri
        .authority
}


fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri
        .authority
}