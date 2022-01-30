package ir.food.operatorAndroid.app

object EndPoints {
    /*TODO : check apis and ports before release*/
    const val IP = "http://happypizza.ir"

    const val HAKWEYE_IP = "http://happypizza.ir"

    private const val APIPort = "3010"
    const val ACRA_PATH = "http://turbotaxi.ir:6061/api/v1/crashReport"
    const val PUSH = "http://turbotaxi.ir:6060"
    private const val WEBSERVICE_PATH = "$IP:$APIPort/api/operator/v1/"
    private const val COMPLAINT_PATH = "$IP:$APIPort/api/operator/v1/complaint"
    private val ACTIVATE_PATH = "$IP:$APIPort/api/operator/v1/activate"
    private const val CUSTOMER_PATH = "$IP:$APIPort/api/operator/v1/customer"
    private const val ORDER_PATH = "$IP:$APIPort/api/operator/v1/order"
    private const val SUPPORT = "$IP:$APIPort/api/operator/v1/order/support"

    /******************************** Base Api  *********************************/

    const val APP_INFO = WEBSERVICE_PATH + "app/info"
    const val LOG_IN = WEBSERVICE_PATH + "login"
    const val SIGN_UP = WEBSERVICE_PATH + "register"
    val LOGIN_VERIFICATION_CODE = WEBSERVICE_PATH + "login/verificationcode"
    const val VERIFICATION_CODE = WEBSERVICE_PATH + "verificationcode"

    /******************************** register order Api  *********************************/

    val ENTER_QUEUE = "${WEBSERVICE_PATH}queue/enter"
    val EXIT_QUEUE = "${WEBSERVICE_PATH}queue/exit"
    val GET_CUSTOMER = CUSTOMER_PATH
    val GET_PRODUCTS = "$ORDER_PATH/product"
    val ADD_ORDER = ORDER_PATH
    val EDIT_ORDER = "$ORDER_PATH/editOrder"
    val SEND_MENU = "$ORDER_PATH/menu"
    val CALCULATE_BILL = "${ORDER_PATH}/bill"

    /******************************** support order Api  *********************************/

    val GET_ORDERS_LIST = SUPPORT
    val GET_ORDER_DETAILS = "$ORDER_PATH/v1/"
    val EDIT_ADDRESS = "$ORDER_PATH/editAddress"
    val ADD_COMPLAINT = COMPLAINT_PATH
    val CANCEL_ORDER = ORDER_PATH
    val GET_DELIVERY_LOCATION = "$ORDER_PATH/delivery/"
    val ARCHIVE_ADDRESS = "$ORDER_PATH/cust/archive/address"

}