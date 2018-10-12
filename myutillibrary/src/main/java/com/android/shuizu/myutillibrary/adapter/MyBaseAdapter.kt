package com.android.shuizu.myutillibrary.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/21/021.
 */
abstract class MyBaseAdapter(private val layoutId: Int) : RecyclerView.Adapter<MyBaseAdapter.MyViewHolder>() {

    var myOnItemClickListener: MyOnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            myOnItemClickListener?.onItemClick(this, holder.itemView, position)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface MyOnItemClickListener {
        fun onItemClick(parent: MyBaseAdapter, view: View, position: Int)
    }
}