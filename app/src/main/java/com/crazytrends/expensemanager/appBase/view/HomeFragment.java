package com.crazytrends.expensemanager.appBase.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.baseClass.BaseFragmentBinding;
import com.crazytrends.expensemanager.databinding.FragmentMainBinding;

public class HomeFragment extends BaseFragmentBinding {
    private FragmentMainBinding binding;


    public void initMethods() {
    }

    public void onClick(View view) {
    }


    public void setOnClicks() {
    }


    public void setToolbar() {
    }


    public View getViewBinding() {
        return this.binding.getRoot();
    }


    public void setBinding(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        this.binding = (FragmentMainBinding) DataBindingUtil.inflate(layoutInflater, R.layout.fragment_main_act, viewGroup, false);
    }
}
