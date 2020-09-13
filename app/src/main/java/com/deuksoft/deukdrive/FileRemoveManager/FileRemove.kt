package com.deuksoft.deukdrive.FileRemoveManager

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.deuksoft.deukdrive.RetrofitManager.RemoveFile
import com.deuksoft.deukdrive.RetrofitManager.RetrofitInterface
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FileRemove {

    lateinit var user : FirebaseUser
    lateinit var db : FirebaseFirestore
    lateinit var document: DocumentSnapshot
    lateinit var context: Context
    fun removeFile(context: Context, FilePath : String, FileName : String, user: FirebaseUser, db : FirebaseFirestore){

        this.user = user
        this.db = db
        this.context = context
        val retrofit = Retrofit.Builder()
            .baseUrl("http://118.42.168.26:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val FilePathHash = HashMap<String, String>()
        FilePathHash.put("FilePath", FilePath+FileName)
        db.collection("FileInfo").document(user.email.toString())
            .collection("Files")
            .get()
            .addOnSuccessListener { result ->
                var Filelog = result.documents
                //FilePath = Filelog.get(0).data!!.get("FilePath").toString() //현재 로드된 파일의 경로 확인
                for(document in Filelog){
                    if(document.data!!.get("FilePath").toString().equals(FilePath) && document.data!!.get("FileName").toString().equals(FileName)){
                        Log.e("file", document.id)
                        this.document = document
                        removeServer(retrofit, FilePathHash)
                    }
                }

            }
            .addOnFailureListener{ exception ->
                Log.e("Failed", exception.toString())
            }
    }

    fun removeServer(retrofit : Retrofit, FilePathHash : HashMap<String, String>){
        val service = retrofit.create(RetrofitInterface::class.java)
        service.removeFile(FilePathHash).enqueue(object : Callback<RemoveFile> {
            override fun onFailure(call: Call<RemoveFile>, t: Throwable) {
                Log.d("CometChatAPI::", "Failed API call with call: ${call} exception: ${t}")
            }

            override fun onResponse(call: Call<RemoveFile>, response: Response<RemoveFile>) {
                try {
                    Log.e("log", response.body()!!.state)
                    if(response.code()==200){
                        removeDB()
                        Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun removeDB(){
        db.collection("FileInfo").document(user.email.toString())
            .collection("Files").document(document.id)
            .delete()
            .addOnSuccessListener {
                Log.d("Success Delete", "DocumentSnapshot successfully deleted!")
                updateDB()
            }
            .addOnFailureListener { e -> Log.w("Faile Delete", "Error deleting document", e) }
    }

    fun updateDB(){
        db.collection("UserInfo").document(user.email.toString())
            .get()
            .addOnSuccessListener { document ->
                if(document != null){
                    var data = document.data
                    var userfilesize = ((data!!.get("freesize").toString()).toLong() + this.document.data!!.get("FileSize").toString().toLong())
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