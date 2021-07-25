package ir.food.operatorAndroid.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.StringRes;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.food.operatorAndroid.app.MyApplication;

/**
 * Created by AmirReza on 28/05/2017.
 *
 * @version : 2
 */

public class StringHelper {

    public static void setCommaOnTime(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            boolean isEnable;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isEnable) return;
                isEnable = true;

                String temp = editable.toString();
                Log.i("LOG", "afterTextChanged: " + temp);
                temp = StringHelper.toEnglishDigits(temp);
                temp = extractTheNumber(temp);

                if (temp.isEmpty())
                    temp = "0";
                double amount = Double.parseDouble(temp);
                DecimalFormat formatter = new DecimalFormat("#,###");
                editText.setText(formatter.format(amount));
                editText.setSelection(editText.getText().toString().length());

                isEnable = false;

            }
        });

    }

    public static void setCharAfterOnTime(final EditText editText, String c, int cnt) {
        editText.addTextChangedListener(new TextWatcher() {
            boolean isEnable;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (isEnable) return;
                isEnable = true;

                String temp = StringHelper.toPersianDigits(s.toString());

                temp = temp.replaceAll(c, "");
                temp = temp.replaceAll("(.{" + cnt + "})", "$0" + c);
                temp = temp.replaceAll("(" + c + ")$", "");

                editText.setText(temp);
                editText.setSelection(editText.getText().toString().length());

                isEnable = false;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public static String setComma(final String string) {
        double amount = Double.parseDouble(string);
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    /**
     * @param content   your content
     * @param sample    A string to repeat in the content that was submitted (s)
     * @param frequency Repeat frequency
     *                  Example :
     *                  input : setCharAfter("60345216354354"," - ",4)
     *                  output : 6034 - 5216 - 3543 - 54
     * @return
     */
    public static String setCharAfter(final String content, String sample, int frequency) {
        String temp = content;
        temp = temp.replaceAll(sample, "");
        temp = temp.replaceAll("(.{" + frequency + "})", "$0" + sample);
        temp = temp.replaceAll("(" + sample + ")$", "");
        return temp;
    }

    public static String extractTheNumber(String v) {
        v = toEnglishDigits(v);
        Matcher m = Pattern.compile("[0-9]").matcher(v);
        String str = "";
        while (m.find()) {
            str += m.group();
        }
        return str;
    }

    public static boolean isNumeric(String str) {
        if (str == null) return false;

        str = toEnglishDigits(str);
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public static boolean charCheck(char input_char) {
        // CHECKING FOR ALPHABET
        if ((input_char >= 65 && input_char <= 90) || (input_char >= 97 && input_char <= 122))
            return true;

            // CHECKING FOR DIGITS
        else if (input_char >= 48 && input_char <= 57)
            return true;

            // OTHERWISE SPECIAL CHARACTER
        else
            return false;
    }

    public static String toEnglishDigits(String eng) {

        if (eng == null) return "";
        char[] chars = {'٩', '٨', '٧', '٦', '٥', '٤', '٣', '٢', '١', '٠', '،'};

        char[] persian = {'۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹', '،'};
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < eng.length(); ++i) {
            if (eng.charAt(i) == chars[0] || eng.charAt(i) == persian[0]) {
                builder.append("0");
                continue;
            }
            if (eng.charAt(i) == chars[1] || eng.charAt(i) == persian[1]) {
                builder.append("1");
                continue;
            }
            if (eng.charAt(i) == chars[2] || eng.charAt(i) == persian[2]) {
                builder.append("2");
                continue;
            }
            if (eng.charAt(i) == chars[3] || eng.charAt(i) == persian[3]) {
                builder.append("3");
                continue;
            }
            if (eng.charAt(i) == chars[4] || eng.charAt(i) == persian[4]) {
                builder.append("4");
                continue;
            }
            if (eng.charAt(i) == chars[5] || eng.charAt(i) == persian[5]) {
                builder.append("5");
                continue;
            }
            if (eng.charAt(i) == chars[6] || eng.charAt(i) == persian[6]) {
                builder.append("6");
                continue;
            }
            if (eng.charAt(i) == chars[7] || eng.charAt(i) == persian[7]) {
                builder.append("7");
                continue;
            }
            if (eng.charAt(i) == chars[8] || eng.charAt(i) == persian[8]) {
                builder.append("8");
                continue;
            }
            if (eng.charAt(i) == chars[9] || eng.charAt(i) == persian[9]) {
                builder.append("9");
                continue;
            }
            if (eng.charAt(i) == chars[10] || eng.charAt(i) == persian[10]) {
                builder.append(",");
                continue;
            }
            builder.append(eng.charAt(i));
        }
        return builder.toString();
    }

    public static String toPersianDigits(String eng) {

        if (eng == null) return "";
        char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < eng.length(); ++i) {
            if (eng.charAt(i) == chars[0]) {
                builder.append("۰");
                continue;
            }
            if (eng.charAt(i) == chars[1]) {
                builder.append("۱");
                continue;
            }
            if (eng.charAt(i) == chars[2]) {
                builder.append("۲");
                continue;
            }
            if (eng.charAt(i) == chars[3]) {
                builder.append("۳");
                continue;
            }
            if (eng.charAt(i) == chars[4]) {
                builder.append("۴");
                continue;
            }
            if (eng.charAt(i) == chars[5]) {
                builder.append("۵");
                continue;
            }
            if (eng.charAt(i) == chars[6]) {
                builder.append("۶");
                continue;
            }
            if (eng.charAt(i) == chars[7]) {
                builder.append("۷");
                continue;
            }
            if (eng.charAt(i) == chars[8]) {
                builder.append("۸");
                continue;
            }
            if (eng.charAt(i) == chars[9]) {
                builder.append("۹");
                continue;
            }
            builder.append(eng.charAt(i));
        }
        return builder.toString();
    }

    public static int roundTwoLastDigitUp(int number) {
        int temp = number % 100;
        int roundedNumber = number - temp;
        roundedNumber += 100;
        return roundedNumber;
    }

    public static String getFileName(String filePath) {
        String[] strings = filePath.split("/");
        String filename = strings[strings.length - 1];
        return filename;
    }

    public static String getString(@StringRes int id) {
        String res = "";
        try {
            res = MyApplication.context.getResources().getString(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}
