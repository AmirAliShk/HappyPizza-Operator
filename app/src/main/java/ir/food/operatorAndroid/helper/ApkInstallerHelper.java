package ir.food.operatorAndroid.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

import ir.food.operatorAndroid.app.MyApplication;

public class ApkInstallerHelper {
//    private static final String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/efspco";

    public static void install(Activity activity, String fileName) {
        File file = new File(MyApplication.DIR_MAIN_FOLDER + MyApplication.UPDATE_FOLDER_NAME + fileName);
        Log.i("LOG AMIR", "install: " + file.getPath());
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String type = "application/vnd.android.package-archive";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri downloadedApk = FileProvider.getUriForFile(activity, "ir.food.operatorAndroid", file);//todo
                intent.setDataAndType(downloadedApk, type);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(file), type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "ّFile not found!", Toast.LENGTH_SHORT).show();
        }
    }
}
