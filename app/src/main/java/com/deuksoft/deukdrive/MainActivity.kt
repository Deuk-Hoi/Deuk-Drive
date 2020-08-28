package com.deuksoft.deukdrive


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deuksoft.deukdrive.FileLoadManager.GetRealPath
import com.deuksoft.deukdrive.FileUploadManager.FileUpload
import com.deuksoft.deukdrive.ItemAdapter.BottomItemAdapter
import com.deuksoft.deukdrive.ItemAdapter.BottomMenuItem
import com.deuksoft.deukdrive.ItemAdapter.UserFileList
import com.deuksoft.deukdrive.ItemAdapter.UserFileListAdapter
import com.deuksoft.deukdrive.RetrofitManager.ExistFolderState
import com.deuksoft.deukdrive.RetrofitManager.RetrofitInterface
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var sliding_layout : SlidingUpPanelLayout
    lateinit var BottomMenuItem : ArrayList<BottomMenuItem> //SlidingUpPanelLayout 아이템 리스트 저장
    var  UserFileListItem : ArrayList<UserFileList> = arrayListOf()
    lateinit var name : String
    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val nav_header_view : View = navView.getHeaderView(0)
        val userstate : TextView = nav_header_view.findViewById(R.id.userstate)
        val listRefresh : SwipeRefreshLayout = findViewById(R.id.listRefresh)
        listRefresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_red_light,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light
        )

        listRefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                loadUserFileList()
                loaduserdata(user)
                listRefresh.isRefreshing = false
            }
        })

        if(intent.hasExtra("GoogleAccount"))
        {
            user = intent.getParcelableExtra("GoogleAccount")
            ExistUserFolder(user.email.toString())
            userstate.text = "${user.displayName}님 환영합니다."
            Log.e("privite?", user.email)
            loaduserdata(user)
            loadUserFileList()
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        sliding_layout = findViewById(R.id.SlidingLayout)

        var toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        userstate.setOnClickListener(this)
    }

    //모든 버튼 이벤트 처리부분
    override fun onClick(v: View?) {
        Log.e("id", v.toString())
        when(v!!.id){
            R.id.userstate -> {
                val Login = Intent(this, Login::class.java)
                startActivity(Login)
            }
        }
    }
    //뒤로가기 버튼을 눌렀을 때 이벤트
    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        else if(sliding_layout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED)
        {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED)
        }else{
            finishAffinity() //어플리케이션 완전히 종료
        }
    }

    //툴바의 메뉴 아이콘 눌렀을 때의 이벤트 처리 부분
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            android.R.id.home -> {
                return true
            }
            R.id.add_icon -> {
                AddNewFile()
                GlobalScope.launch {
                    delay(100)
                    sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED)
                }
                return true
            }
            R.id.search_icon -> {
                db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            Log.d("my", "${document.id} => ${document.data}")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("bro", "Error getting documents.", exception)
                    }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> {
                Log.e("fsdf", "hellop")
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }
        }
        var drawer : DrawerLayout = findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        //네비게이션 작동부분 구현
        var drawer : DrawerLayout = findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }



    // SlidingUpPanelLayout의 add Icon을 클릭 하였을 때의 내부 메뉴 버튼 부분
    fun AddNewFile(){
        headertxt.text = "새로 만들기"
        fileicon.visibility = View.GONE
        BottomMenuItem = arrayListOf<BottomMenuItem>(
            BottomMenuItem(R.drawable.camera, "사진 촬영"),
            BottomMenuItem(R.drawable.camera, "스캔"),
            BottomMenuItem(R.drawable.folder, "폴더 만들기"),
            BottomMenuItem(R.drawable.upload, "업로드")
        )
       val itemAdapter = BottomItemAdapter(this, BottomMenuItem){ bottomMenuItem ->
           when(bottomMenuItem.usetxt){
               "폴더 만들기" -> {
                   makeFolderDialog()
               }
               "업로드" -> {
                   permissionReadWrite(this)
                   sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED)
               }
           }
       }
        Log.e("fds", itemAdapter.toString())
        itemRecycler.adapter = itemAdapter

        val linearManger = LinearLayoutManager(this)
        itemRecycler.layoutManager = linearManger
        itemRecycler.setHasFixedSize(true) //리스트가 바뀌었을 때 반응형으로 하기위한 코드
    }

    //저장된 파일 불러오는 부분
    fun loadUserFileList(){

        var list : ArrayList<Map<String, Any>> = arrayListOf()
        var itemAdapter : UserFileListAdapter
        db.collection("FileInfo").document(user.email.toString())
            .collection("Files")
            .get()
            .addOnSuccessListener { result ->
                for(document in result){
                    var data = document.data
                    list.add(data)
                    Log.e("??", data.get("extension").toString())
                }
            }
            .addOnFailureListener{ exception ->
                Log.e("Failed", exception.toString())
            }
        GlobalScope.launch {
            delay(950)
            UserFileListItem.clear()
            for(data : Map<String, Any> in list){
                var imageFile : String
                var extension = data!!.get("extension").toString()
                var FileName = data!!.get("FileName").toString()
                var UploadDate = data!!.get("UploadDate").toString()
                var FileSize = (data!!.get("FileSize").toString()).toDouble()

                FileSize = FileSize / 1048576

                if(extension.equals("doc")||extension.equals("docx")){
                    imageFile = "doc"
                }else if(extension.equals("ppt")||extension.equals("pptx")){
                    imageFile = "ppt"
                }else if(extension.equals("xls")||extension.equals("xlsx")){
                    imageFile = "xls"
                }else if(extension.equals("jpg")||extension.equals("png")||extension.equals("jpeg")||extension.equals("gif")){
                    imageFile = "photo"
                }else if(extension.equals("pdf")){
                    imageFile = "pdf"
                }else if(extension.equals("hwp")){
                    imageFile = "hwp"
                }else{
                    imageFile = "file"
                }
                Log.e("???", " ${imageFile}, ${FileName}, ${FileSize}, ${UploadDate}")
                UserFileListItem.add(UserFileList( imageFile, FileName, String.format("%.1f",FileSize), UploadDate ))
            }

            itemAdapter = UserFileListAdapter(this@MainActivity, UserFileListItem){ userFileList ->
                clickFile(userFileList)
                GlobalScope.launch {
                    delay(100)
                    sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED)
                }
            }
            runOnUiThread(object : Runnable {
                override fun run() {
                    Fileload.adapter = itemAdapter
                    val linearManger = LinearLayoutManager(this@MainActivity)
                    Fileload.layoutManager = linearManger
                    Fileload.setHasFixedSize(true) //리스트가 바뀌었을 때 반응형으로 하기위한 코드
                }
            })
        }
    }

    fun clickFile(userFileList: UserFileList){
        headertxt.text = userFileList.FileName
        fileicon.visibility = View.VISIBLE
        val resourceId = this.resources.getIdentifier(userFileList.Fileimg,"drawable",this.packageName)
        fileicon.setImageResource(resourceId)
        BottomMenuItem = arrayListOf<BottomMenuItem>(
            BottomMenuItem(R.drawable.open, "열기"),
            BottomMenuItem(R.drawable.download, "다운로드"),
            BottomMenuItem(R.drawable.delete, "삭제")
        )
        val itemAdapter = BottomItemAdapter(this, BottomMenuItem){ bottomMenuItem ->
            when(bottomMenuItem.usetxt){

            }
        }
        itemRecycler.adapter = itemAdapter

        val linearManger = LinearLayoutManager(this)
        itemRecycler.layoutManager = linearManger
        itemRecycler.setHasFixedSize(true) //리스트가 바뀌었을 때 반응형으로 하기위한 코드
    }

    //파일 읽기 쓰기 권한 설정 다이어로그
    fun permissionReadWrite(context: Context){
        val permission : PermissionListener = object : PermissionListener{
            override fun onPermissionGranted() {
                Toast.makeText(context, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                val intent : Intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.setType("*/*")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivityForResult(Intent.createChooser(intent, "Open"), 1)
            }

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Toast.makeText(context, "권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(context)
            .setPermissionListener(permission)
            .setRationaleMessage("파일을 읽기 / 쓰기 위해서는 \n권한이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
            .setPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()

    }

    //인텐트를 통하여 결과를 반환 받을 때 사용하는 부분(파일 선택시 파일의 속성 출력 구간)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == RESULT_OK) {

            var GP: GetRealPath = GetRealPath()
            var result: String = ""
            var fileUri: Uri? = data?.data
            var cusor : Cursor? = contentResolver.query(fileUri!!, null, null, null)
            var FileName = GP.getName(fileUri, cusor!!)
            cusor.close()
            cusor = contentResolver.query(fileUri, null, null, null)
            var FileSize = GP.getFileSize(fileUri, cusor!!)
            var file : File = File(fileUri.toString())
            var extension : String = FileName.substring(FileName.lastIndexOf(".") + 1)
            Log.e(
                "???",
                "FileName : ${FileName}, FileSize : ${FileSize}, extension : ${extension}, file : ${file}"
            )
            Log.e("hi", fileUri.authority!!)//파일 종류 확인 방법
            Log.e("hello", this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString())

            var fileUpload = FileUpload()
            fileUpload.sendFile(FileName, FileSize, extension, file)
            fileUpload.newUpload(fileUri, user, this)
            fileUpload.insertFileInfo(FileName, FileSize, extension, user, db)
            fileUpload.filesizeupdate(FileSize, user, db)
        }
    }

    //새폴더를 생성할 때 나오는 Dialog생성 부분
    fun makeFolderDialog(){
        val foldername : EditText = EditText(this)
        val container : FrameLayout = FrameLayout(this)
        foldername.setTextColor(Color.WHITE)
        val params : FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        foldername.layoutParams = params
        container.addView(foldername)
        var dialog = AlertDialog.Builder(this, R.style.MyAlterDialogStyle)
        dialog.setTitle("폴더 만들기")
        dialog.setMessage("이름을 입력하시오")
        dialog.setView(container)
        var dialog_listener = object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, select: Int) {
                when(select){
                    DialogInterface.BUTTON_POSITIVE -> {
                        name = foldername.text.toString()
                        Log.e("Filename", name)
                        GlobalScope.launch {
                            delay(100)
                            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED)
                        }
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }
        }
        dialog.setPositiveButton("생성", dialog_listener)
        dialog.setNegativeButton("취소", dialog_listener)
        dialog.show()

    }



    //각 User마다 저장할 사용자 이름의 폴더가 있는지 검사하는 부분 없으면 생성.
    fun ExistUserFolder(foldername: String){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://118.42.168.26:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RetrofitInterface::class.java)
        val folder = HashMap<String, String>()
        folder.put("foldername", foldername)

        service.requestExistFile(folder).enqueue(object : Callback<ExistFolderState> {
            override fun onFailure(call: Call<ExistFolderState>, t: Throwable) {
                Log.e("message", t.message)
            }

            override fun onResponse(
                call: Call<ExistFolderState>,
                response: Response<ExistFolderState>
            ) {
                try {
                    val existFolderState = response.body()
                    Log.d("Responce::", existFolderState!!.state)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    //각 유저 마다 부여된 다른 전체 사이즈와 , 남은용량 표시
    fun loaduserdata(user: FirebaseUser){

        db.collection("UserInfo").document(user.email.toString())
            .get()
            .addOnSuccessListener { document ->
                if(document != null){
                    var data = document.data //map 형식으로 저장된다.
                    var free = (data!!.get("freesize").toString()).toDouble()
                    var full = (data!!.get("fullsize").toString()).toDouble()

                    free = free / 1073741824
                    full = full / 1073741824

                    var use = full - free
                    Log.e("free",free.toString())
                    Log.e("use",use.toString())
                    Log.e("full",full.toString())
                    freedisk.text = String.format("%.1f", use) + "GB"
                    Log.e("freeeeeeee",freedisk.text.toString())
                    fulldisk.setText(full.toInt().toString() + "GB")
                    diskprogressbar.setMax(full.toInt())
                    diskprogressbar.setProgress(use.toInt())
                    freesizetxt.setText(String.format("%.1f", free) + " GB")
                }else{
                    Log.d("data", "No such document")
                }
            }
            .addOnFailureListener{ exception ->
                Log.e("Failed", exception.toString())
            }
    }


}
