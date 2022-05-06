package com.bytedance.jstu.homework

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.widget.VideoView
import java.lang.Exception

class MyVideoView  @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int=0) : VideoView(context, attrs, defStyleAttr){

    var videoRealH = 0 //视频的实际高
    var videoRealW = 0 //视频的实际宽
    override fun setVideoPath(path: String?) {
        super.setVideoPath(path)



        try {
            var retr = MediaMetadataRetriever()
            retr.setDataSource(path)
            var height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            var width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            videoRealH = Integer.parseInt(height)
            videoRealW = Integer.parseInt(width)

        }
        catch (e: Exception){
            //Log.e(e.toString(),e.toString())
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var w = getDefaultSize(0, widthMeasureSpec)
        var h = getDefaultSize(0, heightMeasureSpec)
        //判断横竖屏
        if(w>h){
            //横屏

            setMeasuredDimension(w,h)
        }
        else{
            //竖屏，就使用原大小，不设置就是原始大小
            //setMeasuredDimension(videoRealW, videoRealH)
        }



    }


}