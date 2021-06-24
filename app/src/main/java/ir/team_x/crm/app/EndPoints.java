package ir.team_x.crm.app;


import ir.team_x.crm.BuildConfig;

public class EndPoints {

    /*TODO : check apis and ports before release*/

//    http://172.16.2.201:1881/api/operator/*****
//    http://api.parsian.ir:1881/api/operator/
//    http://172.16.2.210:1885/api/findway/citylatinname/address

    public static final String IP = (BuildConfig.DEBUG)
//          ? "http://192.168.1.127"
            ? "http://turbotaxi.ir"
            : "http://turbotaxi.ir";
//          : "http://192.168.1.127";

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

    public static final String APIPort = (BuildConfig.DEBUG) ? "3009" : "1881";
    public static final String PIC_APIPort = (BuildConfig.DEBUG) ? "1880" : "1880";
    public static final String TRIP_APIPort = (BuildConfig.DEBUG) ? "1881" : "1881";
    public static final String CALL_VOICE_APIPort = (BuildConfig.DEBUG) ? "1884" : "1884";
    public static final String HAWKEYE_APIPort = (BuildConfig.DEBUG) ? "3009" : "1890";

    public static final String ACRA_PATH = "http://turbotaxi.ir:6061/api/v1/crashReport";

    public static final String WEBSERVICE_PATH = IP + ":" + APIPort + "/api/operator/v3/";
    public static final String SHIFT_WEBSERVICE_PATH = IP + ":" + TRIP_APIPort + "/api/operator/v3/shift/";

    public static final String HAWKEYE_PATH = HAKWEYE_IP + ":" + HAWKEYE_APIPort + "/api/user/v1/";
    public static final String HAWKEYE_LOGIN_PATH = HAKWEYE_IP + ":" + HAWKEYE_APIPort + "/api/user/v1/login/phone/";

    /******************************** Base Api *********************************/

    public static final String LOG_IN = HAWKEYE_PATH + "login";
    public static final String GET_APP_INFO = WEBSERVICE_PATH + "getAppInfo";
    public static final String GET_MESSAGES = WEBSERVICE_PATH + "getMessages";
    public static final String GET_NEWS = WEBSERVICE_PATH + "getNews";
    public static final String SEND_MESSAGES = WEBSERVICE_PATH + "sendMessages";
    public static final String SET_NEWS_SEEN = WEBSERVICE_PATH + "setNewsSeen";
    public static final String GET_SHIFTS = WEBSERVICE_PATH + "shift";
    public static final String GET_SHIFT_OPERATOR = SHIFT_WEBSERVICE_PATH + "operators";
    public static final String GET_SHIFT_REPLACEMENT_REQUESTS = SHIFT_WEBSERVICE_PATH + "getReplacementRequests";
    public static final String SHIFT_REPLACEMENT_REQUEST = SHIFT_WEBSERVICE_PATH + "replacementRequest";
    public static final String CANCEL_REPLACEMENT_REQUEST = SHIFT_WEBSERVICE_PATH + "cancelReplacementRequest";
    public static final String ANSWER_SHIFT_REPLACEMENT_REQUEST = SHIFT_WEBSERVICE_PATH + "answerReplacementRequest";

    /******************************** Account Api *********************************/

    public static final String BALANCE = WEBSERVICE_PATH + "balance";
    public static final String UPDATE_PROFILE = WEBSERVICE_PATH + "updateProfile";
    public static final String PAYMENT = WEBSERVICE_PATH + "payment";

    /******************************** refresh token Api *********************************/

    public static final String REFRESH_TOKEN = HAWKEYE_PATH + "token";
    public static final String LOGIN = HAWKEYE_PATH + "login";
    public static final String VERIFICATION = HAWKEYE_LOGIN_PATH + "verification";
    public static final String CHECK = HAWKEYE_LOGIN_PATH + "check";

}
