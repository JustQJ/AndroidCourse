package com.bytedance.jstu.homework.homework2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.jstu.homework.R

class MainTwoActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_main)



        findViewById<View>(R.id.first).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,triple_Activity::class.java)
            startActivity(intent)
        }





    }

}