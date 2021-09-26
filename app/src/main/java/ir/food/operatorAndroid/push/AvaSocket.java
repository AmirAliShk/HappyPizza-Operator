package ir.food.operatorAndroid.push;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Proxy;
import java.util.Calendar;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import ir.food.operatorAndroid.BuildConfig;
import ir.food.operatorAndroid.app.EndPoints;
import ir.food.operatorAndroid.app.MyApplication;
import ir.food.operatorAndroid.helper.NetworkUtil;
import okhttp3.OkHttpClient;

public class AvaSocket {

    @SuppressLint("StaticFieldLeak")
    private static AvaSocket instance;
    private Socket socket;
    private Context context;

    public static AvaSocket getConnection(Context context) {
        instance = getSync(context);
        instance.context = context;
        return instance;
    }

    private static synchronized AvaSocket getSync(Context context) {
        if (instance == null) {
            instance = new AvaSocket(context);
        }

        if (instance.socket == null) {
            instance = new AvaSocket(context);
        }
        return instance;
    }

    void restartConnection() {
        disconnectSocket();
        MyApplication.handler.postDelayed(() -> {
            getSync(instance.context);
            AvaLog.i("socket restarted");
        }, 1000);
    }

    void checkConnection() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        int checkAfter = 30000;
        if (!instance.socket.connected()) {
            //check internet connection is connected
            if (NetworkUtil.getConnectivityStatusString(instance.context) != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                if (currentTime - getPrefInstance().getLastPongReceiveAt() < checkAfter
                        && currentTime - getPrefInstance().getLastReconnectTime() < checkAfter) {
                    AvaLog.w("socket not connected try restart socket");
                    //for fail over if ip is failed you must set counter ip reset
                    getPrefInstance().setIpRow(0);
                    restartConnection();
                } else {
                    instance.connectSocket();
                }
            }
        }
    }

    void disconnectSocket() {
        if (instance != null)
            if (instance.socket != null) {
                instance.socket.off(Keys.EVENT_PUSH);
                instance.socket.disconnect();
            }
    }

    void connectSocket() {
        if (instance != null)
            if (instance.socket != null) {
                instance.socket.connect();
                instance.openListener(instance.socket);
                return;
            }
        restartConnection();
    }

    public Socket getSocket() {
        return instance.socket;
    }

    private AvaSocket(Context c) {
        this.context = c;
        this.socket = getServerSocket();
    }

    private AvaPref avaPref = null;

    private static AvaPref getPrefInstance() {
        if (instance != null) {
            if (instance.avaPref == null) {
                instance.avaPref = new AvaPref();
            }
            return instance.avaPref;
        }
        return new AvaPref();
    }

    private static int getProjectID() {
        return getPrefInstance().getProjectId();
    }

    private static String getDeviceID() {
        return getPrefInstance().getDeviceId();
    }

    private static String getUserID() {
        return getPrefInstance().getUserId();
    }

    private static String getToken() {
        return getPrefInstance().getToken();
    }


    @SuppressLint("HardwareIds")
    private Socket getServerSocket() {

        try {
            Socket mSocket;
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{WebSocket.NAME};
            // Polling.NAME
            opts.forceNew = false;
            opts.reconnectionAttempts = 2000;
            opts.reconnectionDelay = 1000;
            opts.timeout = 10000;
            opts.reconnection = true;

            OkHttpClient client = new OkHttpClient
                    .Builder()
                    .proxy(Proxy.NO_PROXY)
                    .build();
            opts.webSocketFactory = client;
            opts.callFactory = client;

            String deviceID = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            getPrefInstance().setDeviceId(deviceID);

            opts.query = "channelInfo=" + getUserID() + "," + getProjectID() + "," + getToken() + ",9" + BuildConfig.VERSION_CODE + "," + getDeviceID() + "," + context.getPackageName();

            AvaLog.e(opts.query);
            mSocket = IO.socket(EndPoints.PUSH, opts);
            mSocket.connect();
            AvaLog.i("create new socket connection");
            openListener(mSocket);
            getPrefInstance().increaseIpRow();
            return mSocket;

        } catch (Exception e1) {
            AvaCrashReporter.send(e1, 102);
            AvaLog.e("Ava Connection Failed", e1);
        }
        return null;
    }


    private final Emitter.Listener onReConnectedListener = args -> {

        getPrefInstance().setLastReconnectTime();
        try {
            if (args.length > 0) {
//        if (Integer.parseInt(args[0].toString()) > 20) {
//          if (AppStatusHelper.appIsRun(instance.context)) {
//            if (getPrefInstance().getIpRow() >= getPrefInstance().getAddress().size()) {
//              getPrefInstance().setIpRow(0);
//            }
//          }
//          restartConnection();
//        }
                AvaLog.i("try Reconnecting : " + args[0].toString());
            } else
                AvaLog.i("Ava Reconnect no message");
        } catch (Exception e) {
            AvaCrashReporter.send(e, 103);

        }
    };

    private final Emitter.Listener onPING = args -> {
        try {
            AvaLog.i("PONG");
            getPrefInstance().setPongReceived();
        } catch (Exception e) {
            AvaCrashReporter.send(e, 104);
            e.printStackTrace();
        }
    };

    private final Emitter.Listener onConfigListener = args -> {
        try {
            String result = args[0].toString();
            AvaLog.i("new Config :  " + result);
            JSONObject config = new JSONObject(result);
            getPrefInstance().setMissingApiEnable(config.getBoolean("missingApiEnable"));
            getPrefInstance().setIntervalTime(config.getInt("missingApiInterval"));
            getPrefInstance().setMissingSocket(config.getBoolean("missingSocketEnable"));
            getPrefInstance().setMissingSocketIntervalTime(config.getInt("missingSocketInterval"));
            getPrefInstance().setMissingApiUrl(config.getString("missingApiUrl"));
            AvaLog.i("Config set successfully :) ");
        } catch (Exception e) {
            AvaCrashReporter.send(e, 105);
            e.printStackTrace();
        }
    };

    private final Emitter.Listener onConnectedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                AvaLog.i("We now Connected :) ");
                AvaReporter.Message(instance.context, Keys.CONNECTED, null);
                getPrefInstance().setIpRow(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final Emitter.Listener onDisconnectListener = args -> AvaLog.e("Ava disconnectSocket :(");

    private final Emitter.Listener onErrorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            AvaLog.e("We have an Error :(");
            try {
                AvaLog.e(args[0].toString());
                JSONObject result = new JSONObject(args[0].toString());
                int errorCode = result.getInt("errorCode");
                if (errorCode == 401) {
                    AvaReporter.Message(instance.context, Keys.AUTHORIZED_FAILED, args[0].toString());
//          ServiceHelper.stop(context, AvaService.class);

                } else {
                    AvaReporter.Message(instance.context, Keys.DISCONNECT, args[0].toString());
                }
            } catch (Exception e1) {
                AvaCrashReporter.send(e1, 107);

                e1.printStackTrace();
            }
        }
    };

    private final Emitter.Listener onErrorInternetListener = args -> AvaLog.e("don't access Server over this IP :(");

    private final Emitter.Listener pushListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String result = args[0].toString();
            AvaReporter.Message(context, Keys.PUSH_RECEIVE, result);
            try {
                socket.emit(Keys.EVENT_PUSH, new JSONObject(result));
            } catch (JSONException e) {
                AvaCrashReporter.send(e, 108);
                e.printStackTrace();
            }
        }
    };

    void openListener(Socket socket) {
        if (!socket.hasListeners(Socket.EVENT_RECONNECT_ATTEMPT))
            socket.on(Socket.EVENT_RECONNECT_ATTEMPT, onReConnectedListener);
        if (!socket.hasListeners(Socket.EVENT_CONNECT_ERROR))
            socket.on(Socket.EVENT_CONNECT_ERROR, onErrorInternetListener);//internet problem
        if (!socket.hasListeners(Socket.EVENT_CONNECT))
            socket.on(Socket.EVENT_CONNECT, onConnectedListener);//connected
        if (!socket.hasListeners(Socket.EVENT_ERROR))
            socket.on(Keys.EVENT_ERROR, onErrorListener);//Authorized failed
        if (!socket.hasListeners(Keys.EVENT_CONFIG))
            socket.on(Keys.EVENT_CONFIG, onConfigListener);//getConnection api config
        if (!socket.hasListeners(Socket.EVENT_CONNECT_TIMEOUT))
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onErrorInternetListener);
        if (!socket.hasListeners(Socket.EVENT_DISCONNECT))
            socket.on(Socket.EVENT_DISCONNECT, onDisconnectListener);
        if (!socket.hasListeners(Socket.EVENT_PING))
            socket.on(Socket.EVENT_PING, onPING);
        if (!socket.hasListeners(Keys.EVENT_PUSH))
            socket.on(Keys.EVENT_PUSH, pushListener);
    }

    static JSONObject getSocketParams() {
        JSONObject params = new JSONObject();
        try {
            params.put("instanceIsNull", instance == null);
            if (instance != null) {
                params.put("socketIsNull", instance.socket == null);
                params.put("contextIsNull", instance.context == null);
                if (instance.socket != null)
                    params.put("socketId", instance.socket.id());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


}





