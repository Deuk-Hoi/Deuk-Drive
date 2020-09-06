package com.deuksoft.deukdrive

import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide

class ImageFreeView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_free_view)
        setSupportActionBar(findViewById(R.id.Imagetoolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        var filePath = intent.getStringExtra("filePath")
        var fileName = intent.getStringExtra("fileName")
        var url = "http://118.42.168.26:3000/${filePath}${fileName}"
        setTitle(fileName)

        var FreeViewImage = findViewById<ImageView>(R.id.FreeViewImage)
        Glide.with(this).load(url).into(FreeViewImage);
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*menuInflater.inflate(R.menu.main, menu)*/
        return true
    }
}