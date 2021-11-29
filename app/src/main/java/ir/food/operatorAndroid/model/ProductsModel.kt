package ir.food.operatorAndroid.model

import org.json.JSONArray
import org.json.JSONObject

data class ProductsModel(
    var id: String,
    var size: JSONArray,
    var name: String,
    var description: String,
    var type: JSONObject,
)