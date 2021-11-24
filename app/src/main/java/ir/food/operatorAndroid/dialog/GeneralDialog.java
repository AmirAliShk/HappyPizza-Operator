package ir.food.operatorAndroid.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ir.food.operatorAndroid.app.MyApplication;
import ir.food.operatorAndroid.databinding.DialogGeneralBinding;
import ir.food.operatorAndroid.helper.TypefaceUtil;

public class GeneralDialog {
    private Runnable bodyRunnable = null;
    private Runnable dismissBody = null;
    private ButtonModel firstBtn = null;
    private ButtonModel secondBtn = null;
    private ButtonModel thirdBtn = null;
    private String messageText = "";
    private String titleText = "";
    private int visibility;
    private boolean cancelable = true;
    private boolean singleInstance = false;
    public static final String ERROR = "error";
    DialogGeneralBinding binding;

    private class ButtonModel {
        String text;
        Runnable body;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Runnable getBody() {
            return body;
        }

        public void setBody(Runnable body) {
            this.body = body;
        }
    }

    public GeneralDialog isSingleMode(boolean singleInstance) {
        this.singleInstance = singleInstance;
        return this;
    }

    public GeneralDialog messageVisibility(int visible) {
        this.visibility = visible;
        return this;
    }

    public GeneralDialog afterDismiss(Runnable dismissBody) {
        this.dismissBody = dismissBody;
        return this;
    }

    public GeneralDialog firstButton(String name, Runnable body) {
        firstBtn = new ButtonModel();
        firstBtn.setBody(body);
        firstBtn.setText(name);
        return this;
    }

    public GeneralDialog secondButton(String name, Runnable body) {
        secondBtn = new ButtonModel();
        secondBtn.setBody(body);
        secondBtn.setText(name);
        return this;
    }

    public GeneralDialog thirdButton(String name, Runnable body) {
        thirdBtn = new ButtonModel();
        thirdBtn.setBody(body);
        thirdBtn.setText(name);
        return this;
    }

    public GeneralDialog bodyRunnable(Runnable bodyRunnable) {
        this.bodyRunnable = bodyRunnable;
        return this;
    }

    public GeneralDialog message(String messageText) {
        this.messageText = messageText;
        return this;
    }

    public GeneralDialog title(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public GeneralDialog cancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    private Dialog dialog;
    private Dialog staticDialog = null;

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        Dialog tempDialog = null;
        if (singleInstance) {
            if (staticDialog != null) {
                staticDialog.dismiss();
                staticDialog = null;
            }
            staticDialog = new Dialog(MyApplication.currentActivity);
            tempDialog = staticDialog;
        } else {
            dialog = new Dialog(MyApplication.currentActivity);
            tempDialog = dialog;
        }
        tempDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = DialogGeneralBinding.inflate(LayoutInflater.from(dialog.getContext()));
        tempDialog.setContentView(binding.getRoot());
        tempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = tempDialog.getWindow().getAttributes();
        tempDialog.getWindow().setAttributes(wlp);
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        tempDialog.setCancelable(cancelable);
        TypefaceUtil.overrideFonts(tempDialog.getWindow().getDecorView());

        binding.txtMessage.setText(messageText);

        if (messageText.isEmpty()) {
            binding.txtMessage.setVisibility(View.GONE);
        }
        if (firstBtn == null) {
            binding.btnPositive.setVisibility(View.GONE);
            binding.vMiddle.setVisibility(View.GONE);
        } else {
            binding.btnPositive.setText(firstBtn.text);
        }
        if (secondBtn == null) {
            binding.btnNegative.setVisibility(View.GONE);
            binding.vMiddle.setVisibility(View.GONE);
        } else {
            binding.btnNegative.setText(secondBtn.text);
        }

        if (firstBtn == null && secondBtn == null && thirdBtn == null) {
            binding.llBtnView.setVisibility(View.GONE);
        }

        binding.btnNegative.setOnClickListener(view -> {
            dismiss();
            if (secondBtn != null) {
                if (secondBtn.getBody() != null)
                    secondBtn.getBody().run();
            }
        });

        binding.btnPositive.setOnClickListener(view -> {
            dismiss();
            if (firstBtn != null) {
                if (firstBtn.getBody() != null) {
                    firstBtn.getBody().run();
                }
            }
        });

        if (bodyRunnable != null)
            bodyRunnable.run();


        tempDialog.setOnDismissListener(dialog -> {
            if (dismissBody != null)
                dismissBody.run();
        });
        tempDialog.show();
    }

    // dismiss center control
    public void dismiss() {
        try {
            if (singleInstance) {
                if (staticDialog != null) {
                    staticDialog.dismiss();
                    staticDialog = null;
                }
            } else {
                if (dialog != null)
                    if (dialog.isShowing())
                        dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog = null;
    }
}
