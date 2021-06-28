package ir.team_x.crm.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by mohsen on 18/10/2016.
 */
public class AppVersionHelper {

    private static final String TAG = AppVersionHelper.class.getSimpleName();
    Context context;
    PackageInfo pInfo = null;

    public AppVersionHelper(Context context) {
        this.context = context;
        initiate();
    }

    private void initiate() {
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getVerionCode() {
        return pInfo.versionCode;
    }

    public String getVerionName() {
        return pInfo.versionName;
    }


}
