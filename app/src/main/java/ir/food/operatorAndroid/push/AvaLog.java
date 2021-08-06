package ir.food.operatorAndroid.push;

import android.util.Log;

public class AvaLog {
  public static void e(String message, Exception e){
    Log.e(Keys.TAG, message,e);
    AvaReporter.MessageLog(message);
  }
  public static void e(String message){
    Log.e(Keys.TAG, message);
    AvaReporter.MessageLog(message);
  }
  public static void i(String message){
    Log.i(Keys.TAG, message);
    AvaReporter.MessageLog(message);
  }
  public static void w(String message){
    Log.w(Keys.TAG, message);
    AvaReporter.MessageLog(message);
  }
}
