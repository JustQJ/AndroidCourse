package com.bytedance.jstu.homework.homework3
import android.util.AttributeSet
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import java.util.*

class ClockView @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int=0) : View(context, attrs, defStyleAttr){
        val myPaint = Paint()




        //获取当前的时间，时，分，秒
        val calendar = Calendar.getInstance()
        var curSecond = calendar.get(Calendar.SECOND)
        var curMinute = calendar.get(Calendar.MINUTE)
        var curHour = calendar.get(Calendar.HOUR)


        init {
            myPaint.style = Paint.Style.FILL
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
            //启动一个线程更新时间，然后刷新页面
            Thread{
                while (true){
                    Thread.sleep(1000)
                    val calendar = Calendar.getInstance(Locale.CHINA)

                    curSecond = calendar.get(Calendar.SECOND)
                    curMinute = calendar.get(Calendar.MINUTE)
                    curHour = calendar.get(Calendar.HOUR)



                    invalidate() //每次重新绘制
                }

            }.start()


        }



        override fun onDraw(canvas: Canvas?){
            super.onDraw(canvas)
            //画表盘
            drawDail(canvas)

            //画指针
            drawPointer(canvas)

        }



        fun drawDail(canvas: Canvas?){

            //画表盘

            //刻度的长度
            val dr = 30
            //表盘的外圆半径，
            val outR = Math.min(width,height)/2f
            //内圆半径
            val innerR = outR-dr
            //圆心坐标
            val x0 = width/2f
            val y0 = height/2f

            var t = 0
            while(t<60){ //分刻度，计算每个刻度的位置，然后绘制
                if (t%5 == 0)
                {
                    myPaint.color = Color.RED
                    myPaint.strokeWidth = 9f
                }
                else
                {
                    myPaint.color = Color.GREEN
                    myPaint.strokeWidth = 6f
                }

                canvas?.drawLine(x0+innerR*Math.cos((t*6)*Math.PI/180).toFloat(),y0+innerR*Math.sin((t*6)*Math.PI/180).toFloat(),x0+outR*Math.cos((t*6)*Math.PI/180).toFloat(),y0+outR*Math.sin((t*6)*Math.PI/180).toFloat(),myPaint)
                t=t+1
            }


        }

        fun drawPointer(canvas: Canvas?){

            //刻度的长度
            val dr = 30
            //表盘的外圆半径，
            val outR = Math.min(width,height)/2f
            //内圆半径
            val innerR = outR-dr
            //圆心坐标
            val x0 = width/2f
            val y0 = height/2f

            val secondlen = innerR-35
            val minutelen = (secondlen*0.8).toFloat()
            val hourlen = (minutelen*2/3).toFloat()

            //秒针
            myPaint.color = Color.YELLOW
            myPaint.strokeWidth = 9f
            canvas?.drawLine(x0,y0,x0+secondlen*Math.cos((curSecond*6-90)*Math.PI/180).toFloat(),y0+secondlen*Math.sin((curSecond*6-90)*Math.PI/180).toFloat(),myPaint)

            //分针
            myPaint.color = Color.BLUE
            myPaint.strokeWidth = 12f
            canvas?.drawLine(x0,y0,x0+minutelen*Math.cos((curMinute*6-90)*Math.PI/180).toFloat(),y0+minutelen*Math.sin((curMinute*6-90)*Math.PI/180).toFloat(),myPaint)

            //时针
            myPaint.color = Color.BLACK
            myPaint.strokeWidth = 15f
            curHour = curHour%12
            canvas?.drawLine(x0,y0,x0+hourlen*Math.cos(((curHour+curMinute/60f)*30-90)*Math.PI/180).toFloat(),y0+hourlen*Math.sin(((curHour+curMinute/60f)*30-90)*Math.PI/180).toFloat(),myPaint)

        }


}