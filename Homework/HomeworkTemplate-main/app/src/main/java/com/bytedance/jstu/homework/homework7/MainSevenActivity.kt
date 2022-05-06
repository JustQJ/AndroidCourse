package com.bytedance.jstu.homework.homework7

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bytedance.jstu.homework.R

class MainSevenActivity :AppCompatActivity(){

    private lateinit var requestButton : Button
    companion object{
        private const val PERMISSION_REQUEST_CODE=1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seven_main)

        requestButton = findViewById(R.id.takephotoandrecord)

        requestButton.setOnClickListener(){
            requestPermission()

        }




    }
    private fun recordVideo(){
        val intent = Intent()
        intent.setClass(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun requestPermission(){
        //获取相机核录制权限
        val hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if(hasCameraPermission && hasAudioPermission){
            recordVideo()
        }
        else{
            val permission: MutableList<String> = ArrayList()
            if(!hasCameraPermission){
                permission.add(Manifest.permission.CAMERA)
            }
            if(!hasAudioPermission){
                permission.add(Manifest.permission.RECORD_AUDIO)
            }
            ActivityCompat.requestPermissions(this, permission.toTypedArray(), PERMISSION_REQUEST_CODE)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var hasPermission = true
        for(grantResult in grantResults){
            if(grantResult!=PackageManager.PERMISSION_GRANTED){
                hasPermission = false
                break
            }
        }
        if(hasPermission){
            recordVideo()
        }
        else{
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show()
        }
    }
}