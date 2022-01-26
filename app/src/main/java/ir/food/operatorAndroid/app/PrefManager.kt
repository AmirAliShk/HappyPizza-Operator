package ir.food.operatorAndroid.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import ir.food.operatorAndroid.app.PrefManager
import ir.food.operatorAndroid.app.MyApplication

class PrefManager(  // Context
    var _context: Context
) {
    // Shared Preferences
    var pref: SharedPreferences

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor

    // Shared pref mode
    var PRIVATE_MODE = 0
    var sipNumber: String?
        get() = pref.getString(SIP_NUMBER, "0")
        set(sipNumber) {
            editor.putString(SIP_NUMBER, sipNumber)
            editor.commit()
        }
    var sipServer: String?
        get() = pref.getString(SIP_SERVER, "")
        set(sipServer) {
            editor.putString(SIP_SERVER, sipServer)
            editor.commit()
        }
    var sipPassword: String?
        get() = pref.getString(SIP_PASSWORD, "")
        set(sipPassword) {
            editor.putString(SIP_PASSWORD, sipPassword)
            editor.commit()
        }
    var authorization: String?
        get() = pref.getString(AUTHORIZATION, "")
        set(authorization) {
            editor.putString(AUTHORIZATION, authorization)
            editor.commit()
        }
    var lastNotification: String?
        get() = pref.getString(LAST_NOTIFICATION, "")
        set(authorization) {
            editor.putString(LAST_NOTIFICATION, authorization)
            editor.commit()
        }
    var isCallIncoming: Boolean
        get() = pref.getBoolean(INCOMINGCALL, false)
        set(b) {
            editor.putBoolean(INCOMINGCALL, b)
            editor.commit()
        }
    var connectedCall: Boolean
        get() = pref.getBoolean(GET_CONNECTED_CALL, false)
        set(b) {
            editor.putBoolean(GET_CONNECTED_CALL, b)
            editor.commit()
        }
    var queueStatus: Boolean
        get() = pref.getBoolean(QUEUE_STATUS, false)
        set(b) {
            editor.putBoolean(QUEUE_STATUS, b)
            editor.commit()
        }
    var idToken: String?
        get() = pref.getString(ID_TOKEN, "")
        set(idToken) {
            editor.putString(ID_TOKEN, idToken)
            editor.commit()
        }
    val refreshToken: String?
        get() = pref.getString(REFRESH_TOKEN, "")
    var userCode: String?
        get() = pref.getString(KEY_USER_CODE, "0")
        set(v) {
            editor.putString(KEY_USER_CODE, v)
            editor.commit()
        }
    var pushToken: String?
        get() = pref.getString(PUSH_TOKEN, "")
        set(v) {
            editor.putString(PUSH_TOKEN, v)
            editor.commit()
        }
    var pushId: Int
        get() = pref.getInt(PUSH_ID, 12)
        set(v) {
            editor.putInt(PUSH_ID, v)
            editor.commit()
        }
    var userName: String?
        get() = pref.getString(KEY_USER_NAME, "0")
        set(v) {
            editor.putString(KEY_USER_NAME, v)
            editor.commit()
        }
    val city: String?
        get() = pref.getString(CITY, "")

    fun setProducts(products: String) {
        Log.d("LOG", "setProducts: $products")
        editor.putString(PRODUCTS, products)
        editor.commit()
    }

    val products: String?
        get() = pref.getString(PRODUCTS, "")
    var voipId: String?
        get() = pref.getString(VOIP_ID, "")
        set(voipId) {
            editor.putString(VOIP_ID, voipId)
            editor.commit()
        }
    var queue: String?
        get() = pref.getString(QUEUE, "")
        set(queue) {
            editor.putString(QUEUE, queue)
            editor.commit()
        }
    var productsList: String?
        get() = pref.getString(PRODUCTS_LIST, "")
        set(productsList) {
            editor.putString(PRODUCTS_LIST, productsList)
            editor.commit()
        }
    var productsTypeList: String?
        get() = pref.getString(PRODUCTS_TYPE_LIST, "")
        set(productsTypeList) {
            editor.putString(PRODUCTS_TYPE_LIST, productsTypeList)
            editor.commit()
        }
    var isAppRun: Boolean
        get() = pref.getBoolean(KEY_APP_STATUS, false)
        set(v) {
            editor.putBoolean(KEY_APP_STATUS, v)
            editor.commit()
        }
    var repetitionTime: Int
        get() = this.pref.getInt(REPETITION_TIME, 0)
        set(repetitionTime) {
            editor.putInt(REPETITION_TIME, repetitionTime)
            editor.commit()
        }
    var activationRemainingTime: Long
        get() = this.pref.getLong(
            KEY_ACTIVATION_REMAINING_TIME,
            repetitionTime.toLong()
        )
        set(v) {
            editor.putLong(KEY_ACTIVATION_REMAINING_TIME, v)
            editor.commit()
        }

    fun cleanPrefManger() {
        pref.edit().clear().apply()
    }

    companion object {
        // Shared preferences file name
        private val PREF_NAME = MyApplication.context.applicationInfo.name
        private const val KEY_KEY = "key"
        private const val KEY_USER_CODE = "userCode"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_PASSWORD = "password"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_COUNT_REQUEST = "countRquest"
        private const val PUSH_TOKEN = "pushToken"
        private const val PUSH_ID = "pushID"
        private const val ACCOUNT_NUMBER = "accountNumber"
        private const val KEY_APP_STATUS = "AppStatus"
        private const val COMPLAINT_TYPE = "ComplaintType"
        private const val AUTHORIZATION = "Authorization"
        private const val ID_TOKEN = "id_token"
        private const val REFRESH_TOKEN = "refreshToken"
        private const val REPETITION_TIME = "repetitionTime"
        private const val KEY_ACTIVATION_REMAINING_TIME = "activationRemainingTime"
        private const val CITY = "city"
        private const val PRODUCTS = "products"
        private const val INCOMINGCALL = "inCall"
        private const val GET_CONNECTED_CALL = "connectedCall"
        private const val QUEUE_STATUS = "queueStatus"
        private const val LAST_CALL_NUMBER = "lastCallNumber"
        private const val LAST_NOTIFICATION = "lastNotif"
        private const val SIP_NUMBER = "sipNumber"
        private const val SIP_PASSWORD = "sipPassword"
        private const val SIP_SERVER = "sipServer"
        private const val VOIP_ID = "voipId"
        private const val QUEUE = "queue"
        private const val PRODUCTS_LIST = "product_list"
        private const val PRODUCTS_TYPE_LIST = "product_type_list"
    }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
}