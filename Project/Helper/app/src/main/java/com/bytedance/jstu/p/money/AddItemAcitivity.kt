package com.bytedance.jstu.p.money

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bytedance.jstu.p.R
import com.xuexiang.xui.adapter.FragmentAdapter
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.tabbar.EasyIndicator
import com.xuexiang.xui.widget.tabbar.TabSegment
import java.text.SimpleDateFormat
import java.util.*


class AddItemAcitivity :AppCompatActivity() {

    private lateinit var tb_moneyaddtitle: TitleBar
    private lateinit var viewPager: ViewPager
    private lateinit var tabsegment: TabSegment
    private  var pages = ContentPage.getPageNames()
    var adapter = FragmentAdapter<Fragment>(supportFragmentManager)
    private var inout = arrayListOf<String>("收入","支出")
    val inorout = arrayListOf<String>("totalin","totalout")

    private val dbHelper = MyDBHelper(this, "incoutmoeny.db",1)
    private var db : SQLiteDatabase? = null

    //编辑操作的接收数据
    private   var updateinout = ""
    private var updatetime = ""
    private var updatekey = 0
    private var updatemoney= 0f
    private  var updateinfo:String = ""
    private var updatetype = ""

    private var flag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money_add)

        db = dbHelper.writableDatabase

        val intent = getIntent()
        if(intent.hasExtra("key")){
            updateinfo = intent.getStringExtra("info")!!
            updateinout = intent.getStringExtra("inout")!!
            updatekey = intent.getIntExtra("key",0)
            updatemoney = intent.getFloatExtra("money",0f)
            updatetime = intent.getStringExtra("time")!!
            updatetype = intent.getStringExtra("type")!!
            flag = true
            //设置初始化数据
            if (updateinout==inout[0]){
                initAddView(0)
            }
            else{
                initAddView(1)
            }



        }else{
            initAddView(0)
        }

       // updateSetDate()





    }





    private fun initAddView(initpostion:Int){
        //初始化标题栏并设置监听
        tb_moneyaddtitle = findViewById(R.id.titlebar_moneyaddtitle)
        tb_moneyaddtitle.setLeftClickListener{
            finishMainMoneyActivity()
        }
        tb_moneyaddtitle.addAction(object:TitleBar.TextAction("保存"){
            override fun performAction(view: View?) {

                savefinalDate()
            }
        })


        viewPager = findViewById(R.id.contentViewPager)
        tabsegment = findViewById(R.id.tabSegment)




        for (page in pages){
            tabsegment.addTab(TabSegment.Tab(page))
        }

        //根据先传值
        val Incomefragment = IncomChooseFragment()
        val Outcomefragemt = OutcomeChooseFragment()
        if (flag){
            //传值
            val bundle = Bundle()
            bundle.putString("time",updatetime)
            bundle.putString("info",updateinfo)
            bundle.putString("type",updatetype)
            bundle.putFloat("money",updatemoney)
            if (updateinout=="收入"){
                Incomefragment.arguments = bundle
            }else{
                Outcomefragemt.arguments = bundle
            }

        }



        adapter.addFragment(Incomefragment,pages[0])
        adapter.addFragment(Outcomefragemt,pages[1])
        viewPager.adapter = adapter
        viewPager.setCurrentItem(initpostion,false)
        tabsegment.setupWithViewPager(viewPager,false)
        tabsegment.mode = TabSegment.MODE_FIXED







    }


    //选择那个一
    private fun savefinalDate(){
        if (flag){
            saveUpdateDate()
        }
        else
            saveInputData()



    }
    private fun finishMainMoneyActivity(){
        val intent = Intent()

        intent.setAction("Main.close")
        sendBroadcast(intent)
        finish()
        startActivity(Intent().apply{setClass(this@AddItemAcitivity, MoneyMainActivity::class.java)})
    }

    //用于编辑的情况
    private fun saveUpdateDate(){


        //获取当前的页面号和页面
        val currentpageId = viewPager.currentItem
        val currentpage = adapter.getItem(currentpageId)

        //通过页面找到各个控件的id，从而获取值
        val et_money_number = currentpage.view?.findViewById<EditText>(R.id.et_moenyinput)
        val et_money_info = currentpage.view?.findViewById<EditText>(R.id.et_moenyinfo)
        val et_money_class = currentpage.view?.findViewById<EditText>(R.id.et_moenyclass)
        val et_money_time = currentpage.view?.findViewById<EditText>(R.id.et_moenytime)


        //将输入的钱的数额转化成小数
        var moneyvalue = (et_money_number?.text.toString()).toFloatOrNull()



        if(et_money_class?.text!!.isEmpty() || et_money_info?.text!!.isEmpty() || et_money_time?.text!!.isEmpty()){
            Toast.makeText(this, "所有内容不能为空", Toast.LENGTH_SHORT).show()
        }
        else if(moneyvalue == null)
        {
            Toast.makeText(this, "请输入格式正确的金额", Toast.LENGTH_SHORT).show()
        }else{

            //对时间进行判断，如果时间小于本月，则不加入条目和分类统计数据库，只加入总的收入支出数据库
            val simpleDateFormat = SimpleDateFormat("yyyy-MM")
            val date = Date(System.currentTimeMillis())
            var datestr = simpleDateFormat.format(date)

            if (et_money_time.text.toString() >= datestr) //是当前月份
            {
                //对于record数据库，对当前给条目进行更新
                val values = ContentValues().apply {

                    put("recordTime", et_money_time.text.toString())
                    put("recordInOut", inout[currentpageId])
                    put("recordType", et_money_class.text.toString())
                    put("recordMoney", moneyvalue)
                    put("recordInfo", et_money_info.text.toString())

                }
                db?.update("record",values,"id=${updatekey}",null)

                //对inrecord数据库
                /*
                1.收入支出类别没有变，在数据上进行修改
                2.收入支出发生了变化
                 */
                if(inout[currentpageId]=="收入" && updateinout == "收入") //只在收入数据库上进行修改
                {
                    updateData1("incomeType","inrecord",et_money_class.text.toString(),moneyvalue,"incomeType","inrecord",updatetype,updatemoney,et_money_time.text.toString())

                }
                else if(inout[currentpageId]=="支出" && updateinout == "收入")
                {
                    updateData1("outcomeType","outrecord",et_money_class.text.toString(),moneyvalue,"incomeType","inrecord",updatetype,updatemoney,et_money_time.text.toString())

                }
                else if(inout[currentpageId]=="收入" && updateinout == "支出"){
                    updateData1("incomeType","inrecord",et_money_class.text.toString(),moneyvalue,"outcomeType","outrecord",updatetype,updatemoney,et_money_time.text.toString())

                }
                else{
                    updateData1("outcomeType","outrecord",et_money_class.text.toString(),moneyvalue,"outcomeType","outrecord",updatetype,updatemoney,et_money_time.text.toString())

                }

            }
            else{ //时间的变化，不是本月
                //对于record直接删除
                db?.delete("record","id=${updatekey}",null)
                //对于其他的也直接减掉
                var typev = "incomeType"
                var inouts = "inrecord"
                if(updateinout=="支出"){
                    typev = "outcomeType"
                    inouts = "outrecord"

                }

                updateData1(typev ,inouts,updatetype,0f,typev,inouts,updatetype,updatemoney,et_money_time.text.toString())


            }

            //对总的数据库进行修改
            //先找到本月的数据进行减去老的数据，再加上新的数据


            //但要加入总的收入数据库中
            var totalidx = 1
            if(updateinout=="收入")
                totalidx=0
            var cursor = (db ?: dbHelper.writableDatabase).query(
                "totalrecord",
                null,
                "recordTime=?",
                arrayOf(datestr),
                null,
                null,
                null
            )
            if(cursor.moveToFirst()){
                val oldnew = cursor.getFloat(cursor.getColumnIndex(inorout[totalidx]))-updatemoney
                val oldnew1 = ContentValues().apply {
                    put(inorout[totalidx],oldnew)
                }
                db?.update("totalrecord",oldnew1,"recordTime=?", arrayOf(datestr))
            }


            val inputdate = formatTime(et_money_time.text.toString()) //变成yyyy-MM的形式
             cursor = (db ?: dbHelper.writableDatabase).query(
                "totalrecord",
                null,
                "recordTime=?",
                arrayOf(inputdate),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()){ //已经存在
                val newtotalmoney =
                    cursor.getFloat(cursor.getColumnIndex(inorout[currentpageId])) + moneyvalue //加入得到的数据
                val totalval = ContentValues().apply {
                    put(inorout[currentpageId],newtotalmoney)
                }

                db?.update(
                    "totalrecord",
                    totalval,
                    "recordTime=?",
                    arrayOf(inputdate)
                )
            }
            else{
                //不存在创建

                val totalval = ContentValues().apply {
                    put("recordTime",inputdate)
                    put(inorout[currentpageId],moneyvalue)
                    put(inorout[1-currentpageId],0f)
                }

                db?.insert("totalrecord",null,totalval)
            }


            finishMainMoneyActivity()

        }



    }

    //用于处理添加数据的情况
    private fun saveInputData(){





        //获取当前的页面号和页面
        val currentpageId = viewPager.currentItem
        val currentpage = adapter.getItem(currentpageId)

        //通过页面找到各个控件的id，从而获取值
        val et_money_number = currentpage.view?.findViewById<EditText>(R.id.et_moenyinput)
        val et_money_info = currentpage.view?.findViewById<EditText>(R.id.et_moenyinfo)
        val et_money_class = currentpage.view?.findViewById<EditText>(R.id.et_moenyclass)
        val et_money_time = currentpage.view?.findViewById<EditText>(R.id.et_moenytime)



        //将输入的钱的数额转化成小数
        var moneyvalue = (et_money_number?.text.toString()).toFloatOrNull()



        if(et_money_class?.text!!.isEmpty() || et_money_info?.text!!.isEmpty() || et_money_time?.text!!.isEmpty()){
            Toast.makeText(this, "所有内容不能为空", Toast.LENGTH_SHORT).show()
        }
        else if(moneyvalue == null)
        {
            Toast.makeText(this, "请输入格式正确的金额", Toast.LENGTH_SHORT).show()
        }

        else {

            //对时间进行判断，如果时间小于本月，则不加入条目和分类统计数据库，只加入总的收入支出数据库
            val simpleDateFormat = SimpleDateFormat("yyyy-MM")
            val date = Date(System.currentTimeMillis())
            var datestr = simpleDateFormat.format(date)

            if (et_money_time.text.toString() >= datestr) //只有比当前月份大才行
            {
                val values = ContentValues().apply {

                put("recordTime", et_money_time.text.toString())
                put("recordInOut", inout[currentpageId])
                put("recordType", et_money_class.text.toString())
                put("recordMoney", moneyvalue)
                put("recordInfo", et_money_info.text.toString())

            }

            //插入入条目数据库
            db?.insert("record", null, values)

            //更新输入或者支出数据库
            if (inout[currentpageId] == "收入") {
                val cursor = (db ?: dbHelper.writableDatabase).query(
                    "inrecord",
                    null,
                    "incomeType=?",
                    arrayOf(et_money_class.text.toString()),
                    null,
                    null,
                    null
                )
                if (cursor.moveToFirst()) //如果为真，就存在这数据
                {
                    val newmoney =
                        cursor.getFloat(cursor.getColumnIndex("money")) + moneyvalue //加入得到的数据
                    val values1 = ContentValues().apply {
                        put("money", newmoney)

                    }
                    db?.update(
                        "inrecord",
                        values1,
                        "incomeType=?",
                        arrayOf(et_money_class.text.toString())
                    )
                } else { //不存在就插入这条数据

                    val values2 = ContentValues().apply {
                        put("recordTime",et_money_time.text.toString())
                        put("incomeType", et_money_class.text.toString())
                        put("money", moneyvalue)
                    }
                    db?.insert("inrecord", null, values2)
                }

                cursor.close()
            } else if (inout[currentpageId] == "支出") { //支出

                val cursor = (db ?: dbHelper.writableDatabase).query(
                    "outrecord",
                    null,
                    "outcomeType=?",
                    arrayOf(et_money_class.text.toString()),
                    null,
                    null,
                    null
                )
                if (cursor.moveToFirst()) //如果为真，就存在这数据
                {
                    val newmoney =
                        cursor.getFloat(cursor.getColumnIndex("money")) + moneyvalue //加入得到的数据
                    val values1 = ContentValues().apply {
                        put("money", newmoney)
                    }
                    db?.update(
                        "outrecord",
                        values1,
                        "outcomeType=?",
                        arrayOf(et_money_class.text.toString())
                    )
                } else { //不存在就插入这条数据，并且要插入时间

                    val values2 = ContentValues().apply {
                        put("outcomeType", et_money_class.text.toString())
                        put("recordTime",et_money_time.text.toString())
                        put("money", moneyvalue)
                    }
                    db?.insert("outrecord", null, values2)
                }
                cursor.close()

            }


                //但要加入总的收入数据库中

                val inputdate = formatTime(et_money_time.text.toString()) //变成yyyy-MM的形式
                val cursor = (db ?: dbHelper.writableDatabase).query(
                    "totalrecord",
                    null,
                    "recordTime=?",
                    arrayOf(inputdate),
                    null,
                    null,
                    null
                )

                if (cursor.moveToFirst()){ //已经存在
                    val newtotalmoney =
                        cursor.getFloat(cursor.getColumnIndex(inorout[currentpageId])) + moneyvalue //加入得到的数据
                    val totalval = ContentValues().apply {
                        put(inorout[currentpageId],newtotalmoney)
                    }

                    db?.update(
                        "totalrecord",
                        totalval,
                        "recordTime=?",
                        arrayOf(inputdate)
                    )
                }
                else{
                    //不存在创建

                    val totalval = ContentValues().apply {
                        put("recordTime",inputdate)
                        put(inorout[currentpageId],moneyvalue)
                        put(inorout[1-currentpageId],0f)
                    }

                    db?.insert("totalrecord",null,totalval)
                }



            }


            //但要加入总的收入数据库中

            val inputdate = formatTime(et_money_time.text.toString()) //变成yyyy-MM的形式
            val cursor = (db ?: dbHelper.writableDatabase).query(
                "totalrecord",
                null,
                "recordTime=?",
                arrayOf(inputdate),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()){ //已经存在
                val newtotalmoney =
                    cursor.getFloat(cursor.getColumnIndex(inorout[currentpageId])) + moneyvalue //加入得到的数据
                val totalval = ContentValues().apply {
                    put(inorout[currentpageId],newtotalmoney)
                }

                db?.update(
                    "totalrecord",
                    totalval,
                    "recordTime=?",
                    arrayOf(inputdate)
                )
            }
            else{
                //不存在创建

                val totalval = ContentValues().apply {
                    put("recordTime",inputdate)
                    put(inorout[currentpageId],moneyvalue)
                    put(inorout[1-currentpageId],0f)
                }

                db?.insert("totalrecord",null,totalval)
            }



            //结束,这里要通过广播结束前一个activity



            finishMainMoneyActivity()

        }



    }

    //设置原始数据
    private fun updateSetDate()
    {
        //获取当前的页面号和页面
        val currentpageId = viewPager.currentItem
        val currentpage = adapter.getItem(currentpageId)

        //通过页面找到各个控件的id，从而获取值
        val et_money_number = currentpage.view?.findViewById<EditText>(R.id.et_moenyinput)
        val et_money_info = currentpage.view?.findViewById<EditText>(R.id.et_moenyinfo)
        val et_money_class = currentpage.view?.findViewById<EditText>(R.id.et_moenyclass)
        val et_money_time = currentpage.view?.findViewById<EditText>(R.id.et_moenytime)

        et_money_class?.setText(updatetype)
        et_money_number?.setText(updatemoney.toString())
        et_money_time?.setText(updatetime)
        et_money_info?.setText(updateinfo)
    }

    //用于在时间满足的情况下更新inrecord 和 outrecord
    private fun updateData1(newtype1:String, newinout: String, newtype: String, newmoney:Float, oldtype1:String,oldinout: String, oldtype: String, oldmoney:Float, date:String){


        //先从老的表中减去改数据
        var cursor = (db ?: dbHelper.writableDatabase).query(
            oldinout,
            null,
            "${oldtype1}=?",
            arrayOf(oldtype),
            null,
            null,
            null
        )
        cursor.moveToFirst()
        val oldnewmoney = cursor.getFloat(cursor.getColumnIndex("money"))-oldmoney

        //如果数据变成0了，就删去这个条目
        if (oldnewmoney==0f){
            db?.delete(oldinout,"${oldtype1}=?", arrayOf(oldtype))
        }
        else{
            val oldnewmoney1 = ContentValues().apply {
                put("money",oldnewmoney)
            }
            db?.update(oldinout,oldnewmoney1,"${oldtype1}=?", arrayOf(oldtype))
        }




        if(newmoney!=0f)//不为0，表明是本月
        {
            //再在新的表中加上该数据
            cursor = (db ?: dbHelper.writableDatabase).query(
                newinout,
                null,
                "${newtype1}=?",
                arrayOf(newtype),
                null,
                null,
                null
            )
            if (cursor.moveToFirst())
            {
                val newnewmoney = cursor.getFloat(cursor.getColumnIndex("money"))+newmoney
                val newnewmoney1 = ContentValues().apply {
                    put("money",newnewmoney)
                }
                db?.update(newinout,newnewmoney1,"${newtype1}=?", arrayOf(newtype))
            }
            else{ //不存在，插入
                val values2 = ContentValues().apply {
                    put(newtype1, newtype)
                    put("recordTime",date)
                    put("money", newmoney)
                }
                db?.insert(newinout, null, values2)
            }
        }




        cursor.close()




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



    private fun formatTime(time:String):String{

        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = formatter.parse(time)

        val formatter1 = SimpleDateFormat("yyyy-MM") //获取年月即可
        val date1 = formatter1.format(date)
        return date1
    }

}

