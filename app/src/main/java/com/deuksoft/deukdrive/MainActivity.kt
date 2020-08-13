package com.deuksoft.deukdrive

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.marginLeft
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.deuksoft.deukdrive.ItemAdapter.BottomItemAdapter
import com.deuksoft.deukdrive.ItemAdapter.BottomMenuItem
import com.google.android.material.navigation.NavigationView
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
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    lateinit var sliding_layout : SlidingUpPanelLayout
    lateinit var BottomMenuItem : ArrayList<BottomMenuItem> //SlidingUpPanelLayout 아이템 리스트 저장
    lateinit var name : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        sliding_layout = findViewById(R.id.SlidingLayout)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        var toggle = ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(null)

        val arraylist2 = ArrayList<String>()
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, arraylist2)
        adapter2.add("득")
        adapter2.add("회")
        adapter2.add("김")
        adapter2.add("득")
        adapter2.add("회")
        adapter2.add("김")
        adapter2.add("득")
        adapter2.add("회")
        adapter2.add("김")
        adapter2.add("득")
        adapter2.add("회")
        adapter2.add("김")

        adapter2.notifyDataSetChanged()
        Fileload.adapter = adapter2;

        loadSize()
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        else if(sliding_layout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED)
        {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            android.R.id.home->{
                return true
            }
            R.id.add_icon->{
                AddNewFile()
                GlobalScope.launch {
                    delay(100)
                    sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED)
                }
                return true
            }
            R.id.search_icon->{
                permissionReadWrite(this)

            }
        }

        return super.onOptionsItemSelected(item)
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
        BottomMenuItem = arrayListOf<BottomMenuItem>(
            BottomMenuItem(R.drawable.camera, "사진 촬영"),
            BottomMenuItem(R.drawable.camera, "스캔"),
            BottomMenuItem(R.drawable.folder, "폴더 만들기"),
            BottomMenuItem(R.drawable.upload, "업로드")
        )
       val itemAdapter = BottomItemAdapter(this, BottomMenuItem){bottomMenuItem ->
           when(bottomMenuItem.usetxt){
               "폴더 만들기" -> {makeFolderDialog()}
           }
       }
        itemRecycler.adapter = itemAdapter

        val linearManger = LinearLayoutManager(this)
        itemRecycler.layoutManager = linearManger
        itemRecycler.setHasFixedSize(true)
    }

    //파일 읽기 쓰기 권한 설정 다이어로그
    fun permissionReadWrite(context : Context){
        val permission : PermissionListener = object : PermissionListener{
            override fun onPermissionGranted() {
                Toast.makeText(context, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                val intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("*/*")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivityForResult(Intent.createChooser(intent,"Open"), 1)
            }

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Toast.makeText(context, "권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(context)
            .setPermissionListener(permission)
            .setRationaleMessage("파일을 읽기 / 쓰기 위해서는 \n권한이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == RESULT_OK){
            var fileUri : Uri? = data?.data
            Log.e("뭐가올까요?? ",fileUri.toString())
        }
    }




    //새폴더를 생성할 때 나오는 Dialog생성 부분
    fun makeFolderDialog(){
        val foldername : EditText = EditText(this)
        val container : FrameLayout = FrameLayout(this)
        foldername.setTextColor(Color.WHITE)
        val params : FrameLayout.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
                        Log.e("Filename",name)
                        GlobalScope.launch {
                            delay(100)
                            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED)
                        }
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {}

                }
            }
        }
        dialog.setPositiveButton("생성", dialog_listener)
        dialog.setNegativeButton("취소", dialog_listener)
        dialog.show()

    }



    //서버에서 하드디스크 전체용량과 남은용량을 받는 부분
    fun loadSize(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://210.219.231.26:3000/driveMain/get_freedisk/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService = retrofit.create(RetrofitInterface::class.java)
        retrofitService.requestAllData().enqueue(object : Callback<GetDiskSize>{
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
        })
    }
}
