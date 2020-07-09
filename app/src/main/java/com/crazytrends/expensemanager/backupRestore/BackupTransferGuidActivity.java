package com.crazytrends.expensemanager.backupRestore;


import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.databinding.ActivityBackupTransferGuidBinding;

public class BackupTransferGuidActivity extends AppCompatActivity implements OnClickListener {

    public ActivityBackupTransferGuidBinding binding;
    private String strUrl = "file:///android_asset/info.html";
    public ToolbarModel toolbarModel;

    private class MyCustomizedWebClient extends WebViewClient {
        public void onReceivedHttpAuthRequest(WebView webView, HttpAuthHandler httpAuthHandler, String str, String str2) {
        }

        private MyCustomizedWebClient() {
        }

        public void onPageFinished(WebView webView, String str) {
            BackupTransferGuidActivity.this.binding.progress.setVisibility(8);
            super.onPageFinished(webView, str);
        }
    }

    class backInWB implements OnKeyListener {
        backInWB() {
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == 0) {
                WebView webView = (WebView) view;
                if (i == 4 && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
            }
            return false;
        }
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.binding = (ActivityBackupTransferGuidBinding) DataBindingUtil.setContentView(this, R.layout.activity_backup_transfer_guid);
        setToolbar();
        setOnClicks();
        initMethods();
    }


    public void setToolbar() {
        this.toolbarModel = new ToolbarModel();
        this.toolbarModel.setTitle(getString(R.string.backup_transfer_guid));
        this.binding.includedToolbar.setToolbarModel(this.toolbarModel);
    }

    private void setOnClicks() {
        this.binding.includedToolbar.imgBack.setOnClickListener(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.imgBack) {
            onBackPressed();
        }
    }

    private void initMethods() {
        loadUrl();
    }

    public void loadUrl() {
        try {
            WebSettings settings = this.binding.privacyWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            this.binding.progress.setVisibility(0);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            this.binding.privacyWebView.setOnKeyListener(new backInWB());
            this.binding.privacyWebView.setWebViewClient(new MyCustomizedWebClient());
            this.binding.privacyWebView.loadUrl(this.strUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        try {
            if (this.binding.privacyWebView == null) {
                return;
            }
            if (this.binding.privacyWebView.canGoBack()) {
                this.binding.privacyWebView.goBack();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
