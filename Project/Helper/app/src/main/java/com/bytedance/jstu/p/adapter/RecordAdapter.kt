package com.bytedance.jstu.p.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.jstu.p.R
import com.bytedance.jstu.p.money.Record

class RecordAdapter :  RecyclerView.Adapter<RecordAdapter.EventViewHolder>(){

    private val contentList = mutableListOf<Record>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        val v = View.inflate(parent.context, R.layout.record_list_layout,null)
        val itemholder =  EventViewHolder(v)
        return itemholder
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(contentList[position])

    }
    override fun getItemCount(): Int {
        return contentList.size

    }

    fun setContentList(list : List<Record>){
        contentList.clear()
        contentList.addAll(list)
        notifyDataSetChanged()
    }


    class  EventViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val recordtime = view.findViewById<TextView>(R.id.tv_recordtime)
        private val recordinfo = view.findViewById<TextView>(R.id.tv_recordinfo)
        private val recordmoeny = view.findViewById<TextView>(R.id.tv_recordmoeny)
        private val recordtype = view.findViewById<TextView>(R.id.tv_recordtype)

        fun bind(content:Record){
            recordtime.text = content.recordTime
            recordinfo.text = content.recordInfo
            if(content.recordInOut=="支出")
            {
                recordmoeny.text = (-content.recordMoney).toString()
            }
            else
                recordmoeny.text = (content.recordMoney).toString()
            recordtype.text = content.recordType
        }
    }
}