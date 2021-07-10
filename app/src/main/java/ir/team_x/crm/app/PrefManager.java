package ir.team_x.crm.app;

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

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getAuthorization() {
        return pref.getString(AUTHORIZATION, "");
    }

    public void setAuthorization(String authorization) {
        editor.putString(AUTHORIZATION, authorization);
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

    public void setRefreshToken(String refreshToken) {
        editor.putString(REFRESH_TOKEN, refreshToken);
        editor.commit();
    }

    public void setAppRun(boolean v) {
        editor.putBoolean(KEY_APP_STATUS, v);
        editor.commit();
    }

    public boolean isAppRun() {
        return pref.getBoolean(KEY_APP_STATUS, false);
    }

    public String getComplaint() {
        return pref.getString(COMPLAINT_TYPE, "");
    }

    public void setComplaint(String complaint) {
        editor.putString(COMPLAINT_TYPE, complaint);
        editor.commit();
    }

    public void setKey(String key) {
        editor.putString(KEY_KEY, key);
        editor.commit();
    }

    public String getKey() {
        return pref.getString(KEY_KEY, "");
    }

    public void setUserCode(int userCode) {
        editor.putInt(KEY_USER_CODE, userCode);
        editor.commit();
    }

    public int getUserCode() {
        return pref.getInt(KEY_USER_CODE, 0);
    }

    public void setRepetitionTime(int repetitionTime) {
        editor.putInt(REPETITION_TIME, repetitionTime);
        editor.commit();
    }

    public int getRepetitionTime() {
        return pref.getInt(REPETITION_TIME, 0);
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
        return pref.getInt(PUSH_ID, 5);
    }

    public void setAccountNumber(String accountNumber) {
        editor.putString(ACCOUNT_NUMBER, accountNumber);
        editor.commit();
    }

    public String getAccountNumber() {
        return pref.getString(ACCOUNT_NUMBER, "");
    }

    public void setPassword(String pass) {
        editor.putString(KEY_PASSWORD, pass);
        editor.commit();
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, "0");
    }

    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "0");
    }

    public void isLoggedIn(boolean login) {
        editor.putBoolean(KEY_IS_LOGGED_IN, login);
        editor.commit();
    }

    public boolean getLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setCountRequest(int count) {
        editor.putInt(KEY_COUNT_REQUEST, count);
        editor.commit();
    }

    public int getCountRequest() {
        return pref.getInt(KEY_COUNT_REQUEST, 0);
    }

    public void setActivationRemainingTime(long v) {
        editor.putLong(KEY_ACTIVATION_REMAINING_TIME, v);
        editor.commit();
    }

    public long getActivationRemainingTime() {
        return pref.getLong(KEY_ACTIVATION_REMAINING_TIME, getRepetitionTime());
    }

    public void setCity(String city) {
        Log.d("LOG", "setCity: " + city);
        editor.putString(CITY, city);
        editor.commit();
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
}
