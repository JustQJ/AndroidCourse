package com.bytedance.jstu.homework.homework2

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.jstu.homework.R
import java.io.ObjectStreamException

class triple_Activity : AppCompatActivity() {

    var judge1 = 1
    var judge2 = 1
    var judge3 = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_triple_likes)

        //当点赞时，出现三联的效果
        val liked = findViewById<View>(R.id.like)
        val cir11 = findViewById<View>(R.id.cir1)
        val cir22 = findViewById<View>(R.id.cir2)
        val moenyid = findViewById<View>(R.id.memony)
        val collectid = findViewById<View>(R.id.collect)
        val like11 = findViewById<View>(R.id.like1)
        val money11 = findViewById<View>(R.id.memony1)
        val collection11 = findViewById<View>(R.id.collect1)
        liked.setOnClickListener(){

            //点赞图标的变化和圆环的变化
            val likeBigX = ObjectAnimator.ofFloat(liked,"scaleX",1f,1.2f)
            likeBigX.setDuration(1000)
            val likeBigY = ObjectAnimator.ofFloat(liked,"scaleY",1f,1.2f)
            likeBigY.setDuration(1000)
            val likesmallX = ObjectAnimator.ofFloat(liked,"scaleX",1.2f,1f)
            likesmallX.setDuration(1000)
            val likesmallY = ObjectAnimator.ofFloat(liked,"scaleY",1.2f,1f)
            likesmallY.setDuration(1000)
            val rotate1 =  ObjectAnimator.ofFloat(liked,"rotation",0f,10f)
            rotate1.setDuration(100)
            val rotate2 =  ObjectAnimator.ofFloat(liked,"rotation",10f,-10f)
            rotate2.setDuration(200)
            val rotate3 =  ObjectAnimator.ofFloat(liked,"rotation",-10f,10f)
            rotate3.setDuration(200)
            val rotate4 =  ObjectAnimator.ofFloat(liked,"rotation",10f,-10f)
            rotate4.setDuration(200)
            val rotate5 =  ObjectAnimator.ofFloat(liked,"rotation",-10f,10f)
            rotate5.setDuration(200)
            val rotate6 =  ObjectAnimator.ofFloat(liked,"rotation",10f,-10f)
            rotate6.setDuration(200)
            val rotate7 =  ObjectAnimator.ofFloat(liked,"rotation",-10f,10f)
            rotate7.setDuration(200)
            val rotate8 =  ObjectAnimator.ofFloat(liked,"rotation",10f,-10f)
            rotate8.setDuration(200)
            val rotate9 =  ObjectAnimator.ofFloat(liked,"rotation",-10f,10f)
            rotate9.setDuration(200)
            val rotate10 =  ObjectAnimator.ofFloat(liked,"rotation",10f,0f)
            rotate10.setDuration(100)




            val circle1 = ObjectAnimator.ofFloat(cir11,"startArc",270f,-90f)
            circle1.setDuration(2000)
            val circle2 = ObjectAnimator.ofFloat(cir22,"startArc",270f,-90f)
            circle2.setDuration(2000)




            //然后红边消失，三个图像变红，然后再变大，再缩小回到原样

            val circle11 = ObjectAnimator.ofFloat(cir11,"startArc",270f,270f)
            circle11.setDuration(200)
            val circle22 = ObjectAnimator.ofFloat(cir22,"startArc",270f,270f)
            circle22.setDuration(200)
            val likeRed = ObjectAnimator.ofFloat(liked,"alpha",1f,0f)
            likeRed.setDuration(2)
            val moneyRed = ObjectAnimator.ofFloat(moenyid,"alpha",1f,0f)
            moneyRed.setDuration(2)
            val collectionRed = ObjectAnimator.ofFloat(collectid,"alpha",1f,0f)
            collectionRed.setDuration(2)

            val likeRed1X = ObjectAnimator.ofFloat(like11,"scaleX",1f,1.2f)
            likeRed1X.setDuration(1000)
            val likeRed1Y = ObjectAnimator.ofFloat(like11,"scaleY",1f,1.2f)
            likeRed1Y.setDuration(1000)
            val likeRed1X1 = ObjectAnimator.ofFloat(like11,"scaleX",1.2f,1f)
            likeRed1X1.setDuration(1000)
            val likeRed1Y1 = ObjectAnimator.ofFloat(like11,"scaleY",1.2f,1f)
            likeRed1Y1.setDuration(1000)
            val moneyRed1X = ObjectAnimator.ofFloat(money11,"scaleX",1f,1.2f)
            moneyRed1X.setDuration(1000)
            val moneyRed1Y = ObjectAnimator.ofFloat(money11,"scaleY",1f,1.2f)
            moneyRed1Y.setDuration(1000)
            val moneyRed1X1 = ObjectAnimator.ofFloat(money11,"scaleX",1.2f,1f)
            moneyRed1X1.setDuration(1000)
            val moneyRed1Y1 = ObjectAnimator.ofFloat(money11,"scaleY",1.2f,1f)
            moneyRed1Y1.setDuration(1000)


            val collectionRed1X = ObjectAnimator.ofFloat(collection11,"scaleX",1f,1.2f)
            collectionRed1X.setDuration(1000)
            val collectionRed1Y = ObjectAnimator.ofFloat(collection11,"scaleY",1f,1.2f)
            collectionRed1Y.setDuration(1000)
            val collectionRed1X1 = ObjectAnimator.ofFloat(collection11,"scaleX",1.2f,1f)
            collectionRed1X1.setDuration(1000)
            val collectionRed1Y1 = ObjectAnimator.ofFloat(collection11,"scaleY",1.2f,1f)
            collectionRed1Y1.setDuration(1000)


            //恢复原样
            val likeRed2 = ObjectAnimator.ofFloat(liked,"alpha",0f,1f)
            likeRed2.setDuration(2)
            val moneyRed2 = ObjectAnimator.ofFloat(moenyid,"alpha",0f,1f)
            moneyRed2.setDuration(2)
            val collectionRed2 = ObjectAnimator.ofFloat(collectid,"alpha",0f,1f)
            collectionRed2.setDuration(2)


            /*不能连写，都是以传入play的动画为基准执行后面的
*   animatorSet.play(rotate1).before(rotate2).before(rotate3) rotate2和3都是以1为基准的，2和3没有关系
* */
            val animatorSet = AnimatorSet()
            animatorSet.play(likeBigX).with(likeBigY).before(likesmallX).with(rotate1).with(circle1).with(circle2)
            animatorSet.play(likesmallX).with(likesmallY)
            animatorSet.playSequentially(rotate1,rotate2,rotate3,rotate4,rotate5,rotate6,rotate7,rotate8,rotate9,rotate10)
            animatorSet.play(circle11).with(circle22).after(rotate10)
            animatorSet.play(likeRed).with(moneyRed).with(collectionRed).after(circle11)
            animatorSet.play(likeRed1X).with(likeRed1Y).with(moneyRed1X).with(moneyRed1Y).with(collectionRed1X).with(collectionRed1Y).after(likeRed)
            animatorSet.play(likeRed1X1).with(likeRed1Y1).with(moneyRed1X1).with(moneyRed1Y1).with(collectionRed1X1).with(collectionRed1Y1).after(likeRed1X)
            animatorSet.play(likeRed2).with(moneyRed2).with(collectionRed2).after(likeRed1X1)
            animatorSet.start()











        }









        //当点踩时，踩变红

        bindActivity1(R.id.dislike)

        //当点收藏

        bindActivity2(R.id.memony)

        //点投币

       bindActivity3(R.id.collect)


        //点返回，回到上一个页面
        findViewById<View>(R.id.returning).setOnClickListener(){

            val intent = Intent()
            intent.setClass(this,MainTwoActivity::class.java)
            startActivity(intent)
        }


    }


    private fun bindActivity1(btnId: Int) {


        findViewById<View>(btnId).setOnClickListener (){
            if (judge1==1)
            {
                val alphaAnimation1 = AlphaAnimation(1f,0f)
                findViewById<View>(btnId).startAnimation(alphaAnimation1)
                alphaAnimation1.setFillAfter(true)
               judge1=0
            }
            else
            {
                val alphaAnimation2 = AlphaAnimation(0f,1f)
                findViewById<View>(btnId).startAnimation(alphaAnimation2)
                alphaAnimation2.setFillAfter(true)
                judge1=1
            }

        }
    }
    private fun bindActivity2(btnId: Int) {


        findViewById<View>(btnId).setOnClickListener (){
            if (judge2==1)
            {
                val alphaAnimation1 = AlphaAnimation(1f,0f)
                findViewById<View>(btnId).startAnimation(alphaAnimation1)
                alphaAnimation1.setFillAfter(true)
                judge2=0

            }
            else
            {
                val alphaAnimation2 = AlphaAnimation(0f,1f)
                findViewById<View>(btnId).startAnimation(alphaAnimation2)
                alphaAnimation2.setFillAfter(true)
                judge2=1
            }

        }
    }
    private fun bindActivity3(btnId: Int) {


        findViewById<View>(btnId).setOnClickListener (){
            if (judge3==1)
            {
                val alphaAnimation1 = AlphaAnimation(1f,0f)
                findViewById<View>(btnId).startAnimation(alphaAnimation1)
                alphaAnimation1.setFillAfter(true)
                judge3=0

            }
            else
            {
                val alphaAnimation2 = AlphaAnimation(0f,1f)
                findViewById<View>(btnId).startAnimation(alphaAnimation2)
                alphaAnimation2.setFillAfter(true)
                judge3=1
            }

        }
    }

}
