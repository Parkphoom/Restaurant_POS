package com.example.restaurantpos.Adapter

class FoodnameItem(foodName: String?,foodPrice: String?) {
    private var foodName: String? = foodName
    private var foodPrice: String? = foodPrice

    fun getFoodname(): String? {
        return foodName
    }
    fun getFoodprice(): String? {
        return foodPrice
    }

}