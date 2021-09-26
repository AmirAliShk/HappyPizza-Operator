package ir.food.operatorAndroid.push;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import ir.food.operatorAndroid.app.MyApplication;

public class AvaService extends Service {

    private static final String TAG = AvaService.class.getSimpleName();
    Context context;

    @Override
    public IBinder onBind(Intent intent) {
        AvaLog.i("Push Service BIND");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        AvaLog.i("Push Service UNBIND");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AvaLog.i("Push Service CREATE");
    }


    AvaPref avaPref;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // getAPI socket, if was null create again

        try {
            // if service restart on background we must fill the variable with current context
            if (MyApplication.context == null) {
                MyApplication.context = this;
            }

//      MyApplication.prefManager.incrementResetPushServiceCount();

            avaPref = new AvaPref();
            avaPref.setIpRow(0);

            AvaSocket.getConnection(context).checkConnection();
            AvaLog.i("Push Service STARTING");

            if (avaPref.isMissingSocketEnable()) {
                startCheckConnection();
            }

            if (avaPref.isMissingApiEnable()) {
                startGetMissingPush();
            }

        } catch (Exception e) {
            AvaLog.e("Push Service CRASH", e);
            AvaCrashReporter.send(e, 100);
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        stopGetMissingPush();
        stopCheckConnection();
        AvaSocket.getConnection(context).disconnectSocket();
        Intent intent = new Intent("ir.taxi1880.operatormanagement.PUSH_SERVICE_DESTROY");
        sendBroadcast(intent);

        AvaLog.e("push service Stopped");
        super.onDestroy();
    }

    // Send Data every 1 seconds to activity
    private Timer unreadMessageTimer;

    public void startGetMissingPush() {
        if (unreadMessageTimer != null) {
            return;
        }
        unreadMessageTimer = new Timer();
        unreadMessageTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new ReadUnreadMessage().getUnreadPush(false, context);
            }
        }, 0, avaPref.getIntervalTime() * 1000);
    }

    public void stopGetMissingPush() {
        AvaLog.w("timeTask stop");

        if (unreadMessageTimer != null)
            unreadMessageTimer.cancel();
        unreadMessageTimer = null;
    }

    private Timer checkConnectionTimer;

    public void startCheckConnection() {
        if (checkConnectionTimer != null) {
            return;
        }

        checkConnectionTimer = new Timer();
        checkConnectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AvaSocket.getConnection(context).checkConnection();
            }
        }, 20000, avaPref.getMissingSocketIntervalTime() * 1000);
    }

    public void stopCheckConnection() {
        if (checkConnectionTimer != null) {
            checkConnectionTimer.cancel();
        }
        checkConnectionTimer = null;
    }
}
