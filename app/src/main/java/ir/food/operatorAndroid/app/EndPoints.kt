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
    const val LOGIN_VERIFICATION_CODE = WEBSERVICE_PATH + "login/verificationcode"
    const val VERIFICATION_CODE = WEBSERVICE_PATH + "verificationcode"

    /******************************** register order Api  *********************************/

    const val ENTER_QUEUE = "${WEBSERVICE_PATH}queue/enter"
    const val EXIT_QUEUE = "${WEBSERVICE_PATH}queue/exit"
    const val GET_CUSTOMER = CUSTOMER_PATH
    const val GET_PRODUCTS = "$ORDER_PATH/product"
    const val ADD_ORDER = ORDER_PATH
    const val EDIT_ORDER = "$ORDER_PATH/editOrder"
    const val SEND_MENU = "$ORDER_PATH/menu"
    const val CALCULATE_BILL = "${ORDER_PATH}/bill"

    /******************************** support order Api  *********************************/

    const val GET_ORDERS_LIST = SUPPORT
    const val GET_ORDER_DETAILS = "$ORDER_PATH/v1/"
    const val EDIT_ADDRESS = "$ORDER_PATH/editAddress"
    const val ADD_COMPLAINT = COMPLAINT_PATH
    const val CANCEL_ORDER = ORDER_PATH
    const val GET_DELIVERY_LOCATION = "$ORDER_PATH/delivery/"
    const val ARCHIVE_ADDRESS = "$ORDER_PATH/cust/archive/address"

}