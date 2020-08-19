package com.deuksoft.deukdrive

import java.io.File

data class GetDiskSize(
    var diskPath: String = "",
    var free : Long = 0,
    var size : Long = 0
)

data class FileData(val data : Accepted)
data class Accepted(val accepted: HashMap<String, GetFileInfo>)
data class GetFileInfo(
    var filename : String,
    var extension : String,
    var filesize : String
)

/*data class FileData(var data : Accepted)
data class Accepted(var accepted: GetFileInfo)
data class GetFileInfo(
    var filename : String,
    var extension : String,
    var filesize : String
)*/