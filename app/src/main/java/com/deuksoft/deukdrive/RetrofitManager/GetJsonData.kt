package com.deuksoft.deukdrive.RetrofitManager

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

data class ExistFolderState(var state : String)

data class RemoveFile(var FilePath : HashMap<String, String>, var state: String)