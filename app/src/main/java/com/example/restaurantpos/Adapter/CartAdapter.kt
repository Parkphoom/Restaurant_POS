package com.example.restaurantpos.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.DB.OnclickItem
import com.example.restaurantpos.R

class CartAdapter(
    context: Context,
    mCartList: List<CartItem>?,
    param: OnclickItem
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mCartList: List<CartItem>? = mCartList
    private var mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.rccart_item, parent, false)
        return CartAdapter.ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CartAdapter.ItemViewHolder) {
            ItemRows(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return mCartList?.size!!
    }


    private class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var textName: TextView
        var textPrice: TextView
        var textCount: TextView

        init {
            textName = itemView.findViewById(R.id.tv_menuname)
            textPrice = itemView.findViewById(R.id.tv_menuprice)
            textCount = itemView.findViewById(R.id.tv_menucount)
        }
    }

    private fun ItemRows(holder: CartAdapter.ItemViewHolder, position: Int) {
        val currentItem: CartItem = this.mCartList!![position]
        holder.textName.text = currentItem.getFoodname()
        holder.textPrice.text = currentItem.getFoodprice()
        holder.textCount.text = currentItem.getFoodcount()



    }

}