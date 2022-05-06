package com.bytedance.jstu.homework.homework5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.jstu.homework.R

class MainFiveActivity : AppCompatActivity() {

    //val mActivity = this
    private val dbHelper = MyDBHelper(this, "todolist.db",1)
    private var db : SQLiteDatabase? = null
    private lateinit var receiver : myBroadcastReceiver



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_five_main)
        db = dbHelper.writableDatabase
        //遍历数据库，拿到数据
        val data = arrayListOf<Event>()
        //db = dbHelper.writableDatabase
        val cursor = (db?: dbHelper.writableDatabase).query("event", null, null, null, null, null, null, null)

        if (cursor.moveToLast()){
            do{
                val Name = cursor.getString(cursor.getColumnIndex("planName"))
                val Info = cursor.getString(cursor.getColumnIndex("planInfo"))
                val Start = cursor.getString(cursor.getColumnIndex("startTime"))
                val End = cursor.getString(cursor.getColumnIndex("endTime"))

                data.add(Event(Name, Info, Start, End))
            } while (cursor.moveToPrevious())
        }
        cursor.close()

        //将数据使用cycle view发布出去
        val todolist = findViewById<RecyclerView>(R.id.todolist)

        todolist.layoutManager = LinearLayoutManager(this)

        val adapter = EventViewAdapter()
        //var data1 = data.asReversed()
        adapter.setContentList(data)
        todolist.adapter = adapter


        adapter.setOnItemClickListener(
            object:EventViewAdapter.OnItemClickListener{
                override fun deleteClick(view: View, position: Int) {
                   //对这个item进行删除
                    db?.delete("event","planName=? and planInfo = ?", arrayOf(data[position].planName, data[position].planInfo))
                    refresh()
                }

                override fun updateClick(view: View, position: Int) {

                    //item进行更新，即开一个intent
                    val intent = Intent()
                    intent.setClass(this@MainFiveActivity, UpdateEventActivity::class.java)
                    intent.putExtra("name",data[position].planName)
                    intent.putExtra("info", data[position].planInfo)
                    intent.putExtra("start",data[position].startTime)
                    intent.putExtra("end",data[position].endTime)

                    startActivity(intent)

                }
            }

        )




        val addlistevent = findViewById<ImageView>(R.id.addlist)

        addlistevent.setOnClickListener(){
            val intent = Intent()
            intent.setClass(this, AddListActivity::class.java)

            startActivity(intent)

        }


        val intentFilter = IntentFilter()
        intentFilter.addAction("Main.close")
        receiver = myBroadcastReceiver()
        registerReceiver(receiver,intentFilter)


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




    private fun refresh(){
        val intent = Intent()
        intent.setClass(this,MainFiveActivity::class.java)
        finish()
        startActivity(intent)


    }
}