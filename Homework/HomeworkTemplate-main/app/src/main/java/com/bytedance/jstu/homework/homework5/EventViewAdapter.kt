package com.bytedance.jstu.homework.homework5

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.jstu.homework.R



class EventViewAdapter : RecyclerView.Adapter<EventViewAdapter.EventViewHolder>(){

    private val contentList = mutableListOf<Event>()
    private  var mListener: OnItemClickListener? = null

    interface OnItemClickListener{
        fun deleteClick(view: View, position: Int)
        fun updateClick(view: View, position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
       val v = View.inflate(parent.context, R.layout.event_list_layout,null)


        val itemholder =  EventViewHolder(v)

        //对当前这个item的delete image进行点击
        itemholder.delete.setOnClickListener(){
            val position = itemholder.adapterPosition
            //Toast.makeText(parent.context,"你点击了" + contentList[position].planName + "图片",Toast.LENGTH_SHORT).show()
            mListener?.deleteClick(itemholder.itemView, position)
        }
        itemholder.modify.setOnClickListener(){
            val position = itemholder.adapterPosition
            mListener?.updateClick(itemholder.itemView, position)
        }

        return itemholder
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(contentList[position])


    }

    override fun getItemCount(): Int {
        return contentList.size

    }



    fun setContentList(list : List<Event>){
        contentList.clear()
        contentList.addAll(list)
        notifyDataSetChanged()
    }












    class  EventViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val eventname = view.findViewById<TextView>(R.id.name)
        private val eventinfo = view.findViewById<TextView>(R.id.info)
        private val eventstart = view.findViewById<TextView>(R.id.start)
        private val eventend = view.findViewById<TextView>(R.id.end)
        val modify = view.findViewById<ImageView>(R.id.modify)
        val delete = view.findViewById<ImageView>(R.id.delete)
        fun bind(content:Event){
            eventname.text = content.planName
            eventinfo.text = content.planInfo
            eventstart.text = content.startTime
            eventend.text = content.endTime
        }
    }




}