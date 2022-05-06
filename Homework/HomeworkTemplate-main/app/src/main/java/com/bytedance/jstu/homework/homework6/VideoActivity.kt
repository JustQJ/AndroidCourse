package com.bytedance.jstu.homework.homework6

import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.jstu.homework.R

class VideoActivity:AppCompatActivity() {

    private lateinit var videoview: VideoView
    private lateinit var controller: MediaController

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        videoview = findViewById(R.id.videoview)


        controller = MediaController(this)
        controller.setMediaPlayer(videoview)
        videoview.setMediaController(controller)
        videoview.holder.setFormat(PixelFormat.TRANSPARENT)
        videoview.setZOrderOnTop(true)
        videoview.setVideoPath(getVideoPath(R.raw.big_buck_bunny))

        videoview.start()




    }


    private fun getVideoPath(resId: Int): String{
        return "android.resource://" + this.packageName + "/" + resId
    }








}