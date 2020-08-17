package com.example.restaurantpos.Adapter

class RestNameItem(restaurantName: String) {
    private var restaurantName: String? = restaurantName

    fun getQueue(): String? {
        return restaurantName
    }


}