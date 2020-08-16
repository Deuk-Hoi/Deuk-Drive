package com.deuksoft.deukdrive

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

}