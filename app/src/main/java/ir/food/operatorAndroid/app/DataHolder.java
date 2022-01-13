package ir.food.operatorAndroid.app;

import java.util.ArrayList;
import java.util.HashMap;

import ir.food.operatorAndroid.model.ProductsModel;
import ir.food.operatorAndroid.model.SupportCartModel;

public class DataHolder {
    private static DataHolder ourInstance;
    public String reserveDate = "0";
    public String voipId = "0";
    public String pushType = null;

    private HashMap<String,SupportCartModel> customerCart = null;

    public HashMap<String,SupportCartModel> getCustomerCart() {
        return ourInstance.customerCart;
    }

    public void setCustomerCart(HashMap<String,SupportCartModel> cart) {
        ourInstance.customerCart = cart;
    }

    public String getVoipId() {
        return ourInstance.voipId;
    }

    public void setVoipId(String voipId) {
        ourInstance.voipId = voipId;
    }

    public static DataHolder getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataHolder();
        }
        return ourInstance;
    }

    public String getPushType() {
        return ourInstance.pushType;
    }

    public void setPushType(String pushType) {
        ourInstance.pushType = pushType;
    }
}
