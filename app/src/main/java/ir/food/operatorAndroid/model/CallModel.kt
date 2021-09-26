package ir.food.operatorAndroid.model

data class CallModel(
    var type: String,
    var exten: Int = 0,
    var participant: String,
    var queue: String,
    var voipId: String
)