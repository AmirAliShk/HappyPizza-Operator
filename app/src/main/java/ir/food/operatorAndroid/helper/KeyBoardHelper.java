package ir.food.operatorAndroid.helper;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ir.food.operatorAndroid.app.MyApplication;

/**
 * Created by AmirReza on 20/01/2017.
 */

public class KeyBoardHelper {
    public static void hideKeyboard() {
        View view = MyApplication.currentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) MyApplication.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Context context){
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
