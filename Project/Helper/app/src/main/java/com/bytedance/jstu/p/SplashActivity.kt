package com.bytedance.jstu.p

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity:AppCompatActivity() {
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val skipBtn = findViewById<TextView>(R.id.skip)
        val runnable = Runnable { //跳转到首页
            jumpToHomepage()
        }
        handler.postDelayed(runnable, 3000)
        skipBtn.setOnClickListener {
            handler.removeCallbacks(runnable)
            jumpToHomepage()
        }
    }

    private fun jumpToHomepage() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun finish() {
        super.finish()
        handler.removeCallbacksAndMessages(null)
    }
}