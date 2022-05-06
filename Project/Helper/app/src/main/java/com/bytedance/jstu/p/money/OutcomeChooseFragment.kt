package com.bytedance.jstu.p.money

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.bytedance.jstu.p.R
import com.xuexiang.xui.utils.ResUtils
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText
import com.xuexiang.xui.widget.picker.widget.TimePickerView
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder
import com.xuexiang.xutil.data.DateUtils
import java.util.*


class OutcomeChooseFragment : Fragment() {
    //输入框

    lateinit var et_money_time: EditText
    lateinit var et_money_class: EditText
    lateinit var et_money_info: EditText
    lateinit var et_money_number: MaterialEditText


    //选择器的数组
    private lateinit var classOption: Array<String>
    private var classSelectOption = 0
    private var mDatePicker: TimePickerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outcome, container, false)

        et_money_class = view.findViewById(R.id.et_moenyclass)
        et_money_info = view.findViewById(R.id.et_moenyinfo)
        et_money_number = view.findViewById(R.id.et_moenyinput)
        et_money_time = view.findViewById(R.id.et_moenytime)
        classOption = ResUtils.getStringArray(R.array.outmoney_item_class_values)
        myPickerListener()

        val bundle = arguments
        if (bundle!=null){
            val time = bundle.getString("time")
            val info = bundle.getString("info")
            val money = bundle.getFloat("money")
            val type = bundle.getString("type")
            initview(money,time,info,type)
        }



        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    private fun initview(money:Float,time:String?,info:String?,type:String?){

        //利用传递的数据给view设置值

        et_money_class.setText(type)
        et_money_time.setText(time)
        et_money_info.setText(info)
        et_money_number.setText(money.toString())


    }


    //为每个选择输入框（类别，时间）设置监听器
    private fun myPickerListener() {


        et_money_class.setOnClickListener {


            val classpvOptions = OptionsPickerBuilder(
                context
            ) { v: View?, options1: Int, options2: Int, options3: Int ->
                et_money_class.setText(classOption[options1])
                classSelectOption = options1
                false
            }
                .setTitleText(getString(R.string.classSelectTitle))
                .setSelectOptions(classSelectOption)
                .build<Any>()
            classpvOptions.setPicker(classOption)
            classpvOptions.show()


        }

        et_money_time.setOnClickListener {

            if (mDatePicker == null) {
                mDatePicker = TimePickerBuilder(
                    context
                ) { date: Date?, v: View? ->

                    et_money_time.setText(DateUtils.date2String(date, DateUtils.yyyyMMdd.get()))

                }
                    .setTimeSelectChangeListener { date: Date? ->
                        Log.i(
                            "pvTime",
                            "onTimeSelectChanged"
                        )
                    }
                    .setTitleText("日期选择")
                    .build()
//            // 这样设置日月年显示
//            mDatePicker.getWheelTime().getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            mDatePicker!!.show()

        }


    }











}
/*
fun Activity.hideSoftKeyboard(editText: EditText){
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(editText.windowToken, 0)
    }
}*/