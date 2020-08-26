package com.deuksoft.deukdrive.RetrofitManager

import android.util.Log
import kotlinx.android.synthetic.main.nav_header_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun loadSize(){
    /*val retrofit = Retrofit.Builder()
        .baseUrl("http://118.42.168.26:3000/driveMain/get_freedisk/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService = retrofit.create(RetrofitInterface::class.java)
    retrofitService.requestAllData().enqueue(object : Callback<GetDiskSize> {
        override fun onResponse(call: Call<GetDiskSize>, response: Response<GetDiskSize>) {
            if(response.isSuccessful)
            {
                val body = response.body()
                body?.let {

                    var free = body.free.toDouble()
                    var full = body.size.toDouble()

                    free = free / 1000000000
                    full = full / 1000000000

                    var use = full - free

                    freedisk.setText(String.format("%.1f",use) + "GB")
                    fulldisk.setText(full.toInt().toString() + "GB")
                    diskprogressbar.setMax(full.toInt())
                    diskprogressbar.setProgress(use.toInt())
                    freesizetxt.setText(String.format("%.1f",free)+" GB")
                }
            }
        }

        override fun onFailure(call: Call<GetDiskSize>, t: Throwable) {
            Log.e("this is error", t.message.toString())
        }
    })*/
}