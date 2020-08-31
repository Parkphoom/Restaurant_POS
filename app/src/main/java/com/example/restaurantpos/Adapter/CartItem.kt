package com.example.restaurantpos.Adapter

class CartItem(foodName: String?, foodPrice: String?, foodCount: String?) {
    private var foodName: String? = foodName
    private var foodPrice: String? = foodPrice
    private var foodCount: String? = foodCount

    fun getFoodname(): String? {
        return foodName
    }
    fun getFoodprice(): String? {
        return foodPrice
    }
    fun getFoodcount(): String? {
        return foodCount
    }
}
