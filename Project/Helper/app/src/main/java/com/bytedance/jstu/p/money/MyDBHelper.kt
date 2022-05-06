package com.bytedance.jstu.p.money

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


//数据库有三张表
class MyDBHelper (val context: Context, name:String, version: Int): SQLiteOpenHelper(context,name, null, version){

    //四个表

    //记录每个月具体每条记录，一个月满了就清除
    private val createRecord = "create table record(" +
            "id integer primary key autoincrement,"+
            "recordTime text,"+
            "recordInOut text," +
            "recordType text,"+
            "recordMoney float,"+
    "recordInfo text)"

    //记录每个月收入分类的数据，满了清除
    private val createIncome ="create table inrecord(" +
            "id integer primary key autoincrement," +
            "recordTime text,"+
            "incomeType text," +
            "money float)"

    //记录每个月支出分类的记录，满了清除
    private val createOutcome ="create table outrecord(" +
            "id integer primary key autoincrement," +
            "recordTime text,"+
            "outcomeType text," +
            "money float)"

    //记录每个月的总支出和总收入，每个月一条记录
    private val createTotal = "create table totalrecord(" +
            "id integer primary key autoincrement," +
            "recordTime text,"+
            "totalin float," +
            "totalout float)"


    override fun onCreate(db:SQLiteDatabase?){
        db?.execSQL(createRecord)
        db?.execSQL(createIncome)
        db?.execSQL(createOutcome)
        db?.execSQL(createTotal)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

}