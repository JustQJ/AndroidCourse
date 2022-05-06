package com.bytedance.jstu.homework.homework5

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.bytedance.jstu.homework.R
import java.util.*
import android.app.DatePickerDialog;


class AddListActivity :  AppCompatActivity() {

    private val planname: EditText by lazy {
        findViewById(R.id.planname)
    }

    private val planinfo: EditText by lazy {
        findViewById(R.id.planinfo)
    }

    private val planstarttime: EditText by lazy {
        findViewById(R.id.planstarttime)
    }

    private val planendtime: EditText by lazy {
        findViewById(R.id.planendtime)
    }

    private val save: TextView by lazy {
        findViewById(R.id.save)
    }

    private lateinit var calendar: Calendar //定义日期



    private val dbHelper = MyDBHelper(this, "todolist.db",1)
    private var db : SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addlist)
        db = dbHelper.writableDatabase


        //设置日期自动弹出框
        calendar = Calendar.getInstance()
        planstarttime.setOnClickListener(){
            calendar = Calendar.getInstance()
            var mYear = calendar[Calendar.YEAR]
            var mMonth = calendar[Calendar.MONTH]
            var mDay = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
                    mYear = year
                    mMonth = month
                    mDay = dayOfMonth
                    if (mMonth+1<10 && mDay<10)
                    {
                        val mDate = "${year}/0${month + 1}/0${dayOfMonth}"
                        planstarttime.setText(mDate)
                    }
                    else if(mMonth+1>=10 && mDay<10)
                    {
                        val mDate = "${year}/${month + 1}/0${dayOfMonth}"
                        planstarttime.setText(mDate)
                    }
                    else if(mMonth+1>=10 && mDay>=10){
                        val mDate = "${year}/${month + 1}/${dayOfMonth}"
                        planstarttime.setText(mDate)
                    }
                    else
                    {
                        val mDate = "${year}/0${month + 1}/${dayOfMonth}"
                        planstarttime.setText(mDate)
                    }
                },
                mYear, mMonth, mDay

            )
            datePickerDialog.show()

        }
        planendtime.setOnClickListener(){
            calendar = Calendar.getInstance()
            var mYear = calendar[Calendar.YEAR]
            var mMonth = calendar[Calendar.MONTH]
            var mDay = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
                    mYear = year
                    mMonth = month
                    mDay = dayOfMonth
                    if (mMonth+1<10 && mDay<10)
                    {
                        val mDate = "${year}/0${month + 1}/0${dayOfMonth}"
                        planendtime.setText(mDate)
                    }
                    else if(mMonth+1>=10 && mDay<10)
                    {
                        val mDate = "${year}/${month + 1}/0${dayOfMonth}"
                        planendtime.setText(mDate)
                    }
                    else if(mMonth+1>=10 && mDay>=10){
                        val mDate = "${year}/${month + 1}/${dayOfMonth}"
                        planendtime.setText(mDate)
                    }
                    else
                    {
                        val mDate = "${year}/0${month + 1}/${dayOfMonth}"
                        planendtime.setText(mDate)
                    }

                },
                mYear, mMonth, mDay

            )
            datePickerDialog.show()

        }




        save.setOnClickListener{
            if (planname.text.isEmpty()){
                Toast.makeText(this, "计划名称不能为空", Toast.LENGTH_SHORT).show()
            }
            else if (planinfo.text.isEmpty()){
                Toast.makeText(this, "计划详情不能为空", Toast.LENGTH_SHORT).show()
            }

            else if (planstarttime.text.isEmpty()){
                Toast.makeText(this, "计划开始时间不能为空", Toast.LENGTH_SHORT).show()
            }
            else if (planendtime.text.isEmpty()) {
                Toast.makeText(this, "计划结束时间不能为空", Toast.LENGTH_SHORT).show()

            }
            //对开始和结束的时间大小进行判断，开始时间不能大于结束时间
            else if(planstarttime.text.toString()>planendtime.text.toString())
            {
                Toast.makeText(this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show()

            }



            else{

                val values = ContentValues().apply {
                    put("planName", planname.text.toString())
                    put("planInfo", planinfo.text.toString())
                    put("startTime", planstarttime.text.toString())
                    put("endTime", planendtime.text.toString())
                }
                db?.insert("event", null, values)
                val intent = Intent()
                intent.setAction("Main.close")
                sendBroadcast(intent)
                finish()
                startActivity(Intent().apply{setClass(this@AddListActivity, MainFiveActivity::class.java)})




            }
        }



    }



}