package com.crazytrends.expensemanager.appBase.appPref;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import com.crazytrends.expensemanager.appBase.utils.Constants;

public class AppPref {
    static final String CURRENCY_NAME = "CURRENCY_NAME";
    static final String CURRENCY_SYMBOL = "CURRENCY_SYMBOL";
    static final String DATE_FORMAT = "DATE_FORMAT";
    static final String IS_ADFREE = "IS_ADFREE";
    static final String IS_DEFAULT_INSERTED = "IS_DEFAULT_INSERTED";
    static final String IS_ENABLE_CUSTOM_DEBUG_LOGS = "isEnableDebugLog";
    static final String IS_ENABLE_CUSTOM_DEBUG_TOAST = "isEnableDebugToast";
    static final String IS_FIRST_LAUNCH = "isFirstLaunch";
    static final String IS_RATE_US = "IS_RATE_US";
    static final String IS_TERMS_ACCEPT = "IS_TERMS_ACCEPT";
    static final String MyPref = "userPref";
    static final String NEVER_SHOW_RATTING_DIALOG = "isNeverShowRatting";
    static final String TIME_FORMAT = "TIME_FORMAT";

    public static boolean getIsAdfree(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_ADFREE, false);
    }

    public static void setIsAdfree(Context context, boolean z) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_ADFREE, z);
        edit.commit();
    }

    public static boolean IsRateUS(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_RATE_US, false);
    }

    public static void setIsRateUS(Context context, boolean z) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_RATE_US, z);
        edit.commit();
    }

    public static void setEnableDebugLog(Context context, boolean z) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_ENABLE_CUSTOM_DEBUG_LOGS, z);
        edit.commit();
    }

    public static boolean isEnableDebugLog(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_ENABLE_CUSTOM_DEBUG_LOGS, true);
    }

    public static void setEnableDebugToast(Context context, boolean z) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_ENABLE_CUSTOM_DEBUG_TOAST, z);
        edit.commit();
    }

    public static boolean isEnableDebugToast(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_ENABLE_CUSTOM_DEBUG_TOAST, false);
    }

    public static void setNeverShowRatting(Context context, boolean z) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(NEVER_SHOW_RATTING_DIALOG, z);
        edit.commit();
    }

    public static boolean isNeverShowRatting(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(NEVER_SHOW_RATTING_DIALOG, false);
    }

    public static void setFirstLaunch(Context context, boolean z) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_FIRST_LAUNCH, z);
        edit.commit();
    }

    public static boolean isFirstLaunch(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_FIRST_LAUNCH, true);
    }

    public static boolean IsTermsAccept(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_TERMS_ACCEPT, false);
    }

    public static void setIsTermsAccept(Context context, boolean z) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_TERMS_ACCEPT, z);
        edit.commit();
    }

    public static void setDefaultInserted(Context context, boolean z) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_DEFAULT_INSERTED, z);
        edit.commit();
    }

    public static boolean isDefaultInserted(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_DEFAULT_INSERTED, false);
    }

    public static void setCurrencySymbol(Context context, String str) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(CURRENCY_SYMBOL, str);
        edit.commit();
    }

    public static String getCurrencySymbol(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getString(CURRENCY_SYMBOL, "$");
    }

    public static void setCurrencyName(Context context, String str) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(CURRENCY_NAME, str);
        edit.commit();
    }

    public static String getCurrencyName(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getString(CURRENCY_NAME, "USD");
    }

    public static void setDateFormat(Context context, String str) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(DATE_FORMAT, str);
        edit.commit();
    }

    public static String getDateFormat(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getString(DATE_FORMAT, Constants.showDatePatternDDMMMMYYYY);
    }

    public static void setTimeFormat(Context context, String str) {
        Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(TIME_FORMAT, str);
        edit.commit();
    }

    public static String getTimeFormat(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getString(TIME_FORMAT, "hh:mm a");
    }
}
