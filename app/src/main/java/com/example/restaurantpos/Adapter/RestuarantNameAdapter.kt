package com.example.restaurantpos.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.DB.OnclickItem
import com.example.restaurantpos.R


class RestuarantNameAdapter(
    context: Context,
    mNameList: List<RestNameItem>?,
    onclickItem: OnclickItem
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mNameList: List<RestNameItem>? = mNameList
    private var mContext: Context? = context
    private var onclickItem: OnclickItem = onclickItem
    var layoutInflater = LayoutInflater.from(mContext)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.rvrestname_item,
            parent,
            false
        )
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNameList?.size!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            ItemRows(holder, position)
        }
    }

    private class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var textName: TextView = itemView.findViewById(R.id.restaurantname_tv)

    }

    private fun ItemRows(holder: ItemViewHolder, position: Int) {
        val currentItem: RestNameItem = this.mNameList!![position]

        holder.textName.text = currentItem.getRestaurantName()
        holder.textName.setOnClickListener(View.OnClickListener {
            onclickItem.onItemClick(currentItem.getRestaurant_Id())

        })
    }

}