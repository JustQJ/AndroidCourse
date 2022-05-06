package com.bytedance.jstu.homework.homework6

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bytedance.jstu.homework.R

class ImageActivity:AppCompatActivity() {

    private val imagelist: MutableList<View> = ArrayList()
    lateinit var viewPager: ViewPager
    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        viewPager = findViewById(R.id.viewshow)

        addImage("https://t7.baidu.com/it/u=4162611394,4275913936&fm=193&f=GIF")
        addImage("https://cdn.pixabay.com/photo/2022/01/16/23/12/desert-6943430__480.jpg")
        addImage("https://cdn.pixabay.com/photo/2021/12/07/16/43/christmas-rose-6853652__340.jpg")
        addImage("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fdingyue.ws.126.net%2F2021%2F0506%2Fe2fa9a51g00qsnux3000wc0008w006og.gif&refer=http%3A%2F%2Fdingyue.ws.126.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1652177518&t=5cdd5baaedb39c604ecb637ab22a7da3")
        addImage("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp5.itc.cn%2Fq_70%2Fimages03%2F20200916%2F9670e51911c342f69c7de6e29e10a03b.gif&refer=http%3A%2F%2Fp5.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1652177518&t=182d0c54adf5bcd13a754cf6eaf770bb")
        addImage("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fdingyue.ws.126.net%2F2021%2F0506%2Fda35f1ddg00qsnuy300jzc000f000dkg.gif&refer=http%3A%2F%2Fdingyue.ws.126.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1652177518&t=ec409441d24217de52f622a9a66ffa96")
        val adapter = ViewAdapter()
        adapter.setDatas(imagelist)
        viewPager.adapter = adapter



    }
    private fun addImage(url: String){
        val imageV = layoutInflater.inflate(R.layout.activity_image_item, null) as ImageView
        Glide.with(this)
            .load(url)
            .apply(RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL))
            .error(R.drawable.loading)
            .into(imageV)
        imagelist.add(imageV)
    }




}