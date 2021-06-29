package ir.team_x.crm.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.team_x.crm.R;
import ir.team_x.crm.app.MyApplication;
import ir.team_x.crm.helper.TypefaceUtil;

/***
 * Created by Amirreza Erfanian on 2018/July/26.
 * v : 1.0.0
 */

public class GeneralDialog {

    private Runnable bodyRunnable = null;
    private Runnable dismissBody = null;
    private ButtonModel firstBtn = null;
    private ButtonModel secondBtn = null;
    private ButtonModel thirdBtn = null;
    private DismissListener listener;
    private Listener descListener;
    private String messageText = "";
    private String titleText = "";
    private int visibility;
    private boolean cancelable = true;
    private boolean singleInstance = false;
    public static final String ERROR = "error";

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

    interface DismissListener {
        void onDismiss();
    }

    public interface Listener {
        void onDescription(String message);
    }

    public GeneralDialog isSingleMode(boolean singleInstance) {
        this.singleInstance = singleInstance;
        return this;
    }

    public GeneralDialog onDescriptionListener(Listener listener) {
        this.descListener = listener;
        return this;
    }

    public GeneralDialog messageVisibility(int visible) {
        this.visibility = visible;
        return this;
    }

    public GeneralDialog onDismissListener(DismissListener listener) {
        this.listener = listener;
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

    Unbinder unbinder;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.llTitle)
    LinearLayout llTitle;

    @BindView(R.id.txtMessage)
    TextView txtMessage;

    @BindView(R.id.imgSpace)
    View imgSpace;

    @BindView(R.id.llBtnView)
    LinearLayout llBtnView;

    @BindView(R.id.btnFirst)
    Button btnFirst;

    @BindView(R.id.edtMessage)
    EditText edtMessage;

    @BindView(R.id.btnSecond)
    Button btnSecond;

    @BindView(R.id.btnThird)
    Button btnThird;

    @OnClick(R.id.btnFirst)
    void onFirstPress() {
        dismiss();
        if (edtMessage.getVisibility() == View.VISIBLE) {
            descListener.onDescription(edtMessage.getText().toString());
        }
        if (firstBtn != null) {
            if (firstBtn.getBody() != null) {
                firstBtn.getBody().run();
            }
        }
    }

    @OnClick(R.id.btnSecond)
    void onSecondPress() {
        dismiss();

        if (secondBtn != null) {
            if (secondBtn.getBody() != null)
                secondBtn.getBody().run();
        }
    }

    @OnClick(R.id.btnThird)
    void onThirdPress() {
        dismiss();

        if (thirdBtn != null) {
            if (thirdBtn.getBody() != null)
                thirdBtn.getBody().run();
        }
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
        tempDialog.setContentView(R.layout.dialog_general);
        tempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = tempDialog.getWindow().getAttributes();
        tempDialog.getWindow().setAttributes(wlp);
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        tempDialog.setCancelable(cancelable);
        unbinder = ButterKnife.bind(this, tempDialog);
        TypefaceUtil.overrideFonts(tempDialog.getWindow().getDecorView());

        txtMessage.setText(messageText);
        txtTitle.setText(titleText);
        if (titleText.isEmpty()) {
            txtTitle.setVisibility(View.GONE);
            llTitle.setVisibility(View.GONE);
        }
        if (titleText.isEmpty()) {
            txtTitle.setVisibility(View.GONE);
            txtMessage.setTextSize(20);
        }
        if (messageText.isEmpty()) {
            txtMessage.setVisibility(View.GONE);
        }
        if (firstBtn == null) {
            btnFirst.setVisibility(View.GONE);
        } else {
            btnFirst.setText(firstBtn.getText());
        }
        if (secondBtn == null) {
            btnSecond.setVisibility(View.GONE);
            imgSpace.setVisibility(View.GONE);
        } else {
            btnSecond.setText(secondBtn.getText());
            imgSpace.setVisibility(View.VISIBLE);
        }
        if (thirdBtn == null) {
            btnThird.setVisibility(View.GONE);
        } else {
            btnThird.setText(thirdBtn.getText());
        }

        if (firstBtn == null && secondBtn == null && thirdBtn == null) {
            llBtnView.setVisibility(View.GONE);
        }
        if (visibility == 1) {
            edtMessage.setVisibility(View.VISIBLE);
        } else {
            edtMessage.setVisibility(View.GONE);
        }
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
            if (listener != null) {
                listener.onDismiss();
            }
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
