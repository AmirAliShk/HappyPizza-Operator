package ir.team_x.crm.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import ir.team_x.crm.R;
import ir.team_x.crm.app.MyApplication;
import ir.team_x.crm.helper.TypefaceUtil;

public class LoadingDialog {

    private static final String TAG = LoadingDialog.class.getSimpleName();
    private static Dialog ldialog;
    private static Dialog cancelableLoaderDialog;


    public static void makeLoader() {
        if (ldialog != null) return;
        try {

            if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
                return;

            ldialog = new Dialog(MyApplication.currentActivity);
            ldialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            ldialog.setContentView(R.layout.dialog_loder);
            ldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wlp = ldialog.getWindow().getAttributes();
            ldialog.getWindow().setAttributes(wlp);
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            ldialog.setCancelable(false);
            ldialog.show();

        } catch (Exception e) {
            Log.e(TAG, "makeLoader: " + e);
        }
    }

    public static void makeCancelableLoader() {
        if (cancelableLoaderDialog != null) return;
        try {
            if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
                return;
            cancelableLoaderDialog = new Dialog(MyApplication.currentActivity);
            cancelableLoaderDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            cancelableLoaderDialog.setContentView(R.layout.dialog_cancelable_loder);
            cancelableLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TypefaceUtil.overrideFonts(cancelableLoaderDialog.getWindow().getDecorView());
            WindowManager.LayoutParams wlp = cancelableLoaderDialog.getWindow().getAttributes();
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            cancelableLoaderDialog.getWindow().setAttributes(wlp);
            cancelableLoaderDialog.setCancelable(false);
            Button btnCancel = cancelableLoaderDialog.findViewById(R.id.btnCancel);

            btnCancel.setOnClickListener(v -> dismissCancelableDialog());

            cancelableLoaderDialog.show();

        } catch (Exception e) {
            Log.e(TAG, "makeLoader: " + e);
        }
    }

    public static void dismiss() {
        try {
            if (ldialog != null)
                if (ldialog.isShowing())
                    ldialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "dismiss: " + e.getMessage());
        }
        ldialog = null;
    }

    public static void dismissCancelableDialog() {
        try {
            if (cancelableLoaderDialog != null)
                if (cancelableLoaderDialog.isShowing())
                    cancelableLoaderDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "dismiss: " + e.getMessage());
        }
        cancelableLoaderDialog = null;
    }

}