package com.bytedance.jstu.homework.homework5

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class MyDBHelper (val context: Context, name:String, version: Int): SQLiteOpenHelper(context,name, null, version){

    private val createEvent = "create table event(" +
            "id integer primary key autoincrement,"+
            "planName text,"+
            "planInfo text," +
            "startTime text,"+
            "endTime text)"

    override fun onCreate(db:SQLiteDatabase?){
        db?.execSQL(createEvent)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

}