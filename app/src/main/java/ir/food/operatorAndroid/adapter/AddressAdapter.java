package ir.food.operatorAndroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ir.food.operatorAndroid.R;
import ir.food.operatorAndroid.app.EndPoints;
import ir.food.operatorAndroid.dialog.GeneralDialog;
import ir.food.operatorAndroid.helper.TypefaceUtil;
import ir.food.operatorAndroid.model.AddressModel;
import ir.food.operatorAndroid.okHttp.RequestHelper;
import ir.food.operatorAndroid.push.AvaCrashReporter;

public class AddressAdapter extends BaseAdapter {

    private ArrayList<AddressModel> addressModels;
    private LayoutInflater layoutInflater;
    String mobile;

    public AddressAdapter(ArrayList<AddressModel> addressModels, Context context, String mobile) {
        this.addressModels = addressModels;
        this.layoutInflater = LayoutInflater.from(context);
        this.mobile = mobile;
    }

    @Override
    public int getCount() {
        return addressModels.size();
    }

    @Override
    public Object getItem(int position) {
        return addressModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View myView = convertView;

        try {
            final AddressModel addressModel = addressModels.get(position);
            if (myView == null) {
                myView = layoutInflater.inflate(R.layout.item_address_dialog, null);
                TypefaceUtil.overrideFonts(myView);
            }
            TextView txtAddress = myView.findViewById(R.id.txtAddress);
            TextView txtStation = myView.findViewById(R.id.txtStation);
            ImageView imgArchive = myView.findViewById(R.id.imgArchive);
            LinearLayout llStation = myView.findViewById(R.id.llStation);

            if (addressModel.isArchive() == 1) {
                llStation.setVisibility(View.GONE);
            } else {
                llStation.setVisibility(View.VISIBLE);
            }
            txtAddress.setText(addressModel.getAddress());
            txtStation.setText(addressModel.getStationId());

            imgArchive.setOnClickListener(view -> new GeneralDialog()
                    .title("هشدار")
                    .message("ایا از انجام عملیات فوق اطمینان دارید؟")
                    .firstButton("بله", () -> {
                        archiveAddress(addressModel.getAddressId());
                        addressModels.remove(position);
                        notifyDataSetChanged();
                    })
                    .secondButton("خیر", null)
                    .cancelable(false)
                    .show());

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "LastAddressAdapter class, getView method");
        }
        return myView;
    }

    void archiveAddress(String id) {
        RequestHelper.builder(EndPoints.INSTANCE.getARCHIVE_ADDRESS())
                .addParam("mobile", mobile)
                .addParam("addressId", id)
                .listener(archiveCallBack)
                .put();
    }

    RequestHelper.Callback archiveCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
//            {"success":true,"message":"آدرس مشتری با موفقیت آرشیو شد"}

        }
    };
}
