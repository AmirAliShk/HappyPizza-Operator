package ir.team_x.crm.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.team_x.crm.R;
import ir.team_x.crm.app.MyApplication;
import ir.team_x.crm.helper.TypefaceUtil;

public class ErrorDialog {
    static Dialog dialog;
    private Runnable closeRunnable;
    private Runnable tryAgainRunnable;
    private Runnable bodyRunnable;
    private String closeText = "بستن";
    private String tryAgainText = "تلاش مجدد";
    private String messageText;
    private String titleText = null;
    private boolean cancelable;
    private Runnable dismissRunnable;

    public ErrorDialog closeBtnRunnable(String closeText, Runnable closeRunnable) {
        this.closeRunnable = closeRunnable;
        this.closeText = closeText;
        return this;
    }

    public ErrorDialog tryAgainBtnRunnable(String tryAgainText, Runnable tryAgainRunnable) {
        this.tryAgainRunnable = tryAgainRunnable;
        this.tryAgainText = tryAgainText;
        return this;
    }

    public ErrorDialog bodyRunnable(Runnable bodyRunnable) {
        this.bodyRunnable = bodyRunnable;
        return this;
    }

    public ErrorDialog messageText(String messageText) {
        this.messageText = messageText;
        return this;
    }

    public ErrorDialog titleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public ErrorDialog cancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public ErrorDialog onDismiss(Runnable runnable) {
        this.dismissRunnable = runnable;
        return this;
    }

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_error);

        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
//    wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);

        dialog.setCancelable(cancelable);

//    LinearLayout llParent=dialog.findViewById(R.id.llParent);
//    llParent.setLayoutParams(new LinearLayout.LayoutParams(300, 150));

        TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
        txtMessage.setText(messageText);

        TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
        if (titleText == null || titleText.isEmpty()) {
            title.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
            title.setText(titleText);
        }

        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        Button btnTryAgain = dialog.findViewById(R.id.btnTryAgain);

//        btnClose.setText(closeText);
        btnTryAgain.setText(tryAgainText);

        if (bodyRunnable != null)
            bodyRunnable.run();

        btnClose.setOnClickListener(v -> {
            if (closeRunnable != null)
                closeRunnable.run();
            else
                dismiss();
        });

        btnTryAgain.setOnClickListener(v -> {
            if (tryAgainRunnable != null)
                tryAgainRunnable.run();
            dismiss();
        });

        dialog.setOnDismissListener(dialogInterface -> {
            Log.i("GENERAL", "onDismiss General dialog");
            if (dismissRunnable != null)
                dismissRunnable.run();
        });

        dialog.show();
    }

    public static void dismiss() {
        try {
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.dismiss();
        } catch (Exception e) {

        }
        dialog = null;
    }

}
