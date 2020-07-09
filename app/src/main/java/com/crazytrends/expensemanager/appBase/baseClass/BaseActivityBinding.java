package com.crazytrends.expensemanager.appBase.baseClass;

import android.content.Context;
import android.os.Bundle;

import android.view.View.OnClickListener;

import com.crazytrends.expensemanager.BaseActivity;


public abstract class BaseActivityBinding extends BaseActivity implements OnClickListener {
    public Context context;


    public abstract void initMethods();


    public abstract void setBinding();


    public abstract void setOnClicks();


    public abstract void setToolbar();


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.context = this;
        setBinding();
        setToolbar();
        setOnClicks();
        initMethods();
    }

}
