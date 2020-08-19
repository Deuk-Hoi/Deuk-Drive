package com.deuksoft.deukdrive

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PostRetrofit{
    //@Multipart
    @Headers("accept: application/json","content-type: application/json")
    @POST("/driveMain/test/")
    fun test(
        @Body body: HashMap<String, HashMap<String, String>>
    ): Call<FileData>
}