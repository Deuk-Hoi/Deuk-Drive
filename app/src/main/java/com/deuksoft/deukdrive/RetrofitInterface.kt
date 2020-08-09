package com.deuksoft.deukdrive

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitInterface{
    @GET("/driveMain/get_freedisk")
    fun requestAllData():Call<GetDiskSize>
}