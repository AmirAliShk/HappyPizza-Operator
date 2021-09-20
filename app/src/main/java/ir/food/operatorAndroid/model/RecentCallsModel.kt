package ir.food.operatorAndroid.model

data class RecentCallsModel(
    var txtDate: String,
    var txtTime: String,
    var voipId: String,
    var phone: String?,
    var destinationOperator: String?,
    var txtTimeRemaining: Int = 0
)