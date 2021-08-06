package ir.food.operatorAndroid.app;

public class DataHolder {
    private static DataHolder ourInstance;
    public String reserveDate = "0";
    public String voipId = "0";

    public String getVoipId() {
        return ourInstance.voipId;
    }

    public void setVoipId(String voipId) {
        ourInstance.voipId = voipId;
    }

    public String getReserveDate() {
        return ourInstance.reserveDate;
    }

    public void setReserveDate(String reserveDate) {
        ourInstance.reserveDate = reserveDate;
    }

    public static DataHolder getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataHolder();
        }
        return ourInstance;
    }
}
