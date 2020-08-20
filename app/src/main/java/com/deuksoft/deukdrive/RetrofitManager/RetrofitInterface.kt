package com.deuksoft.deukdrive.RetrofitManager

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface RetrofitInterface{
    @GET("/driveMain/get_freedisk")
    fun requestAllData():Call<GetDiskSize>

    @Headers("accept: application/json","content-type: application/json")
    @POST("/driveMain/uploadFile/")
    fun uploadData(
        @Body body: HashMap<String, HashMap<String, String>>
    ): Call<FileData>

    @Multipart
    @POST("/driveMain/create/")
    fun upload(
        @Part("filename") filename : RequestBody,
        @Part file : MultipartBody.Part,
        @Part("myFile") name: RequestBody
    ): Call<ResponseBody>
}

/**/