package ir.food.operatorAndroid.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import ir.food.operatorAndroid.R;
import ir.food.operatorAndroid.adapter.AddressAdapter;
import ir.food.operatorAndroid.app.MyApplication;
import ir.food.operatorAndroid.databinding.DialogAddressListBinding;
import ir.food.operatorAndroid.helper.KeyBoardHelper;
import ir.food.operatorAndroid.helper.TypefaceUtil;
import ir.food.operatorAndroid.model.AddressModel;
import ir.food.operatorAndroid.push.AvaCrashReporter;

public class AddressDialog {

    public interface Listener {
        void address(AddressModel addressModel);
    }

    Dialog dialog;
    DialogAddressListBinding binding;
    Listener listener;

    public void show(ArrayList<AddressModel> addressModels, String mobile, Listener listener) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        binding = DialogAddressListBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        this.listener = listener;

        binding.imgClose.setOnClickListener(view -> dismiss());

        AddressAdapter addressAdapter = new AddressAdapter(addressModels, MyApplication.context, mobile);
        binding.listAddress.setAdapter(addressAdapter);

        binding.listAddress.setOnItemClickListener((adapterView, view, i, l) -> {
            listener.address(addressModels.get(i));
            dismiss();
        });

        dialog.show();
    }

    private void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            AvaCrashReporter.send(e, "AddressDialog class, dismiss method");
        }
        dialog = null;
    }
}