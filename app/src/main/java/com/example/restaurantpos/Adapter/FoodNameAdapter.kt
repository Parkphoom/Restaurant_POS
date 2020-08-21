package com.example.restaurantpos.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.R

class FoodNameAdapter(
    context: Context,
    mNameList: List<FoodnameItem>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mNameList: List<FoodnameItem>? = mNameList
    private var mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rvfoodname_item, parent, false)
        return FoodNameAdapter.ItemViewHolder(view)
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
        var textName: TextView = itemView.findViewById(R.id.foodname_tv)
        var textPrice: TextView = itemView.findViewById(R.id.foodprice_tv)

    }

    private fun ItemRows(holder: ItemViewHolder, position: Int) {
        val currentItem: FoodnameItem = this.mNameList!![position]

        holder.textName.text = currentItem.getFoodname()
        holder.textPrice.text = currentItem.getFoodprice()


    }
}