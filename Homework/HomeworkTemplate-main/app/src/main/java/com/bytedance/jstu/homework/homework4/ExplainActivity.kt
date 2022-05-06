package com.bytedance.jstu.homework.homework4
import com.bytedance.jstu.homework.homework4.api.TranslatorBean
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import android.widget.TextView
import java.io.IOException
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import com.bytedance.jstu.homework.R

class ExplainActivity : AppCompatActivity() {


    private var explain : TextView? = null
    //private var wordcontext : TextView? = null

    private val okhttpListener = object : okhttp3.EventListener() {
        @SuppressLint("SetTextI18n")
        override fun dnsStart(call: okhttp3.Call, domainName: String) {
            super.dnsStart(call, domainName)
           // updateShowTextView("\nDns Search: $domainName")
        }

        @SuppressLint("SetTextI18n")
        override fun responseBodyStart(call: okhttp3.Call) {
            super.responseBodyStart(call)
            //updateShowTextView("\nResponse Start")
        }
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
       .addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener)
        .build()

    private val gson = GsonBuilder().create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explain)

        val intent = getIntent()
        val word = intent.getStringExtra("wordname")

        var wordcontext = findViewById<TextView>(R.id.wordName)
        explain = findViewById<TextView>(R.id.explain)

        wordcontext.text = word
        val url = "https://dict.youdao.com/jsonapi?q=${word}"
        updateShowTextView("",false)
        getexplain(url)

    }

    @SuppressLint("SetTextI18n")
    private fun updateShowTextView(text: String, append: Boolean = true) {
        if (Looper.getMainLooper() !== Looper.myLooper()) {
            // 子线程，提交到主线程中去更新 UI.
            runOnUiThread {
                updateShowTextView(text, append)
            }
        } else {
            explain?.text = if (append) explain?.text.toString() + text else text
        }
    }

    private fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client.newCall(request).enqueue(callback)
    }
    private fun getexplain(url: String){
        request(url, object : Callback{
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                updateShowTextView(e.message.toString(),false)
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: okhttp3.Call, response: Response) {
                var respFormatText =  "\n"
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val transBean = gson.fromJson(bodyString, TranslatorBean::class.java)




                    try {
                        if(transBean.meta.dicts.indexOf("syno")>=0){
                            var synocont = 0
                            for (eachitem in transBean.syno.synos){
                                synocont ++
                                respFormatText += synocont.toString()
                                respFormatText += ". "
                                respFormatText += "${eachitem.syno.pos} ${eachitem.syno.tran} \n"

                            }
                        }

                        else if(transBean.meta.dicts.indexOf("expand_ec")>=0){
                            var eccont = 0
                            for (eachitem in transBean.expand_ec.word){
                                eccont ++
                                respFormatText += eccont.toString()
                                respFormatText += ". "
                                respFormatText += eachitem.pos

                                for (each in eachitem.transList){
                                    respFormatText += each.tran
                                    respFormatText += "  "
                                }
                                respFormatText += ".\n"

                            }
                        }
                        else
                            respFormatText = "Can't not find the translation, please change a word.\n"
                    }
                    catch (e:Exception) {
                      respFormatText = "Can't not find the translation, please change a word.\n"
                    }




                    Log.i(transBean.meta.dicts[0],transBean.meta.dicts[0])

                } else {
                    respFormatText ="\n\n\nResponse fail: ${response.body?.string()}, http status code: ${response.code}."
                }
                updateShowTextView(respFormatText)
            }
        })
    }


}

