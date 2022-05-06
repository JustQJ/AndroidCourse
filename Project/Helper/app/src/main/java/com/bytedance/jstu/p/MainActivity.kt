package com.bytedance.jstu.p

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.services.core.ServiceSettings
import com.bumptech.glide.Glide
import com.bytedance.jstu.p.adapter.ViewAdapter
import com.bytedance.jstu.p.apiclass.*
import com.bytedance.jstu.p.bathe.BathCheckActivity
import com.bytedance.jstu.p.money.MoneyMainActivity
import com.github.clans.fab.FloatingActionButton
import com.google.gson.GsonBuilder
import com.jinrishici.sdk.android.JinrishiciClient
import com.jinrishici.sdk.android.factory.JinrishiciFactory
import com.jinrishici.sdk.android.listener.JinrishiciCallback
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException
import com.jinrishici.sdk.android.model.PoetySentence
import com.qweather.sdk.bean.air.AirNowBean
import com.qweather.sdk.bean.base.Code
import com.qweather.sdk.bean.base.Lang
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.HeConfig


import com.qweather.sdk.view.QWeather
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.xuexiang.xui.utils.WidgetUtils
import com.xuexiang.xui.widget.dialog.LoadingDialog
import com.xuexiang.xui.widget.popupwindow.popup.XUIPopup
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val TAG="MainActivity"
    private var sentence = ""

    //加载框
    private lateinit var loadingDialog : LoadingDialog


    //获取地址需要的变量
    var mLocationClient : AMapLocationClient? = null
    var mLocationOption: AMapLocationClientOption? = null
    var mLocationLister = object: AMapLocationListener {
        override fun onLocationChanged(aMapLocation: AMapLocation){
            if(aMapLocation!=null){
                if(aMapLocation.errorCode == 0){

                    Thread{
                        var cityName = aMapLocation.city.toString()
                        var latitude = aMapLocation.latitude.toString() //精度
                        var longitude = aMapLocation.longitude.toString() //维度
                        var districtNames = aMapLocation.district


                        //Log.i(TAG,"getLocationSuccess: "+districtNames+latitude.toString()+longitude.toString()+cityName)
                        if(cityName[cityName.length-1]=='市')
                            cityName = cityName.dropLast(1)
                        if(districtNames[districtNames.length-1]=='区')
                            districtNames = districtNames.dropLast(1)

                        updateCityName(cityName+" "+districtNames)

                        //将两个维度变成小数点后只有两位的字符串
                        var index1 = latitude.indexOf('.',0) //找不到返回-1
                        var index2 = longitude.indexOf('.',0)
                        if(index1!=-1){
                            latitude = latitude.substring(0,index1+3)
                        }

                        if(index2!=-1){
                            longitude = longitude.substring(0,index2+3)
                        }


                        //用这两个数去查找天气
                        findWeather(latitude, longitude)



                    }.start()
                    //销毁
                    mLocationClient?.stopLocation()
                    mLocationClient?.onDestroy()
                }
                else{
                    Log.i(TAG,"getLocationFailed: "+aMapLocation.errorCode+"ErrorInfo: "+aMapLocation.errorInfo)
                }
            }
        }

    }


    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var weatherBgView: ImageView //天气页面的背景
    private lateinit var weatherCity: TextView //当前的城市
    private  var cityLongitude = "121.43" //记录当前城市的位置经度，设置一个默认值
    private var citylatitude = "31.02" //记录当前城市的位置维度


    private lateinit var weatherNowTemperature: TextView //当前温度
    private lateinit var weatherNowAirQuality: TextView //当前空气质量
    private lateinit var weatherNowState: TextView //当前天气状态
    private lateinit var weatherNowStateIcon:ImageView //当前天气状况的图标

    private lateinit var poetryOne: TextView //第一句诗
    private lateinit var poetryTwo: TextView  //第二句诗
    private lateinit var poem:View //放置整首诗句
    private lateinit var xuipop:XUIPopup //设置弹窗
    private lateinit var poemtitle:TextView
    private lateinit var poemauthor: TextView
    private lateinit var poemcontent:TextView
    private lateinit var poemkey:TextView


    private lateinit var forecastHourLayout: LinearLayout //统计未来24小时的天气
    private lateinit var forecastDayLayout: LinearLayout //统计未来6天的天气
    private lateinit var SuitAndAvoidView: View //放置宜忌view
    private lateinit var forecastDayView: View  //放置未来的天气的view
    private lateinit var forecastHourView: View //放置未来24小时的view
    private lateinit var todaySuit: TextView //记录今日适宜
    private lateinit var todayAvoid: TextView //记录今日避免
    private lateinit var viewPager: ViewPager //用来滑动我的上面的view
    private val viewlist: MutableList<View> = ArrayList()


    //跳转到其他三个工具箱的按钮
    private lateinit var todolistButton: FloatingActionButton
    private lateinit var batheButton: FloatingActionButton
    private lateinit var inoutMoneyButton: FloatingActionButton


    private val okhttpListener = object : okhttp3.EventListener() {
        @SuppressLint("SetTextI18n")
        override fun dnsStart(call: okhttp3.Call, domainName: String) {
            super.dnsStart(call, domainName)
            // updateShowTextView("\nDns Search: $domainName")
        }

        @SuppressLint("SetTextI18n")
        override fun responseBodyStart(call: okhttp3.Call) {
            super.responseBodyStart(call)
            //updateShowTextView("\nResponse Start")
        }
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener)
        .build()
    private val gson = GsonBuilder().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "花有重开日"
        ClassicsHeader.REFRESH_HEADER_FINISH = "人无再少年"


        try {
            ServiceSettings.updatePrivacyShow(this, true, true)
            ServiceSettings.updatePrivacyAgree(this, true)
            mLocationClient = AMapLocationClient(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //api使用需要初始化的一些函数
        JinrishiciFactory.init(this)
        initHeFeWeather()

        //
        initView()
        //显示加载
        loadingDialog.show()

        //监听跳转
        listenrButton()


        //加载默认背景图片
        Glide.with(this)
            .load(R.drawable.bg2)
            .into(weatherBgView)

        //把获取的地理位置并写入对应位置
        //updateCityName("")
        initLocation()

        //获取诗句
        getPoetrySentence()

        //更新万历年宜忌
        getCalenda()

        //刷新
        refreshLayout()




    }





    //初始化所有的view和text的值
    private fun initView(){

        loadingDialog = WidgetUtils.getLoadingDialog(this)
            .setIconScale(0.4f)
            .setLoadingSpeed(8)
            .setLoadingIcon(R.drawable.loadingicon)

        refreshLayout = findViewById(R.id.refreshLayout)
        weatherBgView = findViewById(R.id.weatherBg)
        weatherCity = findViewById(R.id.tv_cityName)

        weatherNowTemperature = findViewById(R.id.tv_weather_temperature)
        weatherNowAirQuality = findViewById(R.id.tv_weather_aqi)
        weatherNowState = findViewById(R.id.tv_weatherstate)
        weatherNowStateIcon = findViewById(R.id.iv_weather_icon)

        poetryOne = findViewById(R.id.tv_poetry1)
        poetryTwo = findViewById(R.id.tv_poetry2)


        forecastDayView = layoutInflater.inflate(R.layout.forecast_day,null)
        forecastHourView = layoutInflater.inflate(R.layout.forecast_hour,null)
        SuitAndAvoidView = layoutInflater.inflate(R.layout.weather_suitandavoid,null)
        forecastDayLayout = forecastDayView.findViewById(R.id.forecast_day_layout)
        forecastHourLayout = forecastHourView.findViewById(R.id.forecast_hour_layout)
        todaySuit = SuitAndAvoidView.findViewById(R.id.tv_weather_suitable)
        todayAvoid = SuitAndAvoidView.findViewById(R.id.tv_weather_avoid)
        viewPager = findViewById(R.id.viewpager_weather)

        viewlist.add(SuitAndAvoidView)
        viewlist.add(forecastHourView)
        viewlist.add(forecastDayView)
        val weatherViewAdpater = ViewAdapter()
        weatherViewAdpater.setDatas(viewlist)
        viewPager.adapter = weatherViewAdpater

        todolistButton = findViewById(R.id.todoButton)
        inoutMoneyButton = findViewById(R.id.inoutButton)
        batheButton = findViewById(R.id.batheButton)


        poem = LayoutInflater.from(this).inflate(R.layout.weather_wholepoem,null)
        poemtitle = poem.findViewById(R.id.poemtitle)
        poemauthor = poem.findViewById(R.id.poemauthor)
        poemcontent = poem.findViewById(R.id.poemcontent)
        poemkey = poem.findViewById(R.id.poemkey)
        xuipop = XUIPopup(this)
        xuipop.setContentView(poem)
        poetryOne.setOnClickListener{
            xuipop.setAnimStyle(XUIPopup.ANIM_GROW_FROM_LEFT)
            xuipop.setPreferredDirection(XUIPopup.DIRECTION_TOP)
            xuipop.show(poetryOne)
        }
        poetryTwo.setOnClickListener{
            xuipop.setAnimStyle(XUIPopup.ANIM_GROW_FROM_LEFT)
            xuipop.setPreferredDirection(XUIPopup.DIRECTION_TOP)
            xuipop.show(poetryTwo)
        }

    }

    //监听跳转
    private fun listenrButton(){
        todolistButton.setOnClickListener{
            //startActivity(Intent(this,))

        }
        inoutMoneyButton.setOnClickListener{
            startActivity(Intent(this,MoneyMainActivity::class.java))

        }
        batheButton.setOnClickListener{
            startActivity(Intent(this,BathCheckActivity::class.java))
        }

    }




    //获取位置
    private fun updateCityName(city:String){
        if(Looper.getMainLooper()!== Looper.myLooper()){
            runOnUiThread{
                updateCityName(city)
            }
        }else{
           weatherCity.text = city
        }

    }
    private fun initLocation(){
        //回调监听
        Log.i(TAG,"eeeeeeeeeeeeeee")
        mLocationClient?.setLocationListener(mLocationLister)
        mLocationOption = AMapLocationClientOption()




        var option = AMapLocationClientOption()
        //设置模式，签到
        option.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
        if(mLocationClient != null){
            //设置定位参数
            mLocationClient?.setLocationOption(option)
            mLocationClient?.stopLocation()
            mLocationClient?.startLocation()
        }




        //设置精度，高精度
        mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置定位为单次定位
        mLocationOption?.isOnceLocation = true
        mLocationOption?.isOnceLocationLatest = true

        //设置是否返回地址信息
        mLocationOption?.isNeedAddress = true


        //设置请求超时时间，100秒
        mLocationOption?.httpTimeOut = 100000
        //关闭缓存机制
        mLocationOption?.isLocationCacheEnable= false

        //设置参数
        mLocationClient?.setLocationOption(mLocationOption)

        mLocationClient?.startLocation()



    }


    //天气

    private fun initHeFeWeather(){
        HeConfig.init("HE2204181430531762","868f4f8b1f5b443cafa1e7179e4a6d76")
        HeConfig.switchToDevService()
    }
    //在城市获取地址时被调用，获取天气，空气质量等更新到ui中
    private fun findWeather(latitude:String, longtitude:String){

        if(Looper.getMainLooper()!= Looper.myLooper())
        {
            runOnUiThread{
                findWeather(latitude,longtitude)
            }
        }else {
                getWeather(longtitude+","+latitude)
                get24hourWeather(latitude+":"+longtitude)
                get6DayWeather(latitude+":"+longtitude)
                cityLongitude = longtitude
                citylatitude = latitude
        }


    }

    //更新天气温度，状态，和状态的图标
    private fun updateWeather(nowTemperature:String, nowState:String, nowIcon:String) {

        if(Looper.getMainLooper()!== Looper.myLooper()){
            runOnUiThread{
                updateWeather(nowTemperature, nowState, nowIcon)
            }
        }else{
            weatherNowTemperature.text = nowTemperature
            weatherNowState.text = nowState
            //loadingIcon(nowIcon)
            val iconname = "vector_drawable_"+nowIcon
            val iconid = getResourceId(iconname)
            weatherNowStateIcon.setImageResource(iconid)
        }
    }

    private fun updateAir(airq:String){
        if(Looper.getMainLooper()!== Looper.myLooper()){
            runOnUiThread{
                updateAir(airq)
            }
        }else{

            weatherNowAirQuality.text = airq
            loadingDialog.dismiss()

        }
    }
    private fun getWeather(citylocation : String){

        //使用城市的经纬度，就不需要再去获取城市的id了

        //val citylocaion = "121.43,31.02"

        QWeather.getWeatherNow(this,citylocation, object:QWeather.OnResultWeatherNowListener{
            override fun onError(p0: Throwable?) {
                Log.i(TAG, "Weather Now Error:", p0)
                //updateWeather("Weather Now Error")
            }

            override fun onSuccess(p0: WeatherNowBean?) {
                Log.i(TAG, p0?.now?.temp.toString())
                if(p0?.code == Code.OK){

                    val now = p0.now
                    val nowtemp = now.temp+ "°"
                    val nowstate = now.text
                    val nowicon = now.icon

                    updateWeather(nowtemp, nowstate, nowicon)

                }
                else{
                    var code = p0?.code
                    Log.i(TAG,"failed code"+code)
                }
            }
        })


        QWeather.getAirNow(this,citylocation, Lang.ZH_HANS, object:QWeather.OnResultAirNowListener{
            override fun onError(p0: Throwable?) {
                Log.i(TAG, "Air Now Error:", p0)
                //updateAir("Air Now Error")
            }

            override fun onSuccess(p0: AirNowBean?) {

                if(p0?.code == Code.OK){
                    var air = "空气" + p0.now.category + " " + p0.now.aqi
                    updateAir(air)
                }
            }
        })






    }



    //获取诗句
    private fun updatePoetrySentence(sentencelist:List<String>){
        if(Looper.getMainLooper()!== Looper.myLooper()){
            runOnUiThread{
                updatePoetrySentence(sentencelist)
            }
        }else{

            poetryOne.text = sentencelist[0]
            poetryTwo.text = sentencelist[1]
            val animation = AlphaAnimation(0f,1f)
            animation.duration = 1000
            poetryOne.startAnimation(animation)
            poetryTwo.startAnimation(animation)

        }
    }

    private fun updateWholePoem(title:String,author:String,dynasty:String,content:List<String>,mathTages:List<String>)
    {
        if (content.isNotEmpty()){


            var contents = ""
            for(senten in content){
                contents += senten+"\n"
            }
            var keys = "关键字："
            for(key in mathTages){
                keys +=key+" "
            }

            poemtitle.setText(title)
            poemauthor.setText("["+dynasty+"] "+author)
            poemcontent.setText(contents)
            poemkey.setText(keys)
        }
    }

    private fun getPoetrySentence()
    {
        var client = JinrishiciClient.getInstance()
        client.getOneSentenceBackground(object:JinrishiciCallback{
            override fun done(poetrySentence: PoetySentence){
                sentence = poetrySentence.data.content.toString()
                val wholePoemTitle = poetrySentence.data.origin.title
                val wholePoemAuthor = poetrySentence.data.origin.author
                val wholePoemDynasty = poetrySentence.data.origin.dynasty
                val wholePoemContent = poetrySentence.data.origin.content
                val mathTags = poetrySentence.data.matchTags
                Log.i("poetrysentencr", sentence)
                var sentences = sentence.split("，","。","、","？")
                    if(sentences.size >= 2)
                    {
                        updatePoetrySentence(sentences)
                        updateWholePoem(wholePoemTitle,wholePoemAuthor,wholePoemDynasty,wholePoemContent,mathTags)
                        return
                    }

                    else{
                        getPoetrySentence()
                    }

            }
            override fun error(e : JinrishiciRuntimeException){
               Log.e(TAG,e.message.toString())
            }
        })
    }



    //获取万年历
    private fun updateCalendar(suit:String, avoid:String){
        if(Looper.getMainLooper()!== Looper.myLooper()){
            runOnUiThread{
                updateCalendar(suit, avoid)
            }
        }else{
           todaySuit.text = suit
            todayAvoid.text = avoid
        }
    }


    private fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client.newCall(request).enqueue(callback)
    }


    private fun RequestCalendar(date:String){

        val calendarUri =
            "http://v.juhe.cn/calendar/day?date=$date&key=3b829bffc604d9d119ded8d8818da1ba"
        request(calendarUri,object : Callback{
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                Log.i(TAG,e.toString())
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: okhttp3.Call, response: Response) {
                var calendar="\n"
                if (response.isSuccessful){
                    val bodyString = response.body()!!.string() //

                    Log.i("calendar response", bodyString)
                    val calendarBean = gson.fromJson(bodyString,CalendarBean::class.java)
                    try {
                        if(calendarBean.error_code == 0){

                            updateCalendar(calendarBean.result.data.suit, calendarBean.result.data.avoid)
                        }



                    }
                    catch (e:Exception){
                        calendar += e.toString()
                        Log.i("calendar",e.toString())
                    }


                }else{
                    calendar+="response is failed"
                    Log.i("calendar failed", calendar)
                }
                //updateCalendar(calendar)

            }


        })



    }

    private fun getCalenda(){
        val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
        val date = Date(System.currentTimeMillis())
        RequestCalendar(simpleDateFormat.format(date))

    }


    //更新24小时天气
    private fun update24HourWeather( hourly: List<Hourly24HWBean>){
        if(Looper.getMainLooper()!== Looper.myLooper()){
            runOnUiThread{
                update24HourWeather(hourly)
            }
        }else{
                forecastHourLayout.removeAllViews()
            for (i in listOf(4,8,12,16,20,23)){
                val item = hourly[i]
                val hourlyview = LayoutInflater.from(this).inflate(R.layout.weather_forecasthour_item,forecastHourLayout,false)
                val timetext = hourlyview.findViewById<TextView>(R.id.forecast_hour_time)
                val iconview = hourlyview.findViewById<ImageView>(R.id.forecast_hour_icon)
                val temptext = hourlyview.findViewById<TextView>(R.id.forecast_hour_temperature)
                val simpleDateFormat = SimpleDateFormat("HH:mm")
                val stateviewname = "xzblack"+item.code.toString()  //不需要拓展名
                val stateviewid = getResourceId(stateviewname)
                Log.i("hourlytime", simpleDateFormat.format(item.time).toString()+stateviewid.toString()+stateviewname+ this.packageName)
                timetext.text = simpleDateFormat.format(item.time).toString()
                temptext.text = item.temperature+"°"

                Glide.with(this)
                    .load(stateviewid)
                    .into(iconview)

                forecastHourLayout.addView(hourlyview)
            }

        }
    }

    //更新未来七天的天气

    private fun update6DayWeather(daily: List<Daily6DWBean>){

        if(Looper.getMainLooper()!== Looper.myLooper()){
            runOnUiThread{
                update6DayWeather(daily)
            }
        }else{
                forecastDayLayout.removeAllViews()
            for (item in daily){
                val dailyview = LayoutInflater.from(this).inflate(R.layout.weather_forecastaday_item,forecastDayLayout,false)
                val timetext = dailyview.findViewById<TextView>(R.id.forecast_day_date)
                val statetext = dailyview.findViewById<TextView>(R.id.forecast_day_weather)
                val mintemptext = dailyview.findViewById<TextView>(R.id.forecast_day_mintemperature)
                val maxtemptext = dailyview.findViewById<TextView>(R.id.forecast_day_maxtemperature)
                val simpleDateFormat = SimpleDateFormat("M-d")
                Log.i("dailytime", item.date.toString())
                timetext.text = simpleDateFormat.format(item.date).toString()
                statetext.text = item.text_day
                mintemptext.text = item.low+"°"
                maxtemptext.text = item.high+"°"
                forecastDayLayout.addView(dailyview)
            }

        }
    }
    //未来24个小时天气，包括 时间，温度，天气图标状况码，可分为6个阶段，获取第 3,7,11,15,19,23这些数据
    private fun get24hourWeather(cityname:String){
        val hourlyweatherUrl =
            "https://api.seniverse.com/v3/weather/hourly.json?key=SrrX9GVka4C62DQbr&location="+cityname+"&language=zh-Hans&unit=c&start=0&hours=24"
        request(hourlyweatherUrl,object : Callback{
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                Log.i(TAG,e.toString())
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: okhttp3.Call, response: Response) {
                var weather24H="\n"
                if (response.isSuccessful){
                    val bodyString = response.body()!!.string()

                    Log.i("24hour response", bodyString)
                    val W24HBean = gson.fromJson(bodyString,XZ24HWeatherBean::class.java)
                    try {

                        Log.i("24HWsuccessful", W24HBean.results[0].location.name)

                        update24HourWeather(W24HBean.results[0].hourly)



                    }
                    catch (e:Exception){
                        weather24H += e.toString()
                        Log.i("24HWfailed",e.toString())
                    }


                }else{
                    weather24H+="response is failed"
                    Log.i("calendar failed", weather24H)
                }
                //updateCalendar(calendar)

            }


        })


    }



    //未来6天天气，时间，天气描述，最高温度，最低温度
    private fun get6DayWeather(cityname:String){
        val hourlyweatherUrl =
            "https://api.seniverse.com/v3/weather/daily.json?key=SrrX9GVka4C62DQbr&location="+cityname+"&language=zh-Hans&unit=c&start=1&days=6"
        request(hourlyweatherUrl,object : Callback{
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                Log.i(TAG,e.toString())
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: okhttp3.Call, response: Response) {
                var weather6D="\n"
                if (response.isSuccessful){
                    val bodyString = response.body()!!.string()

                    Log.i("6day response", bodyString)
                    val W6DBean = gson.fromJson(bodyString,XZ6DWeatherBean::class.java)
                    try {

                        var weather6Dname = W6DBean.results[0].location.name
                        var day1= W6DBean.results[0].daily[3].text_day+W6DBean.results[0].daily[3].low+W6DBean.results[0].daily[3].high+W6DBean.results[0].daily[3].date.toString()
                        weather6D = weather6Dname+day1
                        Log.i("24HWsuccessful", weather6D)

                        update6DayWeather(W6DBean.results[0].daily)

                    }
                    catch (e:Exception){
                        weather6D += e.toString()
                        Log.i("24HWfailed",e.toString())
                    }


                }else{
                    weather6D+="response is failed"
                    Log.i("calendar failed", weather6D)
                }
                //updateCalendar(calendar)

            }


        })


    }



    //通过名字查找资源的id，但是名字不需要后缀名
    private fun getResourceId(resname:String):Int{
        val resource = this.resources
        val pkgname = this.packageName

        var resId:Int = resource.getIdentifier(resname,"drawable",pkgname) //这里的名字不加拓展名

        if(resId!=0)
            return resId
        else
            return R.drawable.xzblack0 //找不到时，放置一个默认图标

    }


    /**
     刷新操作
     要更新：更新背景，现在的天气（包括现在的和预测的），诗句
     不更新：地理位置和今日宜忌
    */
    private fun refreshLayout()

    {

        refreshLayout.setOnRefreshListener(object : OnRefreshListener{

            override fun onRefresh(refreshLayout: RefreshLayout) {

                refreshLayout.autoRefresh()


                //天气
                findWeather(citylatitude,cityLongitude)
                //诗词
                getPoetrySentence()
                //背景
                updateBackground()

                Handler().postDelayed(object : Runnable{
                    override fun run(){
                        refreshLayout?.finishRefresh()
                    }
                },2000)

            }
        })
    }


    //根据天气更新背景
    private fun updateBackground()
    {
        //随机数获取背景资源的id
        val bgid = (1..22).random()
        val bgname = "background"+bgid.toString()
        val drawble_bg = getResourceId(bgname)

        //加载默认背景图片
        Glide.with(this)
            .load(drawble_bg)
            .into(weatherBgView)

    }





}