package com.bytedance.jstu.homework.homework7

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.os.Bundle

import android.view.SurfaceView
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.jstu.homework.R
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Environment
import android.view.SurfaceHolder
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import com.bytedance.jstu.homework.homework7.PathUtils.rotateImage
import java.util.*

class CameraActivity:AppCompatActivity(),SurfaceHolder.Callback {

    private lateinit var surfaceview: SurfaceView
    private lateinit var takephotosButton: Button
    private lateinit var recordvideoButton: Button
    private lateinit var imageviewshow: ImageView
    private lateinit var videoviewshow: VideoView
    private var camera: Camera? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var mp4Path = ""
    private lateinit var holder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seven_camera)
        surfaceview = findViewById(R.id.surfaceview)
        takephotosButton = findViewById(R.id.takephotos)
        recordvideoButton = findViewById(R.id.record)
        imageviewshow = findViewById(R.id.sevenimageview)
        videoviewshow = findViewById(R.id.sevenvideoview)
        holder = surfaceview.holder
        initCamera()
        holder.addCallback(this)

        takephotosButton.setOnClickListener(){
            takePhoto()
        }
        recordvideoButton.setOnClickListener(){
            record()
        }

    }

    private fun initCamera(){
        camera = Camera.open()

        camera?.let {
            val parameters = it.parameters
            parameters.pictureFormat = ImageFormat.JPEG
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            parameters["orientation"] = "portrait"
            parameters["rotation"] = 90
            it.parameters = parameters
            it.setDisplayOrientation(90)
        }
    }

    private fun prepareVideoRecorder():Boolean{
        val mediaRecorder = MediaRecorder()
        this.mediaRecorder = mediaRecorder
        camera?.unlock()
        mediaRecorder.setCamera(camera)

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))

        mp4Path = outputMediaPath
        mediaRecorder.setOutputFile(mp4Path)

        mediaRecorder.setPreviewDisplay(holder.surface)
        mediaRecorder.setOrientationHint(90)


        try {
            mediaRecorder.prepare()
        }catch (e: IllegalStateException){
            releaseMediaRecorder()
            return false
        }catch (e: IOException){
            releaseMediaRecorder()
            return false
        }
        return true

    }

    private fun releaseMediaRecorder(){
        mediaRecorder?.let{ mediaRecorder ->
            mediaRecorder.reset()
            mediaRecorder.release()
            this.mediaRecorder = null
            camera?.lock()
        }
    }

    private val outputMediaPath:String
        private get(){
            val mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val mediaFile = File(mediaStorageDir,"IMG_$timeStamp.mp4")
            if(!mediaFile.exists()){
                mediaFile.parentFile.mkdir()
            }
            return mediaFile.absolutePath

        }

    fun takePhoto(){
        camera?.takePicture(null,null,pictureCallback)
    }

    var pictureCallback = Camera.PictureCallback{data, camera ->
        var fos:FileOutputStream? = null
        val filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + File.separator + "1.jpg"
        val file = File(filePath)
        try{
            fos = FileOutputStream(file)
            fos.write(data)
            fos.flush()
            val bitmap = BitmapFactory.decodeFile(filePath)
            val rotateBitmap = rotateImage(bitmap, filePath)

            imageviewshow.visibility = View.VISIBLE
            videoviewshow.visibility = View.GONE
            imageviewshow.setImageBitmap(rotateBitmap)
        }catch (e: Exception) {
            e.printStackTrace()
        }finally {
            this.camera?.startPreview()
            if(fos!=null){
                try {
                    fos.close()
                }catch (e: IOException){
                    e.printStackTrace()
                }
            }
        }

    }

    fun record(){
        if(isRecording && mediaRecorder!=null){
            recordvideoButton.text="录制"
            val mediaRecorder = this.mediaRecorder?:return
            mediaRecorder.setOnErrorListener(null)
            mediaRecorder.setOnInfoListener(null)
            mediaRecorder.setPreviewDisplay(null)
            try{
                mediaRecorder.stop()
            }catch (e:Exception)
            {
                e.printStackTrace()
            }
            mediaRecorder.reset()
            mediaRecorder.release()
            this.mediaRecorder = null
            camera?.lock()
            videoviewshow.visibility = View.VISIBLE
            imageviewshow.visibility = View.GONE
            videoviewshow.setVideoPath(mp4Path)
            videoviewshow.start()

        }else{
            if(prepareVideoRecorder()){
                recordvideoButton.text = "暂停"
                mediaRecorder!!.start()
            }
        }
        isRecording = !isRecording
    }

    override fun surfaceCreated(holder: SurfaceHolder){
        try {
            camera?.let {
                it.setPreviewDisplay(holder)
                it.startPreview()
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder,format: Int, width: Int, height: Int){
        if(holder.surface == null)
            return

        camera?.stopPreview()

        try {
            camera?.let {
                it.setPreviewDisplay(holder)
                it.startPreview()
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    override fun surfaceDestroyed(holder: SurfaceHolder)
    {
        camera?.let { it.stopPreview()
            it.release()
        }
    }

    override fun onResume(){
        super.onResume()
        if(camera == null)
            initCamera()
        camera?.startPreview()
    }

    override fun onPause(){
        super.onPause()
        camera?.stopPreview()
    }



}