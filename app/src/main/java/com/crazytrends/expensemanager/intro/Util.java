package com.crazytrends.expensemanager.intro;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.view.View;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    public static boolean IS_DEBUG = false;
    public static boolean IS_DISPLAY_ADS = true;

    public static String md5_Hash(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            messageDigest = null;
        }
        messageDigest.update(str.getBytes(), 0, str.length());
        return new BigInteger(1, messageDigest.digest()).toString(16);
    }

    public static String getDeviceID(Context context) {
        return md5_Hash(Secure.getString(context.getContentResolver(), "android_id")).toUpperCase();
    }

}
