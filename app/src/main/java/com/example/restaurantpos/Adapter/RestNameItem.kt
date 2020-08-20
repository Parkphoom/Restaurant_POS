package com.example.restaurantpos.Adapter

class RestNameItem(restaurant_ID: Int, restaurantName: String) {
    private var restaurantName: String? = restaurantName
    private var restaurantId: Int? = restaurant_ID

    fun getRestaurantName(): String? {
        return restaurantName
    }
    fun getRestaurant_Id(): Int? {
        return restaurantId
    }


}