package com.deuksoft.deukdrive

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        var toggle = ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(null)

        val arraylist = ArrayList<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arraylist)
        adapter.add("득")
        adapter.add("회")
        adapter.add("김")
        adapter.notifyDataSetChanged()
        listView.adapter = adapter;


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


        test.setOnClickListener{
            text.setText(test.text.toString())
            sliding.animateOpen();
        }
        loadSize()
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }else if(sliding.isOpened) {
            sliding.animateClose()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            android.R.id.home->{
                return true
            }
            R.id.add_icon->{
                Log.e("dsfdfd","dsfs");
                sliding.animateOpen()
                return true
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

    fun loadSize(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.35.230:3000/driveMain/get_freedisk/")
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