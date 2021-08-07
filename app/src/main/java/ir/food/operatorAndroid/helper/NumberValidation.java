package ir.food.operatorAndroid.helper;

/**
 * Created by mohsen on 16/10/2016.
 */
public class NumberValidation {

    public static boolean isValid(String mobile) {
        if (mobile == null) return false;
        if (mobile.isEmpty()) return false;
//    if (mobile.length()<10) return false;
        if (mobile.startsWith("0")) {
            mobile = mobile.substring(1);
        }
        String regEx = "9[0-9]{9}$";
        return mobile.matches(regEx);
    }

    public static String removePrefix(String mobileNumber) {
        mobileNumber = mobileNumber.trim();
        if (mobileNumber.startsWith("0098")) {
            return mobileNumber.substring(4);
        } else if (mobileNumber.startsWith("+98") || mobileNumber.startsWith("098")) {
            return mobileNumber.substring(3);
        } else if (mobileNumber.startsWith("00") || mobileNumber.startsWith("98")) {
            return mobileNumber.substring(2);
        } else if (mobileNumber.startsWith("0")) {
            return mobileNumber.substring(1);
        }
        return mobileNumber;
    }

    public static boolean havePrefix(String mobileNumber) {
        mobileNumber = mobileNumber.trim();
        if (mobileNumber.startsWith("0098")) {
            return true;
        } else if (mobileNumber.startsWith("+98") || mobileNumber.startsWith("098")) {
            return true;
        } else if (mobileNumber.startsWith("00") || mobileNumber.startsWith("98")) {
            return true;
        } else if (mobileNumber.startsWith("0")) {
            return true;
        }
        return false;
    }

    public static String addZeroFirst(String mobile) {
        if (mobile.startsWith("0")) {
            return mobile;
        } else {
            return "0" + mobile;
        }
    }

}
