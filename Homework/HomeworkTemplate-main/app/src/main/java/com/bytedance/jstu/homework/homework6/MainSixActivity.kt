package com.bytedance.jstu.homework.homework6

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.jstu.homework.R

class MainSixActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_six_main)

        val ImageButton = findViewById<Button>(R.id.imageshow)
        val VideoButton = findViewById<Button>(R.id.videoshow)

        ImageButton.setOnClickListener(){
            val intent = Intent()
            intent.setClass(this, ImageActivity::class.java)
            startActivity(intent)
        }

        VideoButton.setOnClickListener(){
            val intent = Intent()
            intent.setClass(this, VideoActivity::class.java)
            startActivity(intent)
        }



    }


}