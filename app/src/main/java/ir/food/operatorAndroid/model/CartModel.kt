package ir.food.operatorAndroid.model

data class CartModel(
    var discount: Boolean,
    var count: Int,
    var price: String,
    var name: String
)