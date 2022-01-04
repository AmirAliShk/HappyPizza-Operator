package ir.food.operatorAndroid.app

object EndPoints {
    /*TODO : check apis and ports before release*/
    const val IP = "http://happypizza.ir"

    const val HAKWEYE_IP = "http://happypizza.ir"

    private val APIPort = "3010"
    const val ACRA_PATH = "http://turbotaxi.ir:6061/api/v1/crashReport"
    const val PUSH = "http://turbotaxi.ir:6060"
    private val WEBSERVICE_PATH = "$IP:$APIPort/api/operator/v1/"
    private val COMPLAINT_PATH = "$IP:$APIPort/api/operator/v1/complaint"
    private val ACTIVATE_PATH = "$IP:$APIPort/api/operator/v1/activate"
    private val CUSTOMER_PATH = "$IP:$APIPort/api/operator/v1/customer"
    private val ORDER_PATH = "$IP:$APIPort/api/operator/v1/order"

    /******************************** Base Api  *********************************/

    val APP_INFO = WEBSERVICE_PATH + "app/info"
    val LOG_IN = WEBSERVICE_PATH + "login"
    val SIGN_UP = WEBSERVICE_PATH + "register"
    val LOGIN_VERIFICATION_CODE = WEBSERVICE_PATH + "login/verificationcode"
    val VERIFICATION_CODE = WEBSERVICE_PATH + "verificationcode"

    /******************************** register order Api  *********************************/

    val ENTER_QUEUE = "${WEBSERVICE_PATH}queue/enter"
    val EXIT_QUEUE = "${WEBSERVICE_PATH}queue/exit"
    val GET_CUSTOMER = CUSTOMER_PATH
    val GET_PRODUCTS = "$ORDER_PATH/product"
    val ADD_ORDER = ORDER_PATH
    val SEND_MENU = "$ORDER_PATH/menu"

    /******************************** support order Api  *********************************/

    val GET_ORDERS_LIST = ORDER_PATH
    val GET_ORDER_DETAILS = ORDER_PATH
    val EDIT_ADDRESS = "$ORDER_PATH/editAddress"
    val ADD_COMPLAINT = COMPLAINT_PATH
    val CANCEL_ORDER = ORDER_PATH
    val GET_DELIVERY_LOCATION = "$ORDER_PATH/delivery/"
}