package ir.food.operatorAndroid.push;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ir.food.operatorAndroid.app.MyApplication;


public class AvaPref {

  // Shared Preferences
  SharedPreferences pref;

  // Editor for Shared preferences
  Editor editor;

  // Context

  // Shared pref mode
  int PRIVATE_MODE = 0;

  // Shared preferences file name
  private static final String PREF_NAME = "AvaPref";

  // All Shared Preferences Keys
  public AvaPref() {
    pref = MyApplication.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public void clearSession() {
    editor.clear();
    editor.commit();
  }

  private static final String DEVICE_ID = "deviceID";
  private static final String USER_ID = "userID";
  private static final String ADDRESS = "address";
  private static final String FIRST_BACKUP_ADDRESS = "firstAddress";
  private static final String SECOND_BACKUP_ADDRESS = "secondAddress";
  private static final String TOKEN = "token";
  private static final String RECALL_COUNT = "recallCount";
  private static final String PROJECT_ID = "projectID";
  private static final String INTERVAL_TIME = "intervalTime";
  private static final String SOCKET_INTERVAL_TIME = "socketIntervalTime";
  private static final String GET_MISSING_PUSH = "getMissingPush";
  private static final String KEY_RECONNECT_SOCKET = "canReconnectSocket";
  private static final String RECONNECT_TIME = "ReconnectTime";
  private static final String KEY_MISSING_SOCKET = "missingSocketStatus";
  private static final String MISSING_API_URL = "MissingApiUrl";
  private static final String API_REQUEST_TIME = "ApiRequestTime";
  private static final String PING_TIME = "PingTime";

  public void setDeviceId(String id) {
    editor.putString(DEVICE_ID, id);
    editor.commit();
  }

  public String getDeviceId() {
    return pref.getString(DEVICE_ID, null);
  }

  public void setProjectId(int id) {
    editor.putInt(PROJECT_ID, id);
    editor.commit();
  }

  public int getProjectId() {
    return pref.getInt(PROJECT_ID, 0);
  }

  /**
   * @param time second
   */
  public void setIntervalTime(int time) {
    editor.putInt(INTERVAL_TIME, time);
    editor.commit();
  }

  public int getIntervalTime() {
    return pref.getInt(INTERVAL_TIME, 60);
  }

  public void setMissingSocketIntervalTime(int time) {
    editor.putInt(SOCKET_INTERVAL_TIME, time);
    editor.commit();
  }

  public int getMissingSocketIntervalTime() {
    return pref.getInt(SOCKET_INTERVAL_TIME, 30);
  }

  public void setMissingApiEnable(boolean v) {
    editor.putBoolean(GET_MISSING_PUSH, v);
    editor.commit();
  }

  public boolean isMissingApiEnable() {
    return pref.getBoolean(GET_MISSING_PUSH, false);
  }

  public void setUserId(String id) {
    editor.putString(USER_ID, id);
    editor.commit();
  }

  public String getUserId() {
    return pref.getString(USER_ID, "0");
  }

  public void increaseIpRow() {
    setIpRow(getIpRow()+1);
  }

  public void setIpRow(int v) {
    editor.putInt(RECALL_COUNT, v);
    editor.commit();
  }

  public int getIpRow() {
    return pref.getInt(RECALL_COUNT, 0);
  }

  public void setToken(String token) {
    editor.putString(TOKEN, token);
    editor.commit();
  }

  public String getToken() {
    return pref.getString(TOKEN, "0");
  }

  public void setAddress(String... address) {
    String str = null;
    if(address==null)return;
    for (String addr : address) {
      if (str == null)
        str = addr;
      else
        str = str + "," + addr;

    }
    editor.putString(ADDRESS, str);
    editor.commit();
  }

  public List<String> getAddress() {
    String[] address = pref.getString(ADDRESS, null).split(",");
    List<String> addreses = new ArrayList<>();
    for (String addr : address)
      addreses.add(addr);
    return addreses;
  }

  public void setFirstBackupAddress(String address) {
    editor.putString(FIRST_BACKUP_ADDRESS, address);
    editor.commit();
  }

  public String getFirstBackupAddress() {
    return pref.getString(FIRST_BACKUP_ADDRESS, null);
  }

  public void setSecondBackupAddress(String address) {
    editor.putString(SECOND_BACKUP_ADDRESS, address);
    editor.commit();
  }

  public String getSecondBackupAddress() {
    return pref.getString(SECOND_BACKUP_ADDRESS, null);
  }

  public boolean canReconnectSocket() {
    return pref.getBoolean(KEY_RECONNECT_SOCKET, false);
  }

  public void setReconnectSocket(boolean v) {
    editor.putBoolean(KEY_RECONNECT_SOCKET, v);
    editor.commit();
  }

  public boolean isMissingSocketEnable() {
    return pref.getBoolean(KEY_MISSING_SOCKET, false);
  }

  public void setMissingSocket(boolean v) {
    editor.putBoolean(KEY_MISSING_SOCKET, v);
    editor.commit();
  }

  public void setLastReconnectTime() {
    editor.putLong(RECONNECT_TIME, Calendar.getInstance().getTimeInMillis());
    editor.commit();
  }

  public long getLastReconnectTime() {
    return pref.getLong(RECONNECT_TIME, 0);
  }

  public void setPongReceived() {
    editor.putLong(PING_TIME, Calendar.getInstance().getTimeInMillis());
    editor.commit();
  }

  public long getLastPongReceiveAt() {
    return pref.getLong(PING_TIME, 0);
  }

  public void setMissingApiUrl(String i) {
    editor.putString(MISSING_API_URL, i);
    editor.commit();
  }

  public String getMissingApiUrl() {
    return pref.getString(MISSING_API_URL, null);
  }

  public void setMissingApiRequestTime(long i) {
    editor.putLong(API_REQUEST_TIME, i);
    editor.commit();
  }

  public long getMissingApiRequestTime() {
    return pref.getLong(API_REQUEST_TIME, 0);
  }

}

