package com.bytedance.jstu.homework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.bytedance.jstu.homework.homework1.MainOneActivity
import com.bytedance.jstu.homework.homework2.MainTwoActivity
import com.bytedance.jstu.homework.homework3.MainThreeActivity
import com.bytedance.jstu.homework.homework4.MainFourActivity
import com.bytedance.jstu.homework.homework5.MainFiveActivity
import com.bytedance.jstu.homework.homework6.MainSixActivity
import com.bytedance.jstu.homework.homework7.MainSevenActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addHomework("作业一", MainOneActivity::class.java)
        addHomework("作业二", MainTwoActivity::class.java)
        addHomework("作业三", MainThreeActivity::class.java)
        addHomework("作业四", MainFourActivity::class.java)
        addHomework("作业五", MainFiveActivity::class.java)
        addHomework("作业六", MainSixActivity::class.java)
        addHomework("作业七", MainSevenActivity::class.java)


    }

    private fun addHomework(text: String, activityClass: Class<*>){
        val btn = AppCompatButton(this) //button的一个子类
        btn.text = text
        btn.isAllCaps = false
        findViewById<ViewGroup>(R.id.container).addView(btn)
        btn.setOnClickListener{
            startActivity(Intent().apply {
                setClass(this@MainActivity, activityClass)
            })
        }
    }
}
