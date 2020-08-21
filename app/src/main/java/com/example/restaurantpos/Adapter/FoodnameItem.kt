package com.example.restaurantpos.Adapter

class FoodnameItem(Restaurant_ID: Int, foodName: String?, foodPrice: String?) {
    private var restaurant_ID: Int? = Restaurant_ID
    private var foodName: String? = foodName
    private var foodPrice: String? = foodPrice

    fun getRestaurant_ID(): Int? {
        return restaurant_ID
    }
    fun getFoodname(): String? {
        return foodName
    }
    fun getFoodprice(): String? {
        return foodPrice
    }

}