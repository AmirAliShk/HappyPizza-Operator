package ir.food.operatorAndroid.push;

import android.content.Context;
import android.os.StrictMode;

import ir.food.operatorAndroid.app.MyApplication;
import ir.food.operatorAndroid.helper.ServiceHelper;

public class AvaFactory {

    private static AvaFactory instance;
    private Context context;
    private int projectID;
    private String token;
    private String[] address = null;
    private String userID;

    public static AvaFactory getInstance(Context context) {
        if (instance == null) {
            instance = new AvaFactory();
        }
        instance.context = context;
        return instance;
    }

    public AvaFactory setAddress(String... address) {
        instance.address = address;
        return instance;
    }

    public AvaFactory setProjectID(int projectID) {
        instance.projectID = projectID;
        return instance;
    }

    public AvaFactory setUserID(String userID) {
        instance.userID = userID;
        return instance;
    }

    public AvaFactory setToken(String token) {
        instance.token = token;
        return instance;
    }

    public AvaFactory readMissingPush() {
        new ReadUnreadMessage().getUnreadPush(true, instance.context);
        return instance;
    }

    public static Context getContext() {
        return instance.context;
    }

    boolean status = false;

    /**
     * if it starts pushService, return true
     * if pushService was started, return false
     */
    public boolean start() {
        AvaPref avaPref = new AvaPref();
        avaPref.setProjectId(projectID);
        avaPref.setToken(token);
        avaPref.setUserId(userID);
        avaPref.setAddress(address);
        instance.status = false;

        //some times getConnection NetworkOnMainThreadException error even though service is run on background and socket have async
        //added this code to improve this error
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//    instance.status = false;
        //after 1 second start push service
        //wait this time for first time app init is completed
        MyApplication.handler.postDelayed(() -> {
            //push service must every time run on background
            //we have checked service by below if
            if (!ServiceHelper.isRunning(instance.context, AvaService.class)) {
                ServiceHelper.start(instance.context, AvaService.class);
                AvaLog.i("start push service from factory");
                instance.status = true;
            }

            MyApplication.handler.postDelayed(() -> {
                if (!instance.status) {
                    AvaSocket.getConnection(instance.context).checkConnection();
                }
            }, 500);
        }, 500);

        return status;
    }

}
