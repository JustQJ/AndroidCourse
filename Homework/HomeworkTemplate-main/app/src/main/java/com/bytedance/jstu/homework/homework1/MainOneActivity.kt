package com.bytedance.jstu.homework.homework1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import com.bytedance.jstu.homework.R
import com.bytedance.jstu.homework.homework1.titles.FirstTitleActivity
import com.bytedance.jstu.homework.homework1.titles.SecondTitleActivity
import com.bytedance.jstu.homework.homework1.titles.ThirdTitleActivity
import com.bytedance.jstu.homework.homework1.titles.ForthTitleActivity
import com.bytedance.jstu.homework.homework1.titles.FifthTitleActivity
import com.bytedance.jstu.homework.homework1.titles.SixthTitleActivity
import com.bytedance.jstu.homework.homework1.titles.SeventhTitleActivity
import com.bytedance.jstu.homework.homework1.titles.EighthTitleActivity
import com.bytedance.jstu.homework.homework1.titles.NinthTitleActivity
import com.bytedance.jstu.homework.homework1.titles.TenthTitleActivity

class MainOneActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_main)

        findViewById<View>(R.id.firstLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,FirstTitleActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.secondLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,SecondTitleActivity::class.java)
            startActivity(intent)
        }


        findViewById<View>(R.id.thirdLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,ThirdTitleActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.forthLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,ForthTitleActivity::class.java)
            startActivity(intent)
        }


        findViewById<View>(R.id.fifthLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,FifthTitleActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.sixthLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,SixthTitleActivity::class.java)
            startActivity(intent)
        }


        findViewById<View>(R.id.seventhLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,SeventhTitleActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.eighthLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,EighthTitleActivity::class.java)
            startActivity(intent)
        }


        findViewById<View>(R.id.ninthLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,NinthTitleActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.tenthLine).setOnClickListener{
            val intent = Intent()
            intent.setClass(this,TenthTitleActivity::class.java)
            startActivity(intent)
        }


    }

}