package com.bytedance.jstu.p.money

import android.annotation.SuppressLint
import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.jstu.p.MainActivity
import com.bytedance.jstu.p.R
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
import com.xuexiang.xui.XUI
import com.xuexiang.xui.utils.WidgetUtils
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.toast.XToast
import com.yanzhenjie.recyclerview.*
import com.bytedance.jstu.p.adapter.RecordAdapter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class MoneyMainActivity :AppCompatActivity() ,OnChartValueSelectedListener{

    //数据库的初始化
    private val dbHelper = MyDBHelper(this,"incoutmoeny.db",1)
    private var db : SQLiteDatabase? = null
    private val data = arrayListOf<Record>()
    private val incomedata = arrayListOf<IncomeRecord>()
    private val outcomedata = arrayListOf<OutcomeRecord>()

    //用来广播
    private lateinit var receiver : myBroadcastReceiver


    //图标的变量
    private lateinit  var incomechart: PieChart
    private lateinit var outcomechart: PieChart

    //标题栏
    private lateinit var moneytitlebar: TitleBar

    //总支出和总收入
    private lateinit var totalin:TextView
    private lateinit var totalout:TextView

    //侧滑的条目记录
    private lateinit var recycleRecordView: SwipeRecyclerView
    //条目菜单初始化
    private val swipeMenuCreator =
        SwipeMenuCreator { swipeLeftMenu: SwipeMenu, swipeRightMenu: SwipeMenu, position: Int ->
            val width = resources.getDimensionPixelSize(R.dimen.dp_70)

            val height = ViewGroup.LayoutParams.MATCH_PARENT


            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            run {
                //编辑按钮
                val addItem: SwipeMenuItem =
                    SwipeMenuItem(this).setBackground(R.drawable.menu_selector_green)
                        .setText("编辑")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height)
                swipeRightMenu.addMenuItem(addItem) // 添加菜单到右侧。

                //删除按钮
                val deleteItem: SwipeMenuItem =
                    SwipeMenuItem(this).setBackground(R.drawable.menu_selector_red)
                        .setImage(R.drawable.ic_swipe_menu_delete)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height)
                swipeRightMenu.addMenuItem(deleteItem) // 添加菜单到右侧。

            }
        }
    //设置item的监听事件
    private val mMenuItemClickListener =
        OnItemMenuClickListener { menuBridge: SwipeMenuBridge, position: Int ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction // 左侧还是右侧菜单。
            val menuPosition = menuBridge.position // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {

                //删除操作
                if(menuPosition==1){
                    deleterefreshlayout(position)
                }
                else if(menuPosition==0){ //编辑操作

                    modifyItem(position)

                }
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
            }
        }

    //列表的适配器
    private val mAdapter = RecordAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money_main)
        db = dbHelper.writableDatabase //初始化数据库
        initMoenyView()
        checkUpdateDatabase()

        readDatabase()

        showcharts()

        initRecordList()

        //注册广播，然后收到后进行重启
        val intentFilter = IntentFilter()
        intentFilter.addAction("Main.close")
        receiver = myBroadcastReceiver()
        registerReceiver(receiver,intentFilter)





    }


    private fun initMoenyView() {



        moneytitlebar = findViewById(R.id.moneytitle)
        moneytitlebar.setLeftClickListener{
            startActivity(Intent(this,MainActivity::class.java))
        }
        moneytitlebar.addAction(object:TitleBar.TextAction("添加"){

            override fun performAction(view: View?) {
                startActivity(Intent(this@MoneyMainActivity,AddItemAcitivity::class.java))

            }
        })


        incomechart = findViewById(R.id.incomechart)
        outcomechart = findViewById(R.id.outcomechart)

        recycleRecordView = findViewById(R.id.recycler_record_view)

        totalin = findViewById(R.id.t2)
        totalout = findViewById(R.id.t3)


    }

    //初始化列表的展示
    private fun initRecordList(){

        WidgetUtils.initRecyclerView(recycleRecordView)

        //必须在setAdapter之前调用
        recycleRecordView.setSwipeMenuCreator(swipeMenuCreator)
        recycleRecordView.setOnItemMenuClickListener(mMenuItemClickListener)

        //展示列表
        mAdapter.setContentList(data)
        recycleRecordView.adapter = mAdapter



        //recycleRecordView.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
    }


    //对数据库检查，删除上个月的数据，只保留本月的数据，对三个数据库都要进行检查,
    private fun checkUpdateDatabase(){

        //获取当前的月份
        val simpleDateFormat = SimpleDateFormat("yyyy-MM")
        val date = Date(System.currentTimeMillis())
        val datestr = simpleDateFormat.format(date)  //字符串大小的比较，这样比较，按字符逐个比较
        //删除三个数据库中小于这个时间的记录
        db?.delete("record","recordTime<?", arrayOf(datestr))
        db?.delete("inrecord","recordTime<?", arrayOf(datestr))
        db?.delete("outrecord","recordTime<?", arrayOf(datestr))




    }

    //读取数据库数据
    @SuppressLint("SetTextI18n")
    private fun readDatabase(){

        //读取条目数据
        var cursor = (db?: dbHelper.writableDatabase).query("record", null, null, null, null, null, null, null)
        if (cursor.moveToLast()){
            do{
                val key = cursor.getInt(cursor.getColumnIndex("id")) //用于查找
                val recordtime = cursor.getString(cursor.getColumnIndex("recordTime"))
                val recordinout = cursor.getString(cursor.getColumnIndex("recordInOut"))
                val recordtype = cursor.getString(cursor.getColumnIndex("recordType"))
                val recordmoney = cursor.getFloat(cursor.getColumnIndex("recordMoney"))
                val recordinfo = cursor.getString(cursor.getColumnIndex("recordInfo"))

                //将时间由 2020-04-05变成 4月5号
                val formatdate = formatTime(recordtime)

                data.add(Record(key,formatdate, recordinout, recordtype, recordmoney,recordinfo))
            } while (cursor.moveToPrevious())
        }
        cursor.close()

        //读取收入数据
        cursor = (db?: dbHelper.writableDatabase).query("inrecord", null, null, null, null, null, null, null)
        if (cursor.moveToLast()){
            do{

                val inrecordtype = cursor.getString(cursor.getColumnIndex("incomeType"))
                val inrecordmoney = cursor.getFloat(cursor.getColumnIndex("money"))
                incomedata.add(IncomeRecord(inrecordtype, inrecordmoney))
            } while (cursor.moveToPrevious())
        }
        cursor.close()

        //读取支出数据
        cursor = (db?: dbHelper.writableDatabase).query("outrecord", null, null, null, null, null, null, null)
        if (cursor.moveToLast()){
            do{

                val outrecordtype = cursor.getString(cursor.getColumnIndex("outcomeType"))
                val outrecordmoney = cursor.getFloat(cursor.getColumnIndex("money"))
                outcomedata.add(OutcomeRecord(outrecordtype, outrecordmoney))
            } while (cursor.moveToPrevious())
        }
        cursor.close()

        //读入总输入和总支出
        val simpleDateFormat = SimpleDateFormat("yyyy-MM")
        val date = Date(System.currentTimeMillis())
        val datestr = simpleDateFormat.format(date)
        cursor = (db?: dbHelper.writableDatabase).query("totalrecord", null, "recordTime=?", arrayOf(datestr), null, null, null, null)
        var totalinmoney=0f
        var totaloutmoney=0f
        if (cursor.moveToFirst())//有
        {
            totalinmoney= cursor.getFloat(cursor.getColumnIndex("totalin"))
            totaloutmoney= cursor.getFloat(cursor.getColumnIndex("totalout"))
        }
        totalin.text = "总收入："+totalinmoney.toString()
        totalout.text = "总支出："+totaloutmoney.toString()



    }

    //展示图表
    private fun showcharts(){
        initChartStyle(incomechart,"收入比列")
        initCharLabel(incomechart)
        initChartStyle(outcomechart,"支出比列")
        initCharLabel(outcomechart)
        drawchart()
        incomechart.animateY(1400, Easing.EaseInOutQuad)
        incomechart.setOnChartValueSelectedListener(this)
        outcomechart.animateY(1400, Easing.EaseInOutQuad)
        outcomechart.setOnChartValueSelectedListener(this)
    }

    //初始化图表的风格
    private fun initChartStyle(chart:PieChart, centerText:String){

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
    private fun drawchart(){
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
        val incomeEntries =ArrayList<PieEntry>()

        if(incomedata.isEmpty()) //如果还没有数据
        {
            incomeEntries.add(PieEntry(1f,"还没有收入哦"))
        }
        else{
            for(item in incomedata){
                incomeEntries.add(PieEntry(item.money,item.incomeType))
            }
        }

        val incomeDataSet = PieDataSet(incomeEntries,"收入情况")
        incomeDataSet.setDrawIcons(false)
        incomeDataSet.sliceSpace = 3f
        incomeDataSet.iconsOffset = MPPointF(0f, 40f)
        incomeDataSet.selectionShift = 5f

        incomeDataSet.colors = colors


        val incomePieData = PieData(incomeDataSet)
        incomePieData.setValueFormatter(PercentFormatter(incomechart))
        incomePieData.setValueTextSize(11f)
        incomePieData.setValueTextColor(Color.WHITE)
        incomechart.data = incomePieData


        // undo all highlights
        incomechart.highlightValues(null)
        incomechart.invalidate()



        //支出
        //设置数据
        val outcomeEntries =ArrayList<PieEntry>()

        if(outcomedata.isEmpty()) //如果还没有数据
        {
            outcomeEntries.add(PieEntry(1f,"还没有支出哦"))
        }
        else{
            for(item in outcomedata){
                outcomeEntries.add(PieEntry(item.money,item.outcomeType))
            }
        }

        val outcomeDataSet = PieDataSet(outcomeEntries,"支出情况")
        outcomeDataSet.setDrawIcons(false)
        outcomeDataSet.sliceSpace = 3f
        outcomeDataSet.iconsOffset = MPPointF(0f, 40f)
        outcomeDataSet.selectionShift = 5f

        outcomeDataSet.colors = colors


        val outcomePieData = PieData(outcomeDataSet)
        outcomePieData.setValueFormatter(PercentFormatter(incomechart))
        outcomePieData.setValueTextSize(11f)
        outcomePieData.setValueTextColor(Color.WHITE)
        outcomechart.data = outcomePieData


        // undo all highlights
        outcomechart.highlightValues(null)
        outcomechart.invalidate()



    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        XToast.normal(XUI.getContext(),"" + e!!.y+"元").show()

    }

    override fun onNothingSelected() {

    }


    private fun formatTime(time:String):String{

        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = formatter.parse(time)

        val formatter1 = SimpleDateFormat("M-d")
        val date1 = formatter1.format(date)
        val dates = date1.split('-')
        val resultdate = dates[0]+"月"+dates[1]+"日"
        return resultdate
    }


    private fun deleterefreshlayout(position:Int){

        //先删除数据
        deletefromdatabase(position)

        refresh()
    }

    private fun deletefromdatabase(position: Int){
        //position 即data中的位置，去删除数据库中的条目
        db?.delete("record","id=${data[position].key}",null)
        //从收入和支出数据库中减去
        if(data[position].recordInOut=="收入"){
            val cursor = (db?: dbHelper.writableDatabase).query("inrecord", null, "incomeType=?", arrayOf(data[position].recordType), null, null, null, null)
            if(cursor.moveToFirst()){
                var money1 = cursor.getFloat(cursor.getColumnIndex("money"))
                var money2 = money1-data[position].recordMoney

                if (money2==0f) //如果删去后为0，就不要这条记录了
                {
                    db?.delete("inrecord","incomeType=?", arrayOf(data[position].recordType))
                }
                else{
                    var newmoney = ContentValues().apply {
                        put("money",money2)
                    }

                    db?.update("inrecord",newmoney,"incomeType=?", arrayOf(data[position].recordType))
                }

            }
        }
        else if(data[position].recordInOut=="支出")
        {
            val cursor = (db?: dbHelper.writableDatabase).query("outrecord", null, "outcomeType=?", arrayOf(data[position].recordType), null, null, null, null)
            if(cursor.moveToFirst()){
                var money1 = cursor.getFloat(cursor.getColumnIndex("money"))
                var money2 = money1-data[position].recordMoney

                if(money2==0f){
                    db?.delete("outrecord","outcomeType=?", arrayOf(data[position].recordType))
                }
                else{
                    var newmoney = ContentValues().apply {
                        put("money",money2)
                    }

                    db?.update("outrecord",newmoney,"outcomeType=?", arrayOf(data[position].recordType))
                }

            }
        }

        //从总的数据库中删去
        val simpleDateFormat = SimpleDateFormat("yyyy-MM")
        val date = Date(System.currentTimeMillis())
        val datestr = simpleDateFormat.format(date)
        val cursor = (db?: dbHelper.writableDatabase).query("totalrecord", null, "recordTime=?", arrayOf(datestr), null, null, null, null)
        var totalinmoney=0f
        var totaloutmoney=0f
        if (cursor.moveToFirst())//有
        {
            if(data[position].recordInOut=="收入")
            {
                totalinmoney= cursor.getFloat(cursor.getColumnIndex("totalin"))-data[position].recordMoney


                var newmoney1 = ContentValues().apply {
                    put("totalin",totalinmoney)
                }
                db?.update("totalrecord",newmoney1,"recordTime=?", arrayOf(datestr))
            }
            else{
                totaloutmoney= cursor.getFloat(cursor.getColumnIndex("totalout"))-data[position].recordMoney
                var newmoney2 = ContentValues().apply {
                    put("totalout",totaloutmoney)
                }
                db?.update("totalrecord",newmoney2,"recordTime=?", arrayOf(datestr))
            }


        }



    }

   //刷新这个activity
    private fun refresh(){
        finish()
       startActivity(Intent(this@MoneyMainActivity,MoneyMainActivity::class.java))
    }


    //编辑操作
    private fun modifyItem(position: Int){
        val intent = Intent()
        intent.setClass(this@MoneyMainActivity,AddItemAcitivity::class.java)

        val cursor = (db?: dbHelper.writableDatabase).query("record", null, "id=${data[position].key}", null, null, null, null, null)
        if(cursor.moveToFirst()){

            val key = cursor.getInt(cursor.getColumnIndex("id")) //用于查找
            val recordtime = cursor.getString(cursor.getColumnIndex("recordTime"))
            val recordinout = cursor.getString(cursor.getColumnIndex("recordInOut"))
            val recordtype = cursor.getString(cursor.getColumnIndex("recordType"))
            val recordmoney = cursor.getFloat(cursor.getColumnIndex("recordMoney"))
            val recordinfo = cursor.getString(cursor.getColumnIndex("recordInfo"))

            intent.putExtra("inout", recordinout)
            intent.putExtra("key",key)
            intent.putExtra("time",recordtime)
            intent.putExtra("type",recordtype)
            intent.putExtra("info",recordinfo)
            intent.putExtra("money",recordmoney)

        }



        startActivity(intent)
    }



    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()

    }

    inner class myBroadcastReceiver() : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            finish()
        }
    }












}