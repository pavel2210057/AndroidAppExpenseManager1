package com.crazytrends.expensemanager.appBase.baseClass;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.View.OnClickListener;

import com.crazytrends.expensemanager.BaseActivity;
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate;
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl;

import java.util.Locale;

public abstract class BaseActivityRecyclerBinding extends BaseActivity implements OnClickListener {
    public Context context;
    private static LocaleHelperActivityDelegate localeDelegate  = new LocaleHelperActivityDelegateImpl();


    public abstract void callApi();


    public abstract void fillData();


    public abstract void initMethods();


    public abstract void setBinding();


    public abstract void setOnClicks();


    public abstract void setRecycler();


    public abstract void setToolbar();


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.context = this;
        setBinding();
        setToolbar();
        callApi();
        fillData();
        setRecycler();
        setOnClicks();
        initMethods();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(localeDelegate.attachBaseContext(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        localeDelegate.onResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        localeDelegate.onPaused();
    }

    public static void updateLocale(Locale locale , Activity activity){
        localeDelegate.setLocale(activity, locale);
    }
}
