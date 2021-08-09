package ir.food.operatorAndroid.app;


import ir.food.operatorAndroid.BuildConfig;

public class EndPoints {

    /*TODO : check apis and ports before release*/

//    http://192.168.1.145:3000/api/operator/*****
//    http://api.parsian.ir:1881/api/operator/
//    http://192.168.1.145/api/findway/citylatinname/address

    public static final String IP =
            "http://happypizza.ir";
//            "http://192.168.43.93";

    public static final String HAKWEYE_IP =
            "http://happypizza.ir";
//          : "http://192.168.1.145";


    public static final String APIPort = (BuildConfig.DEBUG) ? "3010" : "3010";

    public static final String ACRA_PATH = "http://turbotaxi.ir:6061/api/v1/crashReport";
    public static final String PUSH = "http://turbotaxi.ir:6060";

    public static final String WEBSERVICE_PATH = IP + ":" + APIPort + "/api/operator/v1/";
    public static final String COMPLAINT_PATH = IP + ":" + APIPort + "/api/operator/v1/";
    public static final String ACTIVATE_PATH = IP + ":" + APIPort + "/api/operator/v1/activate";
    public static final String CUSTOMER_PATH = IP + ":" + APIPort + "/api/operator/v1/customer";
    public static final String ORDER_PATH = IP + ":" + APIPort + "/api/operator/v1/order";

    /******************************** Base Api *********************************/

    public static final String APP_INFO = WEBSERVICE_PATH + "app/info";
    public static final String LOG_IN = WEBSERVICE_PATH + "login";
    public static final String SIGN_UP = WEBSERVICE_PATH + "register";
    public static final String LOGIN_VERIFICATION_CODE = WEBSERVICE_PATH + "login/verificationcode";
    public static final String VERIFICATION_CODE = WEBSERVICE_PATH + "verificationcode";
    public static final String ADD_ORDER = ORDER_PATH;
    public static final String CANCEL_ORDER = ORDER_PATH;
    public static final String GET_CUSTOMER = CUSTOMER_PATH;
    public static final String GET_DELIVERY_LOCATION = ORDER_PATH + "/delivery";
    public static final String GET_ORDER = ORDER_PATH;
    public static final String GET_ORDERS = ORDER_PATH;
    public static final String EDIT_ADDRESS = ORDER_PATH+"/editAddress";

    public static final String ENTER_QUEUE = ACTIVATE_PATH;


}
