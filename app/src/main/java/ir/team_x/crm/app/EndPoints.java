package ir.team_x.crm.app;


import ir.team_x.crm.BuildConfig;

public class EndPoints {

    /*TODO : check apis and ports before release*/

//    http://192.168.1.145:3000/api/operator/*****
//    http://api.parsian.ir:1881/api/operator/
//    http://192.168.1.145/api/findway/citylatinname/address

    public static final String IP = (BuildConfig.DEBUG)
            ? "http://192.168.1.127"
//            ? "http://turbotaxi.ir"
//            : "http://turbotaxi.ir";
            : "http://192.168.1.127";

    public static final String HAKWEYE_IP = (BuildConfig.DEBUG)
//          ? "http://192.168.1.127"
            ? "http://turbotaxi.ir"
            : "http://turbotaxi.ir";
//          : "http://192.168.1.127";

    public static final String PUSH_ADDRESS = (BuildConfig.DEBUG)
            ? "http://turbotaxi.ir:6060"
//          ? "http://172.16.2.212:6060"
//          : "http://172.16.2.212:6060";
            : "http://turbotaxi.ir:6060";

    public static final String APIPort = (BuildConfig.DEBUG) ? "3000" : "1881";

    public static final String HAWKEYE_APIPort = (BuildConfig.DEBUG) ? "3009" : "1890";

    public static final String ACRA_PATH = "http://turbotaxi.ir:6061/api/v1/crashReport";

    public static final String WEBSERVICE_PATH = IP + ":" + APIPort + "/api/user/v1/";

    public static final String HAWKEYE_PATH = HAKWEYE_IP + ":" + HAWKEYE_APIPort + "/api/user/v1/";
    public static final String HAWKEYE_LOGIN_PATH = HAKWEYE_IP + ":" + HAWKEYE_APIPort + "/api/user/v1/login/phone/";

    /******************************** Base Api *********************************/

    public static final String LOG_IN = HAWKEYE_PATH + "login";
    public static final String GET_APP_INFO = WEBSERVICE_PATH + "getAppInfo";
    public static final String GET_MESSAGES = WEBSERVICE_PATH + "getMessages";

    /******************************** refresh token Api *********************************/

    public static final String REFRESH_TOKEN = HAWKEYE_PATH + "token";
    public static final String LOGIN = HAWKEYE_PATH + "login";
    public static final String VERIFICATION = HAWKEYE_LOGIN_PATH + "verification";
    public static final String CHECK = HAWKEYE_LOGIN_PATH + "check";

    /******************************** product api *********************************/

    public static final String GET_PRODUCT = WEBSERVICE_PATH + "product/";


}
