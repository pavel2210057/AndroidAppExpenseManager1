package com.crazytrends.expensemanager.appBase.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.baseClass.BaseActivityBinding;
import com.crazytrends.expensemanager.appBase.utils.OnFragmentInteractionListener;
import com.crazytrends.expensemanager.databinding.ActivityMainPagerBinding;
import com.crazytrends.expensemanager.databinding.RowCustomTabBinding;

public class MainActPager extends BaseActivityBinding implements OnFragmentInteractionListener {

    public ActivityMainPagerBinding binding;
    private Fragment currentFragment = null;
    public ImageView imgAdd;
    public boolean isUpdateFragList = false;
    private FragmentPagerAdapterClass pagerAdapter;

    private class FragmentPagerAdapterClass extends FragmentPagerAdapter {
        int TabCount;

        private FragmentPagerAdapterClass(FragmentManager fragmentManager, int i) {
            super(fragmentManager);
            this.TabCount = i;
        }

        public Fragment getItem(int i) {
            return new DemoListFragment();
        }

        public int getCount() {
            return this.TabCount;
        }
    }

    public void onFragmentInteraction(Uri uri) {
    }


    public void setOnClicks() {
    }


    public void setBinding() {
        this.binding = (ActivityMainPagerBinding) DataBindingUtil.setContentView(this, R.layout.act_main_pager);
    }


    public void setToolbar() {
        this.imgAdd = this.binding.imgAdd;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.imgBack) {
            onBackPressedAd();
        }
    }


    public void initMethods() {
        setTabLayout();
    }

    public void onBackPressed() {
        onBackPressedAd();
    }

    private void onBackPressedAd() {
        finish();
    }

    private void setTabLayout() {
        RowCustomTabBinding rowCustomTabBinding = (RowCustomTabBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.row_custom_tab_act, null, false);
        rowCustomTabBinding.imgIcon.setImageResource(R.drawable.add);
        rowCustomTabBinding.textTitle.setText(R.string.drawerTitleHome);
        this.binding.tabLayout.addTab(this.binding.tabLayout.newTab().setCustomView(rowCustomTabBinding.getRoot()));
        RowCustomTabBinding rowCustomTabBinding2 = (RowCustomTabBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.row_custom_tab_act, null, false);
        rowCustomTabBinding2.imgIcon.setImageResource(R.drawable.add);
        rowCustomTabBinding2.textTitle.setText(R.string.drawerTitleDemoList);
        this.binding.tabLayout.addTab(this.binding.tabLayout.newTab().setCustomView(rowCustomTabBinding2.getRoot()));
        RowCustomTabBinding rowCustomTabBinding3 = (RowCustomTabBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.row_custom_tab_act, null, false);
        rowCustomTabBinding3.imgIcon.setImageResource(R.drawable.add);
        rowCustomTabBinding3.textTitle.setText(R.string.drawerTitleAbout);
        this.binding.tabLayout.addTab(this.binding.tabLayout.newTab().setCustomView(rowCustomTabBinding3.getRoot()));
        this.binding.tabLayout.setTabGravity(0);
        this.pagerAdapter = new FragmentPagerAdapterClass(getSupportFragmentManager(), this.binding.tabLayout.getTabCount());
        this.binding.viewPager.setAdapter(this.pagerAdapter);
        this.binding.viewPager.setOffscreenPageLimit(this.binding.tabLayout.getTabCount());
        this.binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this.binding.tabLayout));
        this.binding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabReselected(TabLayout.Tab tab) {
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabSelected(TabLayout.Tab tab) {
                MainActPager.this.binding.viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    MainActPager.this.setToolbarAndMenu(MainActPager.this.getString(R.string.app_name), false);
                }
            }
        });
    }


    @SuppressLint("WrongConstant")
    public void setToolbarAndMenu(String str, boolean z) {
        this.binding.textTitle.setText(str);
        this.binding.imgAdd.setVisibility(z ? 0 : 8);
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }
}
