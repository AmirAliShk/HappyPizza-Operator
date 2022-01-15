package ir.food.operatorAndroid.app;

import java.util.ArrayList;
import java.util.HashMap;

import ir.food.operatorAndroid.model.ProductsModel;

public class DataHolder {
    private static DataHolder ourInstance;
    public String reserveDate = "0";
    public String voipId = "0";
    public String pushType = null;

    private HashMap<String,ProductsModel> customerCart = null;

    public HashMap<String,ProductsModel> getCustomerCart() {
        return ourInstance.customerCart;
    }

    public void setCustomerCart(HashMap<String,ProductsModel> cart) {
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
