package com.crazytrends.expensemanager.appBase.view;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;

public class ExpenseDisclosure extends AppCompatActivity implements OnClickListener {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_disclosure);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.agreeAndContinue) {
            AppPref.setIsTermsAccept(this, true);
            goToMainScreen();
        } else if (id == R.id.privacyPolicy) {
            AppConstants.openUrl(this, Constants.PRIVACY_POLICY_URL);
        } else if (id == R.id.userAgreement) {
            agreeAndContinueDialog();
        }
    }

    public void agreeAndContinueDialog() {
        Builder builder = new Builder(this);
        builder.setMessage(Constants.DISCLOSURE_DIALOG_DESC);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private void goToMainScreen() {
        try {
            if (!AppPref.isFirstLaunch(this)) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
