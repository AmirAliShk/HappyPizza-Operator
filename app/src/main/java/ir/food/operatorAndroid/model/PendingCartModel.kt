package ir.food.operatorAndroid.model

data class PendingCartModel(
    val id: String,
    val name: String,
    val nameWithSupply: String,
    var price: String,
    val size: String,
    var quantity: Int
)