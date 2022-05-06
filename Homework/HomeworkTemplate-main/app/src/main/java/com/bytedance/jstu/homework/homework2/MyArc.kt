package com.bytedance.jstu.homework.homework2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class MyArc @JvmOverloads constructor(context: Context,
                                      attrs: AttributeSet? = null,
                                      defStyleAttr: Int = 0) : View(context,attrs,defStyleAttr){

    private var color: Int = Color.WHITE
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var myPaint : Paint = Paint()
    private var endArc = 270f;
    private var startArc = -90f

    public fun getStartArc(): Float {
        return startArc
    }
    public fun setStartArc(starc:Float){
        this.startArc = starc
        invalidate()

    }

    init {
        paint.color = color
        myPaint.color = color
        myPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var radius = Math.min(width,height)/2f-18f


        var h = height/2
        var w = width/2
        val oval: RectF = RectF(w-radius, h-radius, w+radius, h+radius)

        canvas?.drawArc(oval,startArc,endArc-startArc,true,myPaint)
    }
}