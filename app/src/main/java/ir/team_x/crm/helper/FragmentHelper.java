package ir.team_x.crm.helper;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import androidx.annotation.AnimRes;
import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentActivity;


/***********************************************
 * Created by AmirReza Erfanian at 23 jun 2019 *
 *                                             *
 ***********************************************/

public class FragmentHelper {

    private static final String TAG = FragmentHelper.class.getSimpleName();
    private static FragmentHelper instance;
    private androidx.fragment.app.Fragment fragmentV4 = null;
    private Fragment fragment = null;
    private String flag = null;
    private static Activity activity = null;
    private boolean addToBackStack = true;
    private FragmentManager fragmentManager = null;
    private Bundle bundle = null;
    private int enterAnim = 0;
    private int exitAnim = 0;
    private int popEnterAnim = 0;
    private int popExitAnim = 0;
    private int statusBarColor = -1;
    private int navigationBarColor = -1;

    private androidx.fragment.app.FragmentManager fragmentManagerV4 = null;
    private @IdRes
    int frame = android.R.id.content;

    /**
     * use android.app.fragment library
     * flag parameter and fragmentManger is create in method
     *
     * @param fragment
     * @return instance of FragmentHelper Class
     */
    public static FragmentHelper toFragment(Activity activity, Fragment fragment) {
        instance = new FragmentHelper();
        instance.activity = activity;
        instance.flag = fragment.getClass().getSimpleName();
        instance.fragment = fragment;
        instance.fragmentManager = instance.activity.getFragmentManager();
        return instance;
    }

    /**
     * use android.support.v4.app.fragment library
     * flag parameter and fragmentManger is create in method
     *
     * @param fragmentV4
     * @return instance of FragmentHelper Class
     */
    public static FragmentHelper toFragment(Activity activity, androidx.fragment.app.Fragment fragmentV4) {
        instance = new FragmentHelper();
        instance.activity = activity;
        instance.flag = fragmentV4.getClass().getSimpleName();
        instance.fragmentV4 = fragmentV4;
        instance.fragmentManagerV4 = ((FragmentActivity) instance.activity).getSupportFragmentManager();
        return instance;
    }

    private String getFlag() {
        return instance.flag;
    }

    private int getFrame() {
        return instance.frame;
    }

    private boolean isAddToBackStack() {
        return instance.addToBackStack;
    }

    /**
     * if you are like to use animation for input fragment and output fragment
     * you must set TRUE this parameter
     *
     * @return instance of FragmentHelper
     */
    public FragmentHelper setUseAnimation(@AnimRes int enterAnim, @AnimRes int exitAnim, @AnimRes int popEnterAnim, @AnimRes int popExitAnim) {
        instance.enterAnim = enterAnim;
        instance.exitAnim = exitAnim;
        instance.popEnterAnim = popEnterAnim;
        instance.popExitAnim = popExitAnim;
        return instance;
    }

    public FragmentHelper setUseAnimation(@AnimRes int enterAnim, @AnimRes int exitAnim) {
        instance.enterAnim = enterAnim;
        instance.exitAnim = exitAnim;
        return instance;
    }

    /**
     * if you set TRUE this parameter
     * fragment don't add to back stack and
     * back press button don't work
     *
     * @param addToBackStack default value is true
     * @return
     */
    public FragmentHelper setAddToBackStack(boolean addToBackStack) {
        instance.addToBackStack = addToBackStack;
        return instance;
    }

    public FragmentHelper setArguments(Bundle bundle) {
        if (instance.fragment != null) {
            instance.fragment.setArguments(bundle);
        }

        if (instance.fragmentV4 != null) {
            instance.fragmentV4.setArguments(bundle);
        }
        return instance;
    }

    /**
     * if you want use another container ,you must create frameLayout in your activity view and
     * send id of view to this function
     *
     * @param frame use default Container android
     * @return
     */
    public FragmentHelper setFrame(int frame) {
        instance.frame = frame;
        return instance;
    }

    /**
     * if you use this function fragment only add to stack and don't remove last visible fragment
     */
    public void add() {
        try {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (instance.fragment != null) {
                    FragmentTransaction fragmentTransaction = instance.fragmentManager.beginTransaction();
                    if (isAddToBackStack()) {
                        fragmentTransaction.addToBackStack(null);
                    }
                    if (instance.fragment.isAdded()) {
                        return;
                    }
                    fragmentTransaction.add(instance.frame, instance.fragment, instance.flag);
                    fragmentTransaction.commitAllowingStateLoss();
                } else if (instance.fragmentV4 != null) {
                    androidx.fragment.app.FragmentTransaction fragmentTransaction = instance.fragmentManagerV4.beginTransaction();
                    if (isAddToBackStack()) {
                        fragmentTransaction.addToBackStack(null);
                    }
                    fragmentTransaction.setCustomAnimations(instance.enterAnim, instance.exitAnim, instance.popEnterAnim, instance.popExitAnim);
                    if (instance.fragmentV4.isAdded()) {
                        return;
                    }
                    fragmentTransaction.add(instance.frame, instance.fragmentV4, instance.flag);
                    fragmentTransaction.commitAllowingStateLoss();
                } else {
                    Log.e(TAG, "can't add " + flag + " to " + frame);
                }

            }, 100);
            setWindowStyle();
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "can't add " + flag + " to " + frame);
        }

    }

    /**
     * remove last visible fragment and replace new fragment with it
     */
    public void replace() {
        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (instance.fragment != null) {
                        FragmentTransaction fragmentTransaction = instance.fragmentManager.beginTransaction();
                        if (isAddToBackStack()) {
                            fragmentTransaction.addToBackStack(null);
                        }
                        fragmentTransaction.replace(instance.frame, instance.fragment, instance.flag);
                        fragmentTransaction.commitAllowingStateLoss();
                    } else if (instance.fragmentV4 != null) {
                        androidx.fragment.app.FragmentTransaction fragmentTransaction = instance.fragmentManagerV4.beginTransaction();
                        if (isAddToBackStack()) {
                            fragmentTransaction.addToBackStack(null);
                        }
                        fragmentTransaction.setCustomAnimations(instance.enterAnim, instance.exitAnim, instance.popEnterAnim, instance.popExitAnim);
                        fragmentTransaction.replace(instance.frame, instance.fragmentV4, instance.flag);
                        fragmentTransaction.commitAllowingStateLoss();
                    } else {
                        Log.e(TAG, "can't replace " + flag + " to " + frame);
                    }
                }
            }, 100);
            setWindowStyle();
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "can't replace " + flag + " to " + frame);
        }

    }

    public static FragmentHelper taskFragment(Activity activity, String tag) {
        instance = new FragmentHelper();
        instance.activity = activity;
        instance.fragmentManagerV4 = ((FragmentActivity) instance.activity).getSupportFragmentManager();
        instance.fragmentManager = instance.activity.getFragmentManager();
        instance.fragment = instance.fragmentManager.findFragmentByTag(tag);
        instance.fragmentV4 = instance.fragmentManagerV4.findFragmentByTag(tag);
        return instance;
    }

    public boolean isVisible() {
        if (instance.fragment != null) {
            if (instance.fragment.isVisible()) {
                return true;
            }
        }
        if (instance.fragmentV4 != null) {
            if (instance.fragmentV4.isVisible()) {
                return true;
            }
        }
        return false;
    }

    public void remove() {
        try {
            if (instance.fragment != null) {
                Log.i(TAG, "remove: is null");
                FragmentTransaction transaction = instance.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(instance.enterAnim, instance.exitAnim, instance.popEnterAnim, instance.popExitAnim);
                transaction.remove(instance.fragment).commit();
                instance.fragmentManager.popBackStack();
            }

            if (instance.fragmentV4 != null) {
                Log.i(TAG, "remove: v4 is null");
                androidx.fragment.app.FragmentTransaction transactionV4 = instance.fragmentManagerV4.beginTransaction();
                transactionV4.setCustomAnimations(instance.enterAnim, instance.exitAnim, instance.popEnterAnim, instance.popExitAnim);
                transactionV4.remove(fragmentV4).commit();
                instance.fragmentManagerV4.popBackStack();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public FragmentHelper setNavigationBarColor(@ColorInt int color) {
        instance.navigationBarColor = color;
        return instance;
    }

    public FragmentHelper setStatusBarColor(@ColorInt int color) {
        instance.statusBarColor = color;
        return instance;
    }

    private void setWindowStyle() {
        try {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        if (darkMode)
//          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                if (statusBarColor != -1) {
                    window.setStatusBarColor(statusBarColor);
                }
                if (navigationBarColor != -1) {
                    window.setNavigationBarColor(navigationBarColor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
