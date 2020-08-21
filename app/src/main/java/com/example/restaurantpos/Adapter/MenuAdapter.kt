package com.example.restaurantpos.Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.DB.DBMenuManager
import com.example.restaurantpos.EditRestActivity
import com.example.restaurantpos.R
import com.shreyaspatil.MaterialDialog.MaterialDialog

class MenuAdapter(
    context: Context,
    mNameList: List<FoodnameItem>?,
    dbMenuManager: DBMenuManager?,
    onDelete: EditRestActivity.OnDelete
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mNameList: List<FoodnameItem>? = mNameList
    private var mContext: Context? = context
    private var dbMenuManager: DBMenuManager? = dbMenuManager
    private var onDelete: EditRestActivity.OnDelete = onDelete

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.rvmenu_item, parent, false)
        return MenuAdapter.ItemViewHolder(view)
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
        var textName: TextView = itemView.findViewById(R.id.txt_menuname)
        var textPrice: TextView = itemView.findViewById(R.id.txt_menuprice)
        var del_btn: Button = itemView.findViewById(R.id.del_btn)

    }

    private fun ItemRows(holder: ItemViewHolder, position: Int) {
        val currentItem: FoodnameItem = this.mNameList!![position]
        holder.textName.text = currentItem.getFoodname()
        holder.textPrice.text = currentItem.getFoodprice()
        holder.del_btn.setOnClickListener(View.OnClickListener {
            createDeleteDialog(currentItem.getRestaurant_ID()!!, currentItem.getFoodname()!!,
                currentItem.getFoodprice()!!
            )
        })


    }
    private fun createDeleteDialog(rest_id: Int, name: String, foodprice: String) {
            val mDialog = MaterialDialog.Builder(mContext as Activity)
                .setTitle("ลบเมนู?")
                .setMessage("ข้อมูลรายการอาหาร $name ราคา $foodprice ที่เลือกจะหายไป")
                .setCancelable(true)
                .setPositiveButton(
                    "Delete",
                    R.drawable.icons8_delete_bin_48px_white
                ) { dialogInterface, which ->
                    dbMenuManager?.open()
                    if(dbMenuManager?.deleteMENU(rest_id,name)!!){
                        dbMenuManager?.close()

                        Toast.makeText(mContext, (mContext as Activity).getString(R.string.DeleteSuccess), Toast.LENGTH_SHORT)
                            .show()
                        dialogInterface.dismiss()
                        onDelete.ondeletemenu()
                    }else{

                        Toast.makeText(mContext, (mContext as Activity).getString(R.string.DeleteFailed), Toast.LENGTH_SHORT)
                            .show()
                    }



                }
                .setNegativeButton(
                    "Cancel",
                    R.drawable.icons8_cancel_52px
                ) { dialogInterface, which -> dialogInterface.dismiss() }
                .build()

            // Show Dialog
            mDialog.show()
    }
}