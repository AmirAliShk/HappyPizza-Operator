package ir.food.operatorAndroid.model

data class OrderModel(
    var id:String,
    var statusName: String,
    var statusCode: Int,
    var date: String,
    var name: String,
    var mobile: String,
    var address: String,
    var paid: Boolean
)