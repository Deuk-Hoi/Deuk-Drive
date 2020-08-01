package com.deuksoft.deukdrive

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_main.*

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
    }

    override fun onBackPressed() {
        var drawer : DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            //Dialog()
        }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //상단 메뉴
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
}