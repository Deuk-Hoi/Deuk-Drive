package com.deuksoft.deukdrive

data class GetDiskSize(
    var diskPath: String = "",
    var free : Long = 0,
    var size : Long = 0
)