package com.deuksoft.deukdrive.FileUploadManager

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.deuksoft.deukdrive.RetrofitManager.FileData
import com.deuksoft.deukdrive.RetrofitManager.RetrofitInterface
import com.deuksoft.deukdrive.RetrofitManager.getRealPathFromURI
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FileUpload {
    //파일의 정보 서버로 보내는 구간
    fun sendFile(FileName: String, FileSize: String, extension: String, file: File){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://118.42.168.26:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RetrofitInterface::class.java)
        val fileinfo = HashMap<String, String>()
        fileinfo.put("FileName", FileName);
        fileinfo.put("FileSize", FileSize);
        fileinfo.put("extension", extension);
        val body = HashMap<String, HashMap<String, String>>()
        body.put("accepted", fileinfo)

        service.uploadData(body).enqueue(object : Callback<FileData> {
            override fun onFailure(call: Call<FileData>, t: Throwable) {
                Log.d("CometChatAPI::", "Failed API call with call: ${call} exception: ${t}")
            }

            override fun onResponse(call: Call<FileData>, response: Response<FileData>) {
                try {
                    Log.d("Responce::", response.message())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    //파일 업로드 구간
    fun newUpload(fileUri: Uri, Path: String , context: Context){
        var currentFolder = Path.split("upload/")
        val retrofit = Retrofit.Builder()
            .baseUrl("http://118.42.168.26:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        try{
            var service = retrofit.create(RetrofitInterface::class.java)
            var a = getRealPathFromURI(context, fileUri)
            var file = File(a)
            Log.e("asddasdasds", file.path)

            var requestFile : RequestBody = RequestBody.create(MediaType.parse(context.contentResolver.getType(fileUri)), file)
            var body : MultipartBody.Part = MultipartBody.Part.createFormData("myFile", URLEncoder.encode(file.name,"utf-8"), requestFile)
            var filename : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file.name)
            var name : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"),"myFile")
            var saveUser : RequestBody = RequestBody.create( MediaType.parse("multipart/form-data"),currentFolder[1])
            var call : Call<ResponseBody> = service.upload(filename, saveUser, name, body)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                    Log.e("call", response.message())
                    if (response.code() == 200) {
                        Log.e("hello", "Succecss")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("error", t.message)
                }
            })
        }catch (e: java.lang.Exception){
            var toast = Toast.makeText(context, "내장메모리로 접근하여 사용해 주세요.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 200)
            toast.show()
        }
    }

    fun insertFileInfo(FileName: String, FileSize: String, extension: String, user: FirebaseUser, db : FirebaseFirestore, Path: String){
        var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var fileinfo = hashMapOf(
            "UserName" to user.displayName,
            "FileName" to FileName,
            "FileSize" to FileSize,
            "extension" to extension,
            "FilePath" to Path,
            "UploadDate" to dateFormat.format(Date())
        )

        db.collection("FileInfo").document(user.email.toString())
            .collection("Files")
            .add(fileinfo) // 문서가 있는지 확실하지 않은 경우 전체 문서를 실수로 덮어쓰지 않도록 새 데이터를 기존 문서와 병합하는 옵션
            .addOnSuccessListener { Log.d("Success", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Failed", "Error writing document", e) }
    }

    fun insertDirInfo(DirName: String, DirPath : String,  user: FirebaseUser, db : FirebaseFirestore, Path: String){
        var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var fileinfo = hashMapOf(
            "UserName" to user.displayName,
            "DirName" to DirName,
            "FileSize" to 0,
            "extension" to "dir",
            "DirPath" to DirPath,
            "FilePath" to Path,
            "UploadDate" to dateFormat.format(Date())
        )

        db.collection("FileInfo").document(user.email.toString())
            .collection("Files")
            //.collection("Files").document(DirName) //파일 이름 보이게 하는법
            //.set(fileinfo, SetOptions.merge()) // 문서가 있는지 확실하지 않은 경우 전체 문서를 실수로 덮어쓰지 않도록 새 데이터를 기존 문서와 병합하는 옵션
            .add(fileinfo)
            .addOnSuccessListener { Log.d("Success", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Failed", "Error writing document", e) }
    }

    fun filesizeupdate(FileSize: String, user: FirebaseUser, db : FirebaseFirestore){
        db.collection("UserInfo").document(user.email.toString())
            .get()
            .addOnSuccessListener { document ->
                if(document != null){
                    var data = document.data
                    var userfilesize = ((data!!.get("freesize").toString()).toLong() - FileSize.toLong())
                    var filesize = hashMapOf(
                        "freesize" to userfilesize
                    )
                    db.collection("UserInfo").document(user.email.toString()).update(filesize as Map<String, Long>)
                }else{
                    Log.d("data", "No such document")
                }
            }
            .addOnFailureListener{ exception ->
                Log.e("Failed", exception.toString())
            }
    }
}

