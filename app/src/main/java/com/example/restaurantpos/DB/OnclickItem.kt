package com.example.restaurantpos.DB

interface OnclickItem {

     fun onItemClick(restaurantId: Int?) {

    }
     fun onItemClick(restaurantId: Int?,menu_name:String?,menu_price:String?) {

    }
}