package ir.food.operatorAndroid.sip;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.acra.ACRA;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Config;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.EcCalibratorStatus;
import org.linphone.core.Factory;
import org.linphone.core.LogCollectionState;
import org.linphone.core.tools.Log;
import org.linphone.mediastream.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.food.operatorAndroid.R;
import ir.food.operatorAndroid.activity.CallActivity;
import ir.food.operatorAndroid.activity.RegisterOrderActivity;
import ir.food.operatorAndroid.app.DataHolder;
import ir.food.operatorAndroid.app.MyApplication;
import ir.food.operatorAndroid.helper.NotificationSingleton;
import ir.food.operatorAndroid.helper.ServiceHelper;
import ir.food.operatorAndroid.push.AvaCrashReporter;
import ir.food.operatorAndroid.push.AvaFactory;
import ir.food.operatorAndroid.receiver.BluetoothReceiver;
import ir.food.operatorAndroid.receiver.HeadsetReceiver;

import static android.media.AudioManager.MODE_RINGTONE;
import static android.media.AudioManager.STREAM_RING;
import static android.media.AudioManager.STREAM_VOICE_CALL;
import static java.lang.Thread.sleep;

public class LinphoneService extends Service {
    private static final String START_LINPHONE_LOGS = " ==== Device information dump ====";
    // Keep a static reference to the Service so we can access it from anywhere in the app
    private static LinphoneService sInstance;
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private Handler mHandler;
    private Timer mTimer;

    private Core mCore;
    private CoreListenerStub mCoreListener;
    long[] pattern = {0, 70, 70};

    public static boolean isReady() {
        return sInstance != null;
    }

    public static LinphoneService getInstance() {
        return sInstance;
    }

    public static Core getCore() {
        if (sInstance != null && sInstance.mCore != null) {
            return sInstance.mCore;
        } else {
            ServiceHelper.start(MyApplication.context, LinphoneService.class);
            while (!LinphoneService.isReady()) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    AvaCrashReporter.send(e, "LinphoneService class, getCore method ");
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }

            return sInstance.mCore;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        // The first call to liblinphone SDK MUST BE to a Factory method
        // So let's enable the library debug logs & log collection
        String basePath = getFilesDir().getAbsolutePath();
        Factory.instance().setLogCollectionPath(basePath);
        Factory.instance().enableLogCollection(LogCollectionState.Enabled);
        Factory.instance().setDebugMode(false, "SIP ===>> ");

        // Dump some useful information about the device we're running on
        Log.i(START_LINPHONE_LOGS);
        dumpDeviceInformation();
        dumpInstalledLinphoneInformation();

        mHandler = new Handler();

        mAudioManager = ((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE));
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        mEchoTesterIsRunning = false;
        mHeadsetReceiverRegistered = false;
        mBasePath = basePath;

        startBluetooth();


        // This will be our main Core listener, it will change activities depending on events
        mCoreListener = new CoreListenerStub() {
            @Override
            public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {

                if (state == Call.State.IncomingReceived || (state == Call.State.IncomingEarlyMedia && mContext.getResources().getBoolean(R.bool.allow_ringing_while_early_media))) {
                    onIncomingReceived();
                    MyApplication.prefManager.setCallIncoming(true);
                    // Brighten screen for at least 10 seconds
                    if (core.getCallsNb() == 1) {

                        requestAudioFocus(STREAM_RING);

                        mRingingCall = call;
                        startRinging(call.getRemoteAddress());
                        // otherwise there is the beep
                    }
                } else if (call == mRingingCall && mIsRinging) {
                    // previous state was ringing, so stop ringing
                    stopRinging();
                }

                if (state == Call.State.Connected) {
                    MyApplication.prefManager.setConnectedCall(true);
                    //if don't receive push notification from server we call missingPushApi
                    AvaFactory.getInstance(getApplicationContext()).readMissingPush();
                    MyApplication.prefManager.setCallIncoming(false);

                    if (core.getCallsNb() == 1) {
                        // It is for incoming calls, because outgoing calls enter
                        // MODE_IN_COMMUNICATION immediately when they start.
                        // However, incoming call first use the MODE_RINGING to play the
                        // local ring.
                        if (call.getDir() == Call.Dir.Incoming) {
                            setAudioManagerInCallMode();
                            // mAudioManager.abandonAudioFocus(null);
                            requestAudioFocus(STREAM_VOICE_CALL);
                        }
                        if (!mIsBluetoothHeadsetConnected) {
                            // Only force earpiece audio route for incoming audio calls,
                            // outgoing calls may have manually enabled speaker
                            if (call.getDir() == Call.Dir.Incoming) {
                                routeAudioToEarPiece();
                            }
                        }
                        // Only register this one when a call is active

                        enableHeadsetReceiver();
                    }
                } else if (state == Call.State.End || state == Call.State.Error) {
                    MyApplication.prefManager.setCallIncoming(false);
                    MyApplication.prefManager.setConnectedCall(false);
                    DataHolder.getInstance().setVoipId("0");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(0);
                    if (core.getCallsNb() == 0) {
                        if (mAudioFocused) {
                            int res = mAudioManager.abandonAudioFocus(null);
                            Log.d(
                                    "[Audio Manager] Audio focus released a bit later: "
                                            + (res
                                            == AudioManager
                                            .AUDIOFOCUS_REQUEST_GRANTED
                                            ? "Granted"
                                            : "Denied"));
                            mAudioFocused = false;
                        }

                        // Only register this one when a call is active
                        if (mHeadsetReceiver != null && mHeadsetReceiverRegistered) {
                            Log.i("[Audio Manager] Unregistering headset receiver");
                            mContext.unregisterReceiver(mHeadsetReceiver);
                            mHeadsetReceiverRegistered = false;
                        }

                        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

                        if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                            Log.d("[Audio Manager] ---AndroidAudioManager: back to MODE_NORMAL");
                            mAudioManager.setMode(AudioManager.MODE_NORMAL);
                            Log.d("[Audio Manager] All call terminated, routing back to earpiece");
                            routeAudioToEarPiece();
                        }
                    }
                }

                if (state == Call.State.OutgoingInit) {
                    // Enter the MODE_IN_COMMUNICATION mode as soon as possible, so that
                    // ringback is heard normally in earpiece or bluetooth receiver.
                    setAudioManagerInCallMode();
                    requestAudioFocus(STREAM_VOICE_CALL);
                    if (mIsBluetoothHeadsetConnected) {
                        routeAudioToBluetooth();
                    }
                }

                if (state == Call.State.StreamsRunning) {
                    setAudioManagerInCallMode();
                    if (mIsBluetoothHeadsetConnected) {
                        routeAudioToBluetooth();
                    }
                }

            }

            @Override
            public void onEcCalibrationResult(Core core, EcCalibratorStatus status, int delay_ms) {
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                mAudioManager.abandonAudioFocus(null);
                Log.i("[Audio Manager] Set audio mode on 'Normal'");
            }
        };

        try {
            // Let's copy some RAW resources to the device
            // The default config file must only be installed once (the first time)
            copyIfNotExist(R.raw.linphonerc_default, basePath + "/.linphonerc");
            // The factory config is used to override any other setting, let's copy it each time
            copyFromPackage(R.raw.linphonerc_factory, "linphonerc");
        } catch (IOException ioe) {
            Log.e(ioe);
            AvaCrashReporter.send(ioe, "LinphoneService class, getCore method ");
        }

        // Create the Core and add our listener
        mCore = Factory.instance().createCore(basePath + "/.linphonerc", basePath + "/linphonerc", this);
        mCore.addListener(mCoreListener);
        // Core is ready to be configured
        configureCore();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NotificationSingleton.getNotificationId(), NotificationSingleton.getNotification(this));
        }

        // If our Service is already running, no need to continue
        if (sInstance != null) {
            return START_STICKY;
        }

        // Our Service has been started, we can keep our reference on it
        // From now one the Launcher will be able to call onServiceReady()
        sInstance = this;

        mCore.setUserAgent("mohsen@123", null);

        // Core must be started after being created and configured
        mCore.start();
        // We also MUST call the iterate() method of the Core on a regular basis
        TimerTask lTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(
                        () -> {
                            if (mCore != null) {
                                mCore.iterate();
                            }
                        });
            }
        };
        mTimer = new Timer("Linphone scheduler");
        mTimer.schedule(lTask, 0, 20);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mCore.removeListener(mCoreListener);
        if (mTimer != null)
            mTimer.cancel();
        mCore.stop();
        // A stopped Core can be started again
        // To ensure resources are freed, we must ensure it will be garbage collected
        mCore = null;
        // Don't forget to free the singleton as well
        sInstance = null;

        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // For this sample we will kill the Service at the same time we kill the app
        stopSelf();

        super.onTaskRemoved(rootIntent);
    }

    private void configureCore() {
        // We will create a directory for user signed certificates if needed
        String basePath = getFilesDir().getAbsolutePath();
        String userCerts = basePath + "/user-certs";
        File f = new File(userCerts);
        if (!f.exists()) {
            if (!f.mkdir()) {
                Log.e(userCerts + " can't be created.");
            }
        }
        mCore.setUserCertificatesPath(userCerts);
    }

    private void dumpDeviceInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("DEVICE=").append(Build.DEVICE).append("\n");
        sb.append("MODEL=").append(Build.MODEL).append("\n");
        sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n");
        sb.append("SDK=").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("Supported ABIs=");
        for (String abi : Version.getCpuAbis()) {
            sb.append(abi).append(", ");
        }
        sb.append("\n");
        Log.i("AMIR : " + sb.toString());
    }

    private void dumpInstalledLinphoneInformation() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException nnfe) {
            AvaCrashReporter.send(nnfe, "LinphoneService class, dumpInstalledLinphoneInformation method ");
            Log.e(nnfe);
        }

        if (info != null) {
            Log.i("[Service] Linphone version is ",
                    info.versionName + " (" + info.versionCode + ")");
        } else {
            Log.i("[Service] Linphone version is unknown");
        }
    }

    private void copyIfNotExist(int ressourceId, String target) throws IOException {
        File lFileToCopy = new File(target);
        if (!lFileToCopy.exists()) {
            copyFromPackage(ressourceId, lFileToCopy.getName());
        }
    }

    private void copyFromPackage(int ressourceId, String target) throws IOException {

        FileOutputStream lOutputStream = openFileOutput(target, 0);
        InputStream lInputStream = getResources().openRawResource(ressourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = lInputStream.read(buff)) != -1) {
            lOutputStream.write(buff, 0, readByte);
        }
        lOutputStream.flush();
        lOutputStream.close();
        lInputStream.close();
    }

    private void onIncomingReceived() {
        if (RegisterOrderActivity.Companion.isRunning()) return;

        Intent intent = new Intent(this, CallActivity.class);
//         This flag is required to start an Activity from a Service context
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public static void removeFromUIThreadDispatcher(Runnable r) {
        sHandler.removeCallbacks(r);
    }

    public static void dispatchOnUIThreadAfter(Runnable r, long after) {
        sHandler.postDelayed(r, after);
    }

    public static void trackBreadcrumb(String event) {
        ACRA.getErrorReporter().putCustomData("Event details", event);
    }

    /****************************************Audio manager*********************************************/
    private Context mContext;
    private AudioManager mAudioManager;
    private Call mRingingCall;
    private MediaPlayer mRingerPlayer;
    private Vibrator mVibrator;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothReceiver mBluetoothReceiver;
    private HeadsetReceiver mHeadsetReceiver;
    private boolean mHeadsetReceiverRegistered;
    private boolean mIsRinging;
    private boolean mAudioFocused;
    private boolean mEchoTesterIsRunning;
    private boolean mIsBluetoothHeadsetConnected;
    private boolean mIsBluetoothHeadsetScoConnected;

    public void destroy() {
        if (mBluetoothAdapter != null && mBluetoothHeadset != null) {
            Log.i("[Audio Manager] [Bluetooth] Closing HEADSET profile proxy");
            mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
        }

        Log.i("[Audio Manager] [Bluetooth] Unegistering bluetooth receiver");
        if (mBluetoothReceiver != null) {
            mContext.unregisterReceiver(mBluetoothReceiver);
        }

        Core core = LinphoneService.getCore();
        if (core != null) {
            core.removeListener(mCoreListener);
        }
    }

    /* Audio routing */

    public void setAudioManagerModeNormal() {
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    public void routeAudioToEarPiece() {
        routeAudioToSpeakerHelper(false);
    }

    public void routeAudioToSpeaker() {
        routeAudioToSpeakerHelper(true);
    }

    public boolean isAudioRoutedToSpeaker() {
        return mAudioManager.isSpeakerphoneOn() && !isUsingBluetoothAudioRoute();
    }

    public boolean isAudioRoutedToEarpiece() {
        return !mAudioManager.isSpeakerphoneOn() && !isUsingBluetoothAudioRoute();
    }

    /* Echo cancellation */

    public void startEcCalibration() {
        Core core = LinphoneService.getCore();
        if (core == null) {
            return;
        }

        routeAudioToSpeaker();
        setAudioManagerInCallMode();
        Log.i("[Audio Manager] Set audio mode on 'Voice Communication'");
        requestAudioFocus(STREAM_VOICE_CALL);
        int oldVolume = mAudioManager.getStreamVolume(STREAM_VOICE_CALL);
        int maxVolume = mAudioManager.getStreamMaxVolume(STREAM_VOICE_CALL);
        mAudioManager.setStreamVolume(STREAM_VOICE_CALL, maxVolume, 0);
        core.startEchoCancellerCalibration();
        mAudioManager.setStreamVolume(STREAM_VOICE_CALL, oldVolume, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void startEchoTester() {
        Core core = LinphoneService.getCore();
        if (core == null) {
            return;
        }

        routeAudioToSpeaker();
        setAudioManagerInCallMode();
        Log.i("[Audio Manager] Set audio mode on 'Voice Communication'");
        requestAudioFocus(STREAM_VOICE_CALL);
        int maxVolume = mAudioManager.getStreamMaxVolume(STREAM_VOICE_CALL);
        int sampleRate;
        mAudioManager.setStreamVolume(STREAM_VOICE_CALL, maxVolume, 0);
        String sampleRateProperty =
                mAudioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        sampleRate = Integer.parseInt(sampleRateProperty);
        core.startEchoTester(sampleRate);
        mEchoTesterIsRunning = true;
    }

    public void stopEchoTester() {
        Core core = LinphoneService.getCore();
        if (core == null) {
            return;
        }

        mEchoTesterIsRunning = false;
        core.stopEchoTester();
        routeAudioToEarPiece();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        Log.i("[Audio Manager] Set audio mode on 'Normal'");
    }

    public boolean getEchoTesterStatus() {
        return mEchoTesterIsRunning;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean onKeyVolumeAdjust(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            adjustVolume(1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            adjustVolume(-1);
            return true;
        }
        return false;
    }

    private void setAudioManagerInCallMode() {
        if (mAudioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
            Log.w("[Audio Manager] already in MODE_IN_COMMUNICATION, skipping...");
            return;
        }
        Log.d("[Audio Manager] Mode: MODE_IN_COMMUNICATION");

        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    private void requestAudioFocus(int stream) {
        if (!mAudioFocused) {
            int res = mAudioManager.requestAudioFocus(null, stream, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
            Log.d("[Audio Manager] Audio focus requested: " + (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ? "Granted" : "Denied"));
            if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) mAudioFocused = true;
        }
    }

    public boolean isDeviceRingtoneEnabled() {
        int readExternalStorage = mContext.getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, mContext.getPackageName());
        if (getConfig() == null) return readExternalStorage == PackageManager.PERMISSION_GRANTED;
        return getConfig().getBool("app", "device_ringtone", true) && readExternalStorage == PackageManager.PERMISSION_GRANTED;
    }

    private Core getLc() {
        if (!LinphoneService.isReady()) return null;

        return LinphoneService.getCore();
    }

    private String mBasePath;
    private static final String LINPHONE_DEFAULT_RC = "/.linphonerc";

    public String getLinphoneDefaultConfig() {
        return mBasePath + LINPHONE_DEFAULT_RC;
    }

    public Config getConfig() {
        Core core = getLc();
        if (core != null) {
            return core.getConfig();
        }

        if (!LinphoneService.isReady()) {
            File linphonerc = new File(mBasePath + "/.linphonerc");
            if (linphonerc.exists()) {
                return Factory.instance().createConfig(linphonerc.getAbsolutePath());
            } else if (mContext != null) {
                InputStream inputStream =
                        mContext.getResources().openRawResource(R.raw.linphonerc_default);
                InputStreamReader inputreader = new InputStreamReader(inputStream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                StringBuilder text = new StringBuilder();
                String line;
                try {
                    while ((line = buffreader.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                } catch (IOException ioe) {
                    Log.e(ioe);
                    AvaCrashReporter.send(ioe, "LinphoneService class, getConfig method ");
                }
                return Factory.instance().createConfigFromString(text.toString());
            }
        } else {
            return Factory.instance().createConfig(getLinphoneDefaultConfig());
        }
        return null;
    }

    public boolean isIncomingCallVibrationEnabled() {
        if (getConfig() == null) return true;
        return getConfig().getBool("app", "incoming_call_vibration", true);
    }

    public String getRingtone(String defaultRingtone) {
        String ringtone = getConfig().getString("app", "ringtone", defaultRingtone);
        if (ringtone == null || ringtone.isEmpty()) ringtone = defaultRingtone;
        return ringtone;
    }

    private synchronized void startRinging(Address remoteAddress) {
//    if (!isDeviceRingtoneEnabled()) {
//      // Enable speaker audio route, linphone library will do the ringing itself automatically
//      routeAudioToSpeaker();
//      return;
//    }

//        boolean doNotDisturbPolicyAllowsRinging =
//                Compatibility.isDoNotDisturbPolicyAllowingRinging(mContext, remoteAddress);
//        if (!doNotDisturbPolicyAllowsRinging) {
//            Log.e("[Audio Manager] Do not ring as Android Do Not Disturb Policy forbids it");
//            return;
//        }

        routeAudioToSpeaker();
        mAudioManager.setMode(MODE_RINGTONE);

        try {
            if ((mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE
                    || mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
                    && mVibrator != null
                    && isIncomingCallVibrationEnabled()) {
                long[] patern = {0, 1000, 1000};
                mVibrator.vibrate(patern, 1);
            }
            if (mRingerPlayer == null) {
                requestAudioFocus(STREAM_RING);
                mRingerPlayer = new MediaPlayer();
                mRingerPlayer.setAudioStreamType(STREAM_RING);

                String ringtone = getRingtone(Settings.System.DEFAULT_RINGTONE_URI.toString());
                try {
                    if (ringtone.startsWith("content://")) {
                        mRingerPlayer.setDataSource(mContext, Uri.parse(ringtone));
                    } else {
                        FileInputStream fis = new FileInputStream(ringtone);
                        mRingerPlayer.setDataSource(fis.getFD());
                        fis.close();
                    }
                    mRingerPlayer.prepare();
                    mRingerPlayer.setLooping(true);
                    mRingerPlayer.start();
                } catch (SecurityException ex) {
                    try {
                        mRingerPlayer.setDataSource(mContext, Uri.parse(MyApplication.SOUND + R.raw.ring));
                        mRingerPlayer.prepare();
                        mRingerPlayer.setLooping(true);
                        mRingerPlayer.start();
                    } catch (Exception e) {
                        AvaCrashReporter.send(ex, "LinphoneService class, startRinging method internal SecurityException");
                    }
                } catch (IOException e) {
                    try {
                        mRingerPlayer.setDataSource(mContext, Uri.parse(MyApplication.SOUND + R.raw.ring));
                        mRingerPlayer.prepare();
                        mRingerPlayer.setLooping(true);
                        mRingerPlayer.start();
                    } catch (IOException ex) {
                        AvaCrashReporter.send(e, "LinphoneService class, startRinging method internal IOException");
                    }
                }
            } else {
                Log.w("[Audio Manager] Already ringing");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e, "[Audio Manager] Cannot handle incoming call");
            AvaCrashReporter.send(e, "LinphoneService class, startRinging method");
        }
        mIsRinging = true;
    }

    private synchronized void stopRinging() {
        if (mRingerPlayer != null) {
            mRingerPlayer.stop();
            mRingerPlayer.release();
            mRingerPlayer = null;
        }
        if (mVibrator != null) {
            mVibrator.cancel();
        }

        mIsRinging = false;
    }

    private void routeAudioToSpeakerHelper(boolean speakerOn) {
        Log.w("[Audio Manager] Routing audio to " + (speakerOn ? "speaker" : "earpiece"));
        if (mIsBluetoothHeadsetScoConnected) {
            Log.w("[Audio Manager] [Bluetooth] Disabling bluetooth audio route");
            changeBluetoothSco(false);
        }

        mAudioManager.setSpeakerphoneOn(speakerOn);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void adjustVolume(int i) {
        if (mAudioManager.isVolumeFixed()) {
            Log.e("[Audio Manager] Can't adjust volume, device has it fixed...");
            // Keep going just in case...
        }

        int stream = STREAM_VOICE_CALL;
        if (mIsBluetoothHeadsetScoConnected) {
            Log.i("[Audio Manager] Bluetooth is connected, try to change the volume on STREAM_BLUETOOTH_SCO");
            stream = 6; // STREAM_BLUETOOTH_SCO, it's hidden...
        }

        // starting from ICS, volume must be adjusted by the application,
        // at least for STREAM_VOICE_CALL volume stream
        mAudioManager.adjustStreamVolume(
                stream,
                i < 0 ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_SHOW_UI);
    }

    // Bluetooth

    public synchronized void bluetoothHeadsetConnectionChanged(boolean connected) {
        mIsBluetoothHeadsetConnected = connected;
        mAudioManager.setBluetoothScoOn(connected);
//        if (LinphoneService.isReady()) LinphoneService.getCallManager().refreshInCallActions();
    }

    public synchronized void bluetoothHeadetAudioConnectionChanged(boolean connected) {
        mIsBluetoothHeadsetScoConnected = connected;
        mAudioManager.setBluetoothScoOn(connected);
    }

    public synchronized boolean isBluetoothHeadsetConnected() {
        return mIsBluetoothHeadsetConnected;
    }

    public synchronized void bluetoothHeadetScoConnectionChanged(boolean connected) {
        mIsBluetoothHeadsetScoConnected = connected;
//        if (LinphoneContext.isReady()) LinphoneManager.getCallManager().refreshInCallActions();
    }

    public synchronized boolean isUsingBluetoothAudioRoute() {
        return mIsBluetoothHeadsetScoConnected;
    }

    public synchronized void routeAudioToBluetooth() {
        if (!isBluetoothHeadsetConnected()) {
            Log.w("[Audio Manager] [Bluetooth] No headset connected");
            return;
        }
        if (mAudioManager.getMode() != AudioManager.MODE_IN_COMMUNICATION) {
            Log.w("[Audio Manager] [Bluetooth] Changing audio mode to MODE_IN_COMMUNICATION and requesting STREAM_VOICE_CALL focus");
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            requestAudioFocus(STREAM_VOICE_CALL);
        }
        changeBluetoothSco(true);
    }

    private synchronized void changeBluetoothSco(final boolean enable) {
        // IT WILL TAKE A CERTAIN NUMBER OF CALLS TO EITHER START/STOP BLUETOOTH SCO FOR IT TO WORK
        if (enable && mIsBluetoothHeadsetScoConnected) {
            Log.i("[Audio Manager] [Bluetooth] SCO already enabled, skipping");
            return;
        } else if (!enable && !mIsBluetoothHeadsetScoConnected) {
            Log.i("[Audio Manager] [Bluetooth] SCO already disabled, skipping");
            return;
        }

        new Thread() {
            @Override
            public void run() {
                Log.i("[Audio Manager] [Bluetooth] SCO start/stop thread started");
                boolean resultAcknowledged;
                int retries = 0;

                do {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Log.e(e);
                        AvaCrashReporter.send(e, "LinphoneService class, changeBluetoothSco method");
                    }

                    synchronized (LinphoneService.this) {
                        if (enable) {
                            Log.i("[Audio Manager] [Bluetooth] Starting SCO: try number " + retries);
                            mAudioManager.startBluetoothSco();
                        } else {
                            Log.i("[Audio Manager] [Bluetooth] Stopping SCO: try number " + retries);
                            mAudioManager.stopBluetoothSco();
                        }
                        resultAcknowledged = isUsingBluetoothAudioRoute() == enable;
                        retries++;
                    }
                } while (!resultAcknowledged && retries < 10);
            }
        }.start();
    }

    public void bluetoothAdapterStateChanged() {
        try {
            if (mBluetoothAdapter.isEnabled()) {
                Log.i("[Audio Manager] [Bluetooth] Adapter enabled");
                mIsBluetoothHeadsetConnected = false;
                mIsBluetoothHeadsetScoConnected = false;

                BluetoothProfile.ServiceListener bluetoothServiceListener = new BluetoothProfile.ServiceListener() {
                    public void onServiceConnected(int profile, BluetoothProfile proxy) {
                        if (profile == BluetoothProfile.HEADSET) {
                            Log.i("[Audio Manager] [Bluetooth] HEADSET profile connected");
                            mBluetoothHeadset = (BluetoothHeadset) proxy;

                            List<BluetoothDevice> devices =
                                    mBluetoothHeadset.getConnectedDevices();
                            if (devices.size() > 0) {
                                Log.i(
                                        "[Audio Manager] [Bluetooth] A device is already connected");
                                bluetoothHeadsetConnectionChanged(true);
                            }

                            Log.i("[Audio Manager] [Bluetooth] Registering bluetooth receiver");

                            IntentFilter filter = new IntentFilter();
                            filter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
                            filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
                            filter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
                            filter.addAction(
                                    BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT);

                            Intent sticky =
                                    mContext.registerReceiver(mBluetoothReceiver, filter);
                            Log.i("[Audio Manager] [Bluetooth] Bluetooth receiver registered");
                            int state =
                                    sticky.getIntExtra(
                                            AudioManager.EXTRA_SCO_AUDIO_STATE,
                                            AudioManager.SCO_AUDIO_STATE_DISCONNECTED);
                            if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
                                Log.i(
                                        "[Audio Manager] [Bluetooth] Bluetooth headset SCO connected");
                                bluetoothHeadetScoConnectionChanged(true);
                            } else if (state == AudioManager.SCO_AUDIO_STATE_DISCONNECTED) {
                                Log.i(
                                        "[Audio Manager] [Bluetooth] Bluetooth headset SCO disconnected");
                                bluetoothHeadetScoConnectionChanged(false);
                            } else if (state == AudioManager.SCO_AUDIO_STATE_CONNECTING) {
                                Log.i(
                                        "[Audio Manager] [Bluetooth] Bluetooth headset SCO connecting");
                            } else if (state == AudioManager.SCO_AUDIO_STATE_ERROR) {
                                Log.i(
                                        "[Audio Manager] [Bluetooth] Bluetooth headset SCO connection error");
                            } else {
                                Log.w(
                                        "[Audio Manager] [Bluetooth] Bluetooth headset unknown SCO state changed: "
                                                + state);
                            }
                        }
                    }

                    public void onServiceDisconnected(int profile) {
                        if (profile == BluetoothProfile.HEADSET) {
                            Log.i("[Audio Manager] [Bluetooth] HEADSET profile disconnected");
                            mBluetoothHeadset = null;
                            mIsBluetoothHeadsetConnected = false;
                            mIsBluetoothHeadsetScoConnected = false;
                        }
                    }
                };

                mBluetoothAdapter.getProfileProxy(mContext, bluetoothServiceListener, BluetoothProfile.HEADSET);
            } else {
                Log.w("[Audio Manager] [Bluetooth] Adapter disabled");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "Bluetooth receiver Crash");
        }
    }

    private void startBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            Log.i("[Audio Manager] [Bluetooth] Adapter found");
            if (mAudioManager.isBluetoothScoAvailableOffCall()) {
                Log.i("[Audio Manager] [Bluetooth] SCO available off call, continue");
            } else {
                Log.w("[Audio Manager] [Bluetooth] SCO not available off call !");
            }

            mBluetoothReceiver = new BluetoothReceiver();
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            mContext.registerReceiver(mBluetoothReceiver, filter);

            bluetoothAdapterStateChanged();
        }
    }

    // HEADSET

    private void enableHeadsetReceiver() {
        mHeadsetReceiver = new HeadsetReceiver();

        Log.i("[Audio Manager] Registering headset receiver");
        mContext.registerReceiver(mHeadsetReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        mContext.registerReceiver(mHeadsetReceiver, new IntentFilter(AudioManager.ACTION_HEADSET_PLUG));
        mHeadsetReceiverRegistered = true;
    }
}
