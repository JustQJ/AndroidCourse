package com.bytedance.jstu.homework.homework4

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.jstu.homework.R

class MainFourActivity:AppCompatActivity() {




    private var editText: EditText? = null
    private var text: TextView? = null
    private var his2: TextView? = null
    private var his3: TextView? = null
    private var his4: TextView? = null
    private var his5: TextView? = null
    private var his6: TextView? = null
    private var his7: TextView? = null
    private var his8: TextView? = null
    private var his9: TextView? = null
    private var his10: TextView? = null
    private var his1: TextView? = null

    //var hiss = listOf<TextView?>(his1,his2,his3,his4,his5,his6,his7,his8,his9,his10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_four_main)

        editText = findViewById<EditText>(R.id.search)
        text = findViewById<TextView>(R.id.showtext)
        his2 = findViewById<TextView>(R.id.his2)
        his3 = findViewById<TextView>(R.id.his3)
        his4 = findViewById<TextView>(R.id.his4)
        his5 = findViewById<TextView>(R.id.his5)
        his6 = findViewById<TextView>(R.id.his6)
        his7 = findViewById<TextView>(R.id.his7)
        his8 = findViewById<TextView>(R.id.his8)
        his9 = findViewById<TextView>(R.id.his9)
        his10 = findViewById<TextView>(R.id.his10)
        his1 = findViewById<TextView>(R.id.his1)



        text?.text = ""

        editText?.setOnEditorActionListener(){
                v, actionId, event-> if (actionId == EditorInfo.IME_ACTION_SEARCH){
            //Log.i(editText.text.toString(),editText.text.toString())
            if (editText?.text.toString() == "")
            {
                text?.text = "输入的单词不能为空"
            }
            else{
                text?.text = ""


                updateHistory(editText?.text.toString())

                val  intent  = Intent()

                intent.setClass(this,ExplainActivity::class.java)
                intent.putExtra("wordname",editText?.text.toString())
                startActivity(intent)
                //startActivity(Intent(this@MainActivity,ExplainActivity::class.java))

            }
            false

        }else{
            true
        }
        }

        clickHistory()


    }

    fun updateHistory(newHis:String)
    {
        when(newHis){
            his1?.text -> {}
            his2?.text -> {
                his2?.text = his1?.text
                his1?.text = newHis
            }
            his3?.text ->{
                his3?.text = his2?.text
                his2?.text = his1?.text
                his1?.text = newHis
            }
            his4?.text ->{
                his4?.text = his3?.text
                his3?.text = his2?.text
                his2?.text = his1?.text
                his1?.text = newHis
            }

            his5?.text ->{
                his5?.text = his4?.text
                his4?.text = his3?.text
                his3?.text = his2?.text
                his2?.text = his1?.text
                his1?.text = newHis
            }


            his6?.text->{
                his6?.text = his5?.text
                his5?.text = his4?.text
                his4?.text = his3?.text
                his3?.text = his2?.text
                his2?.text = his1?.text
                his1?.text = newHis
            }

            his7?.text->{
                his7?.text = his6?.text
                his6?.text = his5?.text
                his5?.text = his4?.text
                his4?.text = his3?.text
                his3?.text = his2?.text
                his2?.text = his1?.text
                his1?.text = newHis
            }
            his8?.text ->{
                his8?.text = his7?.text
                his7?.text = his6?.text
                his6?.text = his5?.text
                his5?.text = his4?.text
                his4?.text = his3?.text
                his3?.text = his2?.text
                his2?.text = his1?.text
                his1?.text = newHis
            }

            his9?.text ->{
                his9?.text = his8?.text
                his8?.text = his7?.text
                his7?.text = his6?.text
                his6?.text = his5?.text
                his5?.text = his4?.text
                his4?.text = his3?.text
                his3?.text = his2?.text
                his2?.text = his1?.text
                his1?.text = newHis
            }
            else ->{
                his10?.text = his9?.text
                his9?.text = his8?.text
                his8?.text = his7?.text
                his7?.text = his6?.text
                his6?.text = his5?.text
                his5?.text = his4?.text
                his4?.text = his3?.text
                his3?.text = his2?.text
                his2?.text = his1?.text
                his1?.text = newHis
            }

        }




    }


    fun clickHistory(){
        his1?.setOnClickListener(){
            useHistary(his1)
        }
        his2?.setOnClickListener(){
            useHistary(his2)
        }
        his3?.setOnClickListener(){
            useHistary(his3)
        }
        his4?.setOnClickListener(){
            useHistary(his4)
        }
        his5?.setOnClickListener(){
            useHistary(his5)
        }
        his6?.setOnClickListener(){
            useHistary(his6)
        }
        his7?.setOnClickListener(){
            useHistary(his7)
        }
        his8?.setOnClickListener(){
            useHistary(his8)
        }
        his9?.setOnClickListener(){
            useHistary(his9)
        }
        his10?.setOnClickListener(){
            useHistary(his10)
        }


    }

    fun useHistary(hiss: TextView?){

        if (hiss?.text == "")
        {

        }
        else{


            val  intent  = Intent()
            intent.setClass(this,ExplainActivity::class.java)
            intent.putExtra("wordname",hiss?.text.toString())
            startActivity(intent)
            //startActivity(Intent(this@MainActivity,ExplainActivity::class.java))
            updateHistory(hiss?.text.toString())
        }
    }




}