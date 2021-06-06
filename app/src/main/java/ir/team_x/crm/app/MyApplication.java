package ir.team_x.crm.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ir.team_x.crm.BuildConfig;
import ir.team_x.crm.R;
import ir.team_x.crm.helper.TypefaceUtil;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    public static Context context;
    public static Activity currentActivity;
    public static Handler handler;
    private static final String IRANSANS = "fonts/IRANSans.otf";
    private static final String IRANSANS_MEDUME = "fonts/IRANSANSMOBILE_MEDIUM.TTF";
    public static final String IRANSANS_BOLD = "fonts/IRANSANSMOBILE_BOLD.TTF";
    private static final String IRANSANS_LIGHT = "fonts/IRANSANSMOBILE_LIGHT.TTF";
    public static Typeface iranSance;
    public static Typeface IraSanSMedume;
    public static Typeface IraSanSBold;
    public static Typeface IraSanSLight;
    public static PrefManager prefManager;
    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String DIR_DOWNLOAD;
    public static String DIR_ROOT;
    public static final String VOICE_FOLDER_NAME = "voice";
    public static FragmentManager fragmentManagerV4;
    public static final String SOUND = "android.resource://ir.taxi1880.operatormanagement/";
    public static final String image_path_save = DIR_SDCARD + "/operatormanagement/Image/";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();
        initTypeface();

        prefManager = new PrefManager(context);
        DIR_ROOT = DIR_SDCARD + "/Android/data/" + context.getPackageName() + "/";
        DIR_DOWNLOAD = DIR_SDCARD + "/Android/data/" + context.getPackageName() + "/files/";
        String languageToLoad = "fa_";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        initACRA();
    }

    private void initACRA() {
        Map<String, String> authHeaderMap = new HashMap<>();
        authHeaderMap.put("Authorization", MyApplication.prefManager.getAuthorization());
        authHeaderMap.put("id_token", MyApplication.prefManager.getIdToken());

        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this)
                .setBuildConfigClass(BuildConfig.class)
                .setReportFormat(StringFormat.JSON);

        HttpSenderConfigurationBuilder httpPluginConfigBuilder
                = builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class)
                .setUri(EndPoints.ACRA_PATH)
                .setHttpMethod(HttpSender.Method.POST)
                .setHttpHeaders(authHeaderMap)
                .setEnabled(true);
//        if (!BuildConfig.DEBUG)
        ACRA.init(this, builder);


    }

    private void initTypeface() {
        iranSance = Typeface.createFromAsset(getAssets(), IRANSANS);
        IraSanSMedume = Typeface.createFromAsset(getAssets(), IRANSANS_MEDUME);
        IraSanSLight = Typeface.createFromAsset(getAssets(), IRANSANS_LIGHT);
        IraSanSBold = Typeface.createFromAsset(getAssets(), IRANSANS_BOLD);
    }

    public static void ErrorToast(String message, int duration) {
//    LayoutInflater inflater = LayoutInflater.from(currentActivity);
//    View v = inflater.inflate(R.layout.toast,null,false);
//    TypefaceUtil.overrideFonts(v);
//
//    TextView text = (TextView) v.findViewById(R.id.text);
//    text.setText(message);
//
//    Toast toast = new Toast(currentActivity);
//    toast.setGravity(Gravity.BOTTOM, 0, 0);
//    toast.setDuration(duration);
//    toast.setView(v);
//    toast.show();
    }

    public static void Toast(String message, int duration) {
        LayoutInflater layoutInflater = LayoutInflater.from(currentActivity);
        View v = layoutInflater.inflate(R.layout.item_toast, null);
        TypefaceUtil.overrideFonts(v);
        TextView text = (TextView) v.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(currentActivity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);

        toast.setView(v);
        toast.show();
    }

//  public static void configureAccount() {
//    try {
//      Core core = LinphoneService.getCore();
//      core.clearAllAuthInfo();
//      core.clearProxyConfig();
//
//      // No account configured, we display the configuration activity
//      AccountCreator mAccountCreator = LinphoneService.getCore().createAccountCreator(null);
//
//      mAccountCreator.setDomain((BuildConfig.DEBUG)
////              ? "172.16.2.216:4060"
//              ? prefManager.getSipServer()
//              : prefManager.getSipServer());
//      mAccountCreator.setUsername(prefManager.getSipNumber() + "");
//      mAccountCreator.setPassword(prefManager.getSipPassword());
//      mAccountCreator.setTransport(TransportType.Udp);
//
//      // This will automatically create the proxy config and auth info and add them to the Core
//      ProxyConfig cfg = mAccountCreator.createProxyConfig();
//
//      // Make sure the newly created one is the default
//      core.setDefaultProxyConfig(cfg);
//
//      // At least the 3 below values are required
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//
//  }


}
