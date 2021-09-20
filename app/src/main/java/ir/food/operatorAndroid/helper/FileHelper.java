package ir.food.operatorAndroid.helper;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ir.food.operatorAndroid.app.MyApplication;

public class FileHelper {

    public static void openFile(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (getFileName(uri).contains(".doc") || getFileName(uri).contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (getFileName(uri).contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (getFileName(uri).contains(".ppt") || getFileName(uri).contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (getFileName(uri).contains(".xls") || getFileName(uri).contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (getFileName(uri).contains(".jpg") || getFileName(uri).contains(".jpeg") || getFileName(uri).contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/*");
        } else if (getFileName(uri).contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (getFileName(uri).contains(".apk")) {
            // Apk file
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            //if you want you can also define the intent type for any other file
            intent.setDataAndType(uri, "*/*");
        }

        MyApplication.currentActivity.startActivity(intent);
    }

    public static void openFile(File file) throws IOException {
        openFile(Uri.fromFile(file));
    }

    public static String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = MyApplication.context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static boolean isImage(String type) {
        if (type.equals("jpg") || type.equals("jpeg") || type.equals("png") || type.equals("JPEG") || type.equals("PNG") || type.equals("JPG")) {
            return true;
        }
        return false;

    }


    public static void copyFile(String inputPath, String fileName, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + fileName);
            out = new FileOutputStream(outputPath + fileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static void deleteFile(String inputPath, String fileName) {
        try {
            // delete the original file
            new File(inputPath + fileName).delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public static void moveFile(String inputPath, String fileName, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + fileName);
            out = new FileOutputStream(outputPath + fileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + fileName).delete();


        } catch (Exception fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
    }
}
