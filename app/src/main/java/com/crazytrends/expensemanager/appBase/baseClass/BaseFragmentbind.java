package com.crazytrends.expensemanager.appBase.baseClass;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public abstract class BaseFragmentbind  extends Fragment implements View.OnClickListener {
    public Context context;


    public abstract void callApi();


    public abstract View getViewBinding();


    public abstract void fillData();


    public abstract void initMethods();


    public abstract void setBinding(LayoutInflater layoutInflater, ViewGroup viewGroup);


    public abstract void setOnClicks();


    public abstract void setRecycler();


    public abstract void setToolbar();
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }


    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.context = getContext();
        setBinding(layoutInflater, viewGroup);
        setToolbar();
        callApi();
        fillData();
        setRecycler();
        setOnClicks();
        initMethods();
        return getViewBinding();
    }

}