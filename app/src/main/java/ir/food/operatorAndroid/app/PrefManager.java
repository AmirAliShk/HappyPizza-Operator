package ir.food.operatorAndroid.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class PrefManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = MyApplication.context.getApplicationInfo().name;
    private static final String KEY_KEY = "key";
    private static final String KEY_USER_CODE = "userCode";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_COUNT_REQUEST = "countRquest";
    private static final String PUSH_TOKEN = "pushToken";
    private static final String PUSH_ID = "pushID";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String KEY_APP_STATUS = "AppStatus";
    private static final String COMPLAINT_TYPE = "ComplaintType";
    private static final String AUTHORIZATION = "Authorization";
    private static final String ID_TOKEN = "id_token";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String REPETITION_TIME = "repetitionTime";
    private static final String KEY_ACTIVATION_REMAINING_TIME = "activationRemainingTime";
    private static final String CITY = "city";
    private static final String PRODUCTS = "products";
    private static final String INCOMINGCALL = "inCall";
    private static final String GET_CONNECTED_CALL = "connectedCall";
    private static final String QUEUE_STATUS = "queueStatus";
    private static final String LAST_CALL = "lastCall";
    private static final String LAST_NOTIFICATION = "lastNotif";
    private static final String SIP_NUMBER = "sipNumber";
    private static final String SIP_PASSWORD = "sipPassword";
    private static final String SIP_SERVER = "sipServer";
    private static final String VOIP_ID = "voipId";
    private static final String QUEUE = "queue";
    private static final String PRODUCTS_LIST = "product_list";
    private static final String PRODUCTS_TYPE_LIST = "product_type_list";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setSipNumber(String sipNumber) {
        editor.putString(SIP_NUMBER, sipNumber);
        editor.commit();
    }

    public String getSipNumber() {
        return pref.getString(SIP_NUMBER, "0");
    }

    public void setSipServer(String sipServer) {
        editor.putString(SIP_SERVER, sipServer);
        editor.commit();
    }

    public String getSipServer() {
        return pref.getString(SIP_SERVER, "");
    }

    public void setSipPassword(String sipPassword) {
        editor.putString(SIP_PASSWORD, sipPassword);
        editor.commit();
    }

    public String getSipPassword() {
        return pref.getString(SIP_PASSWORD, "");
    }

    public String getAuthorization() {
        return pref.getString(AUTHORIZATION, "");
    }

    public void setAuthorization(String authorization) {
        editor.putString(AUTHORIZATION, authorization);
        editor.commit();
    }

    public String getLastNotification() {
        return pref.getString(LAST_NOTIFICATION, "");
    }

    public void setLastNotification(String authorization) {
        editor.putString(LAST_NOTIFICATION, authorization);
        editor.commit();
    }

    public boolean isCallIncoming() {
        return pref.getBoolean(INCOMINGCALL, false);
    }

    public void setCallIncoming(boolean b) {
        editor.putBoolean(INCOMINGCALL, b);
        editor.commit();
    }

    public boolean getConnectedCall() {
        return pref.getBoolean(GET_CONNECTED_CALL, false);
    }

    public void setConnectedCall(boolean b) {
        editor.putBoolean(GET_CONNECTED_CALL, b);
        editor.commit();
    }

    public boolean getQueueStatus() {
        return pref.getBoolean(QUEUE_STATUS, false);
    }

    public void setQueueStatus(boolean b) {
        editor.putBoolean(QUEUE_STATUS, b);
        editor.commit();
    }

    public String getLastCall() {
        return pref.getString(LAST_CALL, "");
    }

    public void setLastCall(String idToken) {
        editor.putString(LAST_CALL, idToken);
        editor.commit();
    }

    public String getIdToken() {
        return pref.getString(ID_TOKEN, "");
    }

    public void setIdToken(String idToken) {
        editor.putString(ID_TOKEN, idToken);
        editor.commit();
    }

    public String getRefreshToken() {
        return pref.getString(REFRESH_TOKEN, "");
    }

    public String getUserCode() {
        return pref.getString(KEY_USER_CODE, "0");
    }

    public void setUserCode(String v) {
        editor.putString(KEY_USER_CODE, v);
        editor.commit();
    }

    public void setPushToken(String v) {
        editor.putString(PUSH_TOKEN, v);
        editor.commit();
    }

    public String getPushToken() {
        return pref.getString(PUSH_TOKEN, "");
    }

    public void setPushId(int v) {
        editor.putInt(PUSH_ID, v);
        editor.commit();
    }

    public int getPushId() {
        return pref.getInt(PUSH_ID, 12);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "0");
    }

    public String getCity() {
        return pref.getString(CITY, "");
    }

    public void setProducts(String products) {
        Log.d("LOG", "setProducts: " + products);
        editor.putString(PRODUCTS, products);
        editor.commit();
    }

    public String getProducts() {
        return pref.getString(PRODUCTS, "");
    }

    public String getVoipId() {
        return pref.getString(VOIP_ID, "");
    }

    public void setVoipId(String voipId) {
        editor.putString(VOIP_ID, voipId);
        editor.commit();
    }

    public String getQueue() {
        return pref.getString(QUEUE, "");
    }

    public void setQueue(String queue) {
        editor.putString(QUEUE, queue);
        editor.commit();
    }

    public String getProductsList() {
        return pref.getString(PRODUCTS_LIST, "");
    }

    public void setProductsList(String productsList) {
        editor.putString(PRODUCTS_LIST, productsList);
        editor.commit();
    }

    public String getProductsTypeList() {
        return pref.getString(PRODUCTS_TYPE_LIST, "");
    }

    public void setProductsTypeList(String productsTypeList) {
        editor.putString(PRODUCTS_TYPE_LIST, productsTypeList);
        editor.commit();
    }

    public void setAppRun(boolean v) {
        editor.putBoolean(KEY_APP_STATUS, v);
        editor.commit();
    }

    public boolean isAppRun() {
        return pref.getBoolean(KEY_APP_STATUS, false);
    }

    public void cleanPrefManger() {
        pref.edit().clear().apply();
    }
}
