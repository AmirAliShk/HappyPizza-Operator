package ir.food.operatorAndroid.model

import org.json.JSONArray
import org.json.JSONObject

data class ProductsModel(
    var id: String,
    var size: String, // its medium, large, small
    var name: String,
    val nameWithSupply: String,
    var description: String,
    var type: JSONObject,
    var supply: Int, // the primary count of product
    var quantity:Int = 1, // the count that customer selected
    var price:String = "0",
    var discount:String = "0",
)