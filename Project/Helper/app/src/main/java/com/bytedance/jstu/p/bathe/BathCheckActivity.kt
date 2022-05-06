package com.bytedance.jstu.p.bathe

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.jstu.p.MainActivity
import com.bytedance.jstu.p.R
import com.bytedance.jstu.p.apiclass.BathroomBean
import com.bytedance.jstu.p.apiclass.CalendarBean
import com.bytedance.jstu.p.apiclass.TimeConsumeInterceptor
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.GsonBuilder
import com.qweather.sdk.view.HeContext.context
import com.xuexiang.xui.XUI
import com.xuexiang.xui.utils.ResUtils
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder
import com.xuexiang.xui.widget.toast.XToast
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class BathCheckActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private lateinit  var bathchart: PieChart
    private lateinit var area_et:EditText
    private lateinit var id_et:EditText
    private lateinit var button: Button
    private var area_etPosition = 0
    private lateinit var classOption: Array<String>
    private lateinit var batheTitle:TitleBar
    private lateinit var textbili:TextView

    private var buttoncolor = 0


    //api获取服务
    private val okhttpListener = object : okhttp3.EventListener() {
        @SuppressLint("SetTextI18n")
        override fun dnsStart(call: okhttp3.Call, domainName: String) {
            super.dnsStart(call, domainName)
        }

        @SuppressLint("SetTextI18n")
        override fun responseBodyStart(call: okhttp3.Call) {
            super.responseBodyStart(call)

        }
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener)
        .build()
    private val gson = GsonBuilder().create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bath_main)

        initview()



    }

    private fun initview(){
        bathchart = findViewById(R.id.bathchart)
        area_et = findViewById(R.id.et_bath_area)
        id_et = findViewById(R.id.et_bath_id)
        button = findViewById(R.id.ba_button)
        batheTitle = findViewById(R.id.batheTitle)
        textbili = findViewById(R.id.tv_bili)

        classOption = ResUtils.getStringArray(R.array.batharea)

        initBatheChart()

        //先从数据库中读出上次的选择数据
        val initdata = readData()
        area_et.setText(initdata[0])
        id_et.setText(initdata[1])


        setListener()

        RequestBathInfo(initdata[0],initdata[1])



    }

    private fun setListener(){

        batheTitle.setLeftClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }


        area_et.setOnClickListener{


            val classpvOptions = OptionsPickerBuilder(
                this
            ) { v: View?, options1: Int, options2: Int, options3: Int ->
                area_et.setText(classOption[options1])
                area_etPosition = options1
                false
            }
                .setTitleText(getString(R.string.bathtitle))
                .setSelectOptions(area_etPosition)
                .build<Any>()
            classpvOptions.setPicker(classOption)
            classpvOptions.show()

        }



        button.setOnClickListener {
            var newarea=area_et.text.toString()
            var newid = id_et.text.toString().toInt()
            if(newid<=34&& newid>=1)
            {
                RequestBathInfo(newarea,newid.toString())
                saveData(newarea,newid.toString())
                if (buttoncolor==0){
                    button.setTextColor(R.drawable.menu_selector_red)
                    buttoncolor=1
                }
                else{
                    button.setTextColor(R.drawable.menu_selector_green)
                    buttoncolor=0
                }
            }
            else{
                XToast.normal(XUI.getContext(),"请输入正确的楼栋号码！").show()
            }

        }

    }

    //读数据库
    private fun readData():ArrayList<String>{

        try{
            val sp = getSharedPreferences("BathData", Context.MODE_PRIVATE)
            val areaname = sp.getString("area","东")!!
            val idname = sp.getString("id","1")!!

            return arrayListOf<String>(areaname,idname)
        }
        catch (e:java.lang.Exception){
            e.printStackTrace()
            return arrayListOf<String>(area_et.text.toString(),id_et.text.toString())
        }


    }

    private fun saveData(area:String, id:String){
        val editor = getSharedPreferences("BathData", Context.MODE_PRIVATE).edit()
        editor.putString("area",area)
        editor.putString("id",id)
        editor.apply()
    }

    //获取楼栋洗澡间数据
    private fun updateBathInfo(free:Int, used:Int){
        if(Looper.getMainLooper()!== Looper.myLooper()){
            runOnUiThread{
                updateBathInfo(free,used)
            }
        }else{

            val textval = "可使用数/总数："+ free.toString()+"/"+(free+used).toString()
            //刷新图的数
            val bathinfo = BathInfo(free,used)
            drawchart(bathinfo)
            textbili.setText(textval)


        }
    }


    private fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client.newCall(request).enqueue(callback)
    }


    private fun RequestBathInfo(area:String, id:String){

        val calendarUri =
            "https://plus.sjtu.edu.cn/api/sjtu/bathroom"
        request(calendarUri,object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                Log.i("bathinfo",e.toString())
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: okhttp3.Call, response: Response) {
                var calendar="\n"
                if (response.isSuccessful){
                    val bodyString = response.body()!!.string() //

                    Log.i("bathinfo response", bodyString)
                    val BathInfoBean = gson.fromJson(bodyString, BathroomBean::class.java)
                    try {

                        for(dormitory in BathInfoBean.data){
                            if((area in dormitory.name) && (id in dormitory.name))
                            {
                                val freenumber = dormitory.status_count.free
                                val usednumber = dormitory.status_count.used
                                updateBathInfo(freenumber,usednumber)

                                break
                            }
                        }

                    }
                    catch (e:Exception){
                        calendar += e.toString()
                        Log.i("bathinfo",e.toString())
                    }


                }else{
                    calendar+="response is failed"
                    Log.i("bathinfo failed", calendar)
                }

            }


        })



    }



    //画图表
    //展示图表
    private fun initBatheChart(){
        initChartStyle(bathchart,"使用情况")
        initCharLabel(bathchart)
        bathchart.animateY(1400, Easing.EaseInOutQuad)
        bathchart.setOnChartValueSelectedListener(this)

    }

    //初始化图表的风格
    private fun initChartStyle(chart: PieChart, centerText:String){

        //收入图表
        //使用百分比显示
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5f,10f,5f,5f)

        //设置拖拽的阻尼，0为立即停止
        chart.dragDecelerationFrictionCoef = 0.95f

        //设置图标中心文字
        chart.centerText = centerText
        chart.setDrawCenterText(true)

        //设置图标中心空白，空心
        chart.isDrawHoleEnabled = true

        //设置空心圆的弧度百分比，最大100
        chart.holeRadius = 50f
        chart.setHoleColor(Color.WHITE)

        //设置透明弧的样式
        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)
        chart.transparentCircleRadius = 61f

        //设置可以旋转
        chart.rotationAngle = 0f
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

    }

    //设置图表的标题
    private fun initCharLabel(chart: PieChart){
        val l: Legend = chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f
        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
        chart.setEntryLabelTextSize(12f)
    }

    //利用得到的数据画收入和支出的图标
    private fun drawchart(bathinfo: BathInfo){
        //设置颜色

        val colors: MutableList<Int> = java.util.ArrayList()
        for (c in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c)
        }
        for (c in ColorTemplate.JOYFUL_COLORS) {
            colors.add(c)
        }
        for (c in ColorTemplate.COLORFUL_COLORS) {
            colors.add(c)
        }
        for (c in ColorTemplate.LIBERTY_COLORS) {
            colors.add(c)
        }
        for (c in ColorTemplate.PASTEL_COLORS) {
            colors.add(c)
        }
        colors.add(ColorTemplate.getHoloBlue())

        //收入
        //设置数据
        val bathEntries =ArrayList<PieEntry>()
        bathEntries.add(PieEntry(bathinfo.free.toFloat(),"free"))
        bathEntries.add(PieEntry(bathinfo.used.toFloat(),"used"))


        val incomeDataSet = PieDataSet(bathEntries,"浴室使用情况")
        incomeDataSet.setDrawIcons(false)
        incomeDataSet.sliceSpace = 3f
        incomeDataSet.iconsOffset = MPPointF(0f, 40f)
        incomeDataSet.selectionShift = 5f

        incomeDataSet.colors = colors


        val incomePieData = PieData(incomeDataSet)
        incomePieData.setValueFormatter(PercentFormatter(bathchart))
        incomePieData.setValueTextSize(11f)
        incomePieData.setValueTextColor(Color.WHITE)
        bathchart.data = incomePieData


        // undo all highlights
        bathchart.highlightValues(null)
        bathchart.invalidate()





    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {
      //  XToast.normal(XUI.getContext(),e!!.y.toInt().toString()+"人").show()
    }

    override fun onNothingSelected() {

    }


    //解决edittext收起键盘问题
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val ret = super.dispatchTouchEvent(ev)
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_UP) {
                currentFocus?.let { view ->
                    if (view is EditText) {
                        val touchCoordinates = IntArray(2)
                        view.getLocationOnScreen(touchCoordinates)
                        val x: Float = event.rawX + view.getLeft() - touchCoordinates[0]
                        val y: Float = event.rawY + view.getTop() - touchCoordinates[1]
                        //If the touch position is outside the EditText then we hide the keyboard
                        if (x < view.getLeft() || x >= view.getRight() || y < view.getTop() || y > view.getBottom()) {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                            view.clearFocus()
                        }
                    }
                }
            }
        }
        return ret
    }



}