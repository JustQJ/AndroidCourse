package com.bytedance.jstu.p.money

//用于接收从数据库中读出来的数据
//时间，收入还是支出，类型，数额，消息备注
data class Record (var key: Int,var recordTime: String, var recordInOut: String, var recordType:String, var recordMoney: Float, var recordInfo:String)

