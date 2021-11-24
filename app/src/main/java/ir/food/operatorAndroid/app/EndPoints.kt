package ir.food.operatorAndroid.app

import ir.food.operatorAndroid.BuildConfig

object EndPoints {
    /*TODO : check apis and ports before release*/
    const val IP = "http://happypizza.ir"

    //            "http://192.168.43.93";
    const val HAKWEYE_IP = "http://happypizza.ir"

    //          : "http://192.168.1.145";
    private val APIPort = if (BuildConfig.DEBUG) "3010" else "3010"
    const val ACRA_PATH = "http://turbotaxi.ir:6061/api/v1/crashReport"
    const val PUSH = "http://turbotaxi.ir:6060"
    private val WEBSERVICE_PATH = "$IP:$APIPort/api/operator/v1/"
    private val COMPLAINT_PATH = "$IP:$APIPort/api/operator/v1/complaint"
    private val ACTIVATE_PATH = "$IP:$APIPort/api/operator/v1/activate"
    private val CUSTOMER_PATH = "$IP:$APIPort/api/operator/v1/customer"
    private val ORDER_PATH = "$IP:$APIPort/api/operator/v1/order"

    /******************************** Base Api  */
    val APP_INFO = WEBSERVICE_PATH + "app/info"
    val LOG_IN = WEBSERVICE_PATH + "login"
    val SIGN_UP = WEBSERVICE_PATH + "register"
    val LOGIN_VERIFICATION_CODE = WEBSERVICE_PATH + "login/verificationcode"
    val VERIFICATION_CODE = WEBSERVICE_PATH + "verificationcode"
    val ADD_ORDER = ORDER_PATH
    val CANCEL_ORDER = ORDER_PATH
    val GET_CUSTOMER = CUSTOMER_PATH
    val GET_DELIVERY_LOCATION = "$ORDER_PATH/delivery/"
    val GET_ORDER = ORDER_PATH
    val GET_ORDERS = ORDER_PATH
    val EDIT_ADDRESS = "$ORDER_PATH/editAddress"
    val ADD_COMPLAINT = COMPLAINT_PATH
    val ENTER_QUEUE = ACTIVATE_PATH
}