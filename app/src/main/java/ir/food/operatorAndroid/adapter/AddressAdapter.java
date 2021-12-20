package ir.food.operatorAndroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.food.operatorAndroid.R;
import ir.food.operatorAndroid.helper.TypefaceUtil;
import ir.food.operatorAndroid.model.AddressModel;
import ir.food.operatorAndroid.push.AvaCrashReporter;

public class AddressAdapter extends BaseAdapter {

    private ArrayList<AddressModel> addressModels;
    private LayoutInflater layoutInflater;

    public AddressAdapter(ArrayList<AddressModel> addressModels, Context context) {
        this.addressModels = addressModels;
        this.layoutInflater = LayoutInflater.from(context);
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

            txtAddress.setText(addressModel.getAddress());
            txtStation.setText(addressModel.getStationId());

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "LastAddressAdapter class, getView method");
        }
        return myView;
    }
}
