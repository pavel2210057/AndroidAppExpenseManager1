package com.crazytrends.expensemanager.appBase.view;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.adapter.CustomSpinnerAdapter;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.appBase.baseClass.BaseFragmentSetting;
import com.crazytrends.expensemanager.appBase.models.spinner.SpinnerRowModel;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import com.crazytrends.expensemanager.appBase.utils.TwoButtonDialogListener;
import com.crazytrends.expensemanager.backupRestore.BackupRestore;
import com.crazytrends.expensemanager.backupRestore.BackupRestoreProgress;
import com.crazytrends.expensemanager.backupRestore.BackupTransferGuidAct;
import com.crazytrends.expensemanager.backupRestore.OnBackupRestore;
//import com.crazytrends.expensemanager.backupRestore.RemoteBackup;
import com.crazytrends.expensemanager.backupRestore.RestoreDriveListAct;
import com.crazytrends.expensemanager.backupRestore.RestoreListAct;
import com.crazytrends.expensemanager.backupRestore.RestoreRowModel;
import com.crazytrends.expensemanager.dailyAlarm.NotificationPublisher;
import com.crazytrends.expensemanager.databinding.ActivitySettingBinding;
import com.crazytrends.expensemanager.databinding.AlertDialogBackupBinding;
import com.crazytrends.expensemanager.pdfReport.ReportsListAct;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog.Builder;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;
import pub.devrel.easypermissions.EasyPermissions.RationaleCallbacks;

public class SettingActivity extends BaseFragmentSetting implements PermissionCallbacks, RationaleCallbacks {
    private BackupRestore backupRestore;
    private ActivitySettingBinding binding;

    public ArrayList<SpinnerRowModel> currencyList;

    public ArrayList<SpinnerRowModel> dateFormatList;

    public Dialog dialogBackup;

    public AlertDialogBackupBinding dialogBackupBinding;
    private BackupRestoreProgress progressDialog;

    public int selectedCurrencyPos = 0;

    public int selectedDateFormatPos = 0;

    public int selectedTimeFormatPos = 0;

    public ArrayList<SpinnerRowModel> timeFormatList;
    public ToolbarModel toolbarModel;
    private ArrayAdapter<CharSequence> adapter;
    private Activity settingActivity;

    public void onRationaleAccepted(int i) {
    }

    public SettingActivity() {
    }

    public void onRationaleDenied(int i) {
    }

    public View getViewBinding() {
        return binding.getRoot();
    }


    public void setBinding(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        binding = (ActivitySettingBinding) DataBindingUtil.inflate(layoutInflater, R.layout.act_setting, viewGroup, false);
        backupRestore = new BackupRestore(getActivity());
        progressDialog = new BackupRestoreProgress(getActivity());
    }


    public void setToolbar() {
        toolbarModel = new ToolbarModel();
        toolbarModel.setTitle(getString(R.string.drawerTitleSetting));
        binding.includedToolbar.setToolbarModel(toolbarModel);
    }


    public void setOnClicks() {
        binding.includedToolbar.imgBack.setOnClickListener(this);
        binding.cardViewCategory.setOnClickListener(this);
        binding.cardViewPaymentMode.setOnClickListener(this);
        binding.cardViewExportedReports.setOnClickListener(this);
        binding.cardViewTakeBackup.setOnClickListener(this);
        binding.cardViewLocalBackups.setOnClickListener(this);
        binding.cardViewDriveBackups.setOnClickListener(this);
        binding.cardViewBackupTransferGuid.setOnClickListener(this);

        binding.cardClearAllData.setOnClickListener(this);

        SharedPreferences prefs = getActivity().getSharedPreferences("notification", Context.MODE_PRIVATE);
        boolean noti = prefs.getBoolean("noti", true);

        binding.notificationSwittcCase.setChecked(noti);
        binding.notificationSwittcCase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences prefs = getActivity().getSharedPreferences("notification", Context.MODE_PRIVATE);
                boolean noti = prefs.getBoolean("noti", true);

                if (noti && b) {
                    return;
                }
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("notification", Context.MODE_PRIVATE).edit();
                editor.putBoolean("noti", b);
                editor.apply();
                if (!b) {
                    cancelIntents();
                    return;
                }
                Calendar startTime = valueToCalendar();
                Calendar now = Calendar.getInstance();
                if (now.after(startTime)) {
                    startTime.add(Calendar.DATE, 1);
                }
                scheduleNotification(startTime);
            }
        });
    }


    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.imgBack) {
            switch (id) {
                case R.id.cardClearAllData:
                    AppConstants.showTwoButtonDialog(context, getString(R.string.app_name), getString(R.string.deleteMessage), true, true, getString(R.string.delete), getString(R.string.cancel), new TwoButtonDialogListener() {
                        public void onCancel() {
                        }

                        public void onOk() {
                            AppDataBase.getAppDatabase(context).getOpenHelper().getWritableDatabase().execSQL("DELETE FROM transactionList");
                            Toast.makeText(context, "Clear all entries successfully.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                case R.id.cardViewBackupTransferGuid:
                    startActivity(new Intent(context, BackupTransferGuidAct.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return;
                case R.id.cardViewCategory:
                    startActivity(new Intent(context, CategoryListAct.class));
                    return;
                case R.id.cardViewDriveBackups:
                    startActivityForResult(new Intent(context, RestoreDriveListAct.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 1002);
                    return;
                case R.id.cardViewExportedReports:
                    startActivity(new Intent(getContext(), ReportsListAct.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return;
                case R.id.cardViewLocalBackups:
                    startActivityForResult(new Intent(context, RestoreListAct.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 1002);
                    return;
                case R.id.cardViewPaymentMode:
                    startActivity(new Intent(context, PaymentModeListAct.class));
                    return;
                case R.id.cardViewTakeBackup:
                    checkPermAndBackup();
                    return;
                default:
                    return;
            }
        } else {
            getActivity().onBackPressed();
        }

    }

    public void initMethods() {
        setCurrencyPicker();
        setDateFormatPicker();
        setTimeFormatPicker();
        setBackupDialog();
    }

    private void setCurrencyPicker() {
        fillCurrencyList();
        selectedCurrencyPos = getSelectedPositionByCurrencyLabel(currencyList, AppPref.getCurrencyName(context));
        ((SpinnerRowModel) currencyList.get(selectedCurrencyPos)).setSelected(true);
        binding.spinnerCurrency.setAdapter(new CustomSpinnerAdapter(getContext(), currencyList, false));
        binding.spinnerCurrency.setSelection(selectedCurrencyPos);
        binding.spinnerCurrency.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                setSelectionAllCurrency(currencyList, false);
                selectedCurrencyPos = i;
                AppPref.setCurrencyName(context, ((SpinnerRowModel) currencyList.get(selectedCurrencyPos)).getLabel());
                AppPref.setCurrencySymbol(context, ((SpinnerRowModel) currencyList.get(selectedCurrencyPos)).getValue());
                ((SpinnerRowModel) currencyList.get(selectedCurrencyPos)).setSelected(true);
                getActivity().setResult(-1);
            }
        });
    }

    private void fillCurrencyList() {
        currencyList = new ArrayList<>();
        SpinnerRowModel spinnerRowModel = new SpinnerRowModel();
        spinnerRowModel.setLabel("AED");
        spinnerRowModel.setValue("AED");
        currencyList.add(spinnerRowModel);
        SpinnerRowModel spinnerRowModel2 = new SpinnerRowModel();
        spinnerRowModel2.setLabel("AFN");
        spinnerRowModel2.setValue("AFN");
        currencyList.add(spinnerRowModel2);
        SpinnerRowModel spinnerRowModel3 = new SpinnerRowModel();
        spinnerRowModel3.setLabel("ALL");
        spinnerRowModel3.setValue("Lek");
        currencyList.add(spinnerRowModel3);
        SpinnerRowModel spinnerRowModel4 = new SpinnerRowModel();
        spinnerRowModel4.setLabel("AMD");
        spinnerRowModel4.setValue("AMD");
        currencyList.add(spinnerRowModel4);
        SpinnerRowModel spinnerRowModel5 = new SpinnerRowModel();
        spinnerRowModel5.setLabel("ANG");
        spinnerRowModel5.setValue("ƒ");
        currencyList.add(spinnerRowModel5);
        SpinnerRowModel spinnerRowModel6 = new SpinnerRowModel();
        spinnerRowModel6.setLabel("AOA");
        spinnerRowModel6.setValue("AOA");
        currencyList.add(spinnerRowModel6);
        SpinnerRowModel spinnerRowModel7 = new SpinnerRowModel();
        spinnerRowModel7.setLabel("ARS");
        spinnerRowModel7.setValue("$");
        currencyList.add(spinnerRowModel7);
        SpinnerRowModel spinnerRowModel8 = new SpinnerRowModel();
        spinnerRowModel8.setLabel("AUD");
        spinnerRowModel8.setValue("$");
        currencyList.add(spinnerRowModel8);
        SpinnerRowModel spinnerRowModel9 = new SpinnerRowModel();
        spinnerRowModel9.setLabel("AWG");
        spinnerRowModel9.setValue("ƒ");
        currencyList.add(spinnerRowModel9);
        SpinnerRowModel spinnerRowModel10 = new SpinnerRowModel();
        spinnerRowModel10.setLabel("AZN");
        spinnerRowModel10.setValue("AZN");
        currencyList.add(spinnerRowModel10);
        SpinnerRowModel spinnerRowModel11 = new SpinnerRowModel();
        spinnerRowModel11.setLabel("BAM");
        spinnerRowModel11.setValue("KM");
        currencyList.add(spinnerRowModel11);
        SpinnerRowModel spinnerRowModel12 = new SpinnerRowModel();
        spinnerRowModel12.setLabel("BBD");
        spinnerRowModel12.setValue("$");
        currencyList.add(spinnerRowModel12);
        SpinnerRowModel spinnerRowModel13 = new SpinnerRowModel();
        spinnerRowModel13.setLabel("BDT");
        spinnerRowModel13.setValue("BDT");
        currencyList.add(spinnerRowModel13);
        SpinnerRowModel spinnerRowModel14 = new SpinnerRowModel();
        spinnerRowModel14.setLabel("BGN");
        spinnerRowModel14.setValue("BGN");
        currencyList.add(spinnerRowModel14);
        SpinnerRowModel spinnerRowModel15 = new SpinnerRowModel();
        spinnerRowModel15.setLabel("BHD");
        spinnerRowModel15.setValue("BHD");
        currencyList.add(spinnerRowModel15);
        SpinnerRowModel spinnerRowModel16 = new SpinnerRowModel();
        spinnerRowModel16.setLabel("BIF");
        spinnerRowModel16.setValue("BIF");
        currencyList.add(spinnerRowModel16);
        SpinnerRowModel spinnerRowModel17 = new SpinnerRowModel();
        spinnerRowModel17.setLabel("BMD");
        spinnerRowModel17.setValue("$");
        currencyList.add(spinnerRowModel17);
        SpinnerRowModel spinnerRowModel18 = new SpinnerRowModel();
        spinnerRowModel18.setLabel("BND");
        spinnerRowModel18.setValue("$");
        currencyList.add(spinnerRowModel18);
        SpinnerRowModel spinnerRowModel19 = new SpinnerRowModel();
        spinnerRowModel19.setLabel("BOB");
        spinnerRowModel19.setValue("$b");
        currencyList.add(spinnerRowModel19);
        SpinnerRowModel spinnerRowModel20 = new SpinnerRowModel();
        spinnerRowModel20.setLabel("BOV");
        spinnerRowModel20.setValue("BOV");
        currencyList.add(spinnerRowModel20);
        SpinnerRowModel spinnerRowModel21 = new SpinnerRowModel();
        spinnerRowModel21.setLabel("BRL");
        spinnerRowModel21.setValue("R$");
        currencyList.add(spinnerRowModel21);
        SpinnerRowModel spinnerRowModel22 = new SpinnerRowModel();
        spinnerRowModel22.setLabel("BSD");
        spinnerRowModel22.setValue("$");
        currencyList.add(spinnerRowModel22);
        SpinnerRowModel spinnerRowModel23 = new SpinnerRowModel();
        spinnerRowModel23.setLabel("BTC");
        spinnerRowModel23.setValue("BTC");
        currencyList.add(spinnerRowModel23);
        SpinnerRowModel spinnerRowModel24 = new SpinnerRowModel();
        spinnerRowModel24.setLabel("BTN");
        spinnerRowModel24.setValue("BTN");
        currencyList.add(spinnerRowModel24);
        SpinnerRowModel spinnerRowModel25 = new SpinnerRowModel();
        spinnerRowModel25.setLabel("BWP");
        spinnerRowModel25.setValue("P");
        currencyList.add(spinnerRowModel25);
        SpinnerRowModel spinnerRowModel26 = new SpinnerRowModel();
        spinnerRowModel26.setLabel("BYR");
        spinnerRowModel26.setValue("p.");
        currencyList.add(spinnerRowModel26);
        SpinnerRowModel spinnerRowModel27 = new SpinnerRowModel();
        spinnerRowModel27.setLabel("BZD");
        spinnerRowModel27.setValue("BZ$");
        currencyList.add(spinnerRowModel27);
        SpinnerRowModel spinnerRowModel28 = new SpinnerRowModel();
        spinnerRowModel28.setLabel("CAD");
        spinnerRowModel28.setValue("$");
        currencyList.add(spinnerRowModel28);
        SpinnerRowModel spinnerRowModel29 = new SpinnerRowModel();
        spinnerRowModel29.setLabel("CDF");
        spinnerRowModel29.setValue("CDF");
        currencyList.add(spinnerRowModel29);
        SpinnerRowModel spinnerRowModel30 = new SpinnerRowModel();
        spinnerRowModel30.setLabel("CHE");
        spinnerRowModel30.setValue("CHE");
        currencyList.add(spinnerRowModel30);
        SpinnerRowModel spinnerRowModel31 = new SpinnerRowModel();
        spinnerRowModel31.setLabel("CHF");
        spinnerRowModel31.setValue("CHF");
        currencyList.add(spinnerRowModel31);
        SpinnerRowModel spinnerRowModel32 = new SpinnerRowModel();
        spinnerRowModel32.setLabel("CHW");
        spinnerRowModel32.setValue("CHW");
        currencyList.add(spinnerRowModel32);
        SpinnerRowModel spinnerRowModel33 = new SpinnerRowModel();
        spinnerRowModel33.setLabel("CLF");
        spinnerRowModel33.setValue("CLF");
        currencyList.add(spinnerRowModel33);
        SpinnerRowModel spinnerRowModel34 = new SpinnerRowModel();
        spinnerRowModel34.setLabel("CLP");
        spinnerRowModel34.setValue("$");
        currencyList.add(spinnerRowModel34);
        SpinnerRowModel spinnerRowModel35 = new SpinnerRowModel();
        spinnerRowModel35.setLabel("CNY");
        spinnerRowModel35.setValue("CNY");
        currencyList.add(spinnerRowModel35);
        SpinnerRowModel spinnerRowModel36 = new SpinnerRowModel();
        spinnerRowModel36.setLabel("COP");
        spinnerRowModel36.setValue("$");
        currencyList.add(spinnerRowModel36);
        SpinnerRowModel spinnerRowModel37 = new SpinnerRowModel();
        spinnerRowModel37.setLabel("COU");
        spinnerRowModel37.setValue("COU");
        currencyList.add(spinnerRowModel37);
        SpinnerRowModel spinnerRowModel38 = new SpinnerRowModel();
        spinnerRowModel38.setLabel("CRC");
        spinnerRowModel38.setValue("CRC");
        currencyList.add(spinnerRowModel38);
        SpinnerRowModel spinnerRowModel39 = new SpinnerRowModel();
        spinnerRowModel39.setLabel("CUC");
        spinnerRowModel39.setValue("UC$");
        currencyList.add(spinnerRowModel39);
        SpinnerRowModel spinnerRowModel40 = new SpinnerRowModel();
        spinnerRowModel40.setLabel("CUP");
        spinnerRowModel40.setValue("CUP");
        currencyList.add(spinnerRowModel40);
        SpinnerRowModel spinnerRowModel41 = new SpinnerRowModel();
        spinnerRowModel41.setLabel("CVE");
        spinnerRowModel41.setValue("CVE");
        currencyList.add(spinnerRowModel41);
        SpinnerRowModel spinnerRowModel42 = new SpinnerRowModel();
        spinnerRowModel42.setLabel("CYP");
        spinnerRowModel42.setValue("CYP");
        currencyList.add(spinnerRowModel42);
        SpinnerRowModel spinnerRowModel43 = new SpinnerRowModel();
        spinnerRowModel43.setLabel("CZK");
        spinnerRowModel43.setValue("CZK");
        currencyList.add(spinnerRowModel43);
        SpinnerRowModel spinnerRowModel44 = new SpinnerRowModel();
        spinnerRowModel44.setLabel("DJF");
        spinnerRowModel44.setValue("DJF");
        currencyList.add(spinnerRowModel44);
        SpinnerRowModel spinnerRowModel45 = new SpinnerRowModel();
        spinnerRowModel45.setLabel("DKK");
        spinnerRowModel45.setValue("kr");
        currencyList.add(spinnerRowModel45);
        SpinnerRowModel spinnerRowModel46 = new SpinnerRowModel();
        spinnerRowModel46.setLabel("DOP");
        spinnerRowModel46.setValue("RD$");
        currencyList.add(spinnerRowModel46);
        SpinnerRowModel spinnerRowModel47 = new SpinnerRowModel();
        spinnerRowModel47.setLabel("DZD");
        spinnerRowModel47.setValue("DZD");
        currencyList.add(spinnerRowModel47);
        SpinnerRowModel spinnerRowModel48 = new SpinnerRowModel();
        spinnerRowModel48.setLabel("EEK");
        spinnerRowModel48.setValue("kr");
        currencyList.add(spinnerRowModel48);
        SpinnerRowModel spinnerRowModel49 = new SpinnerRowModel();
        spinnerRowModel49.setLabel("EGP");
        spinnerRowModel49.setValue("£");
        currencyList.add(spinnerRowModel49);
        SpinnerRowModel spinnerRowModel50 = new SpinnerRowModel();
        spinnerRowModel50.setLabel("ERN");
        spinnerRowModel50.setValue("ERN");
        currencyList.add(spinnerRowModel50);
        SpinnerRowModel spinnerRowModel51 = new SpinnerRowModel();
        spinnerRowModel51.setLabel("ETB");
        spinnerRowModel51.setValue("ETB");
        currencyList.add(spinnerRowModel51);
        SpinnerRowModel spinnerRowModel52 = new SpinnerRowModel();
        spinnerRowModel52.setLabel("EUR");
        spinnerRowModel52.setValue("€");
        currencyList.add(spinnerRowModel52);
        SpinnerRowModel spinnerRowModel53 = new SpinnerRowModel();
        spinnerRowModel53.setLabel("FJD");
        spinnerRowModel53.setValue("$");
        currencyList.add(spinnerRowModel53);
        SpinnerRowModel spinnerRowModel54 = new SpinnerRowModel();
        spinnerRowModel54.setLabel("FKP");
        spinnerRowModel54.setValue("£");
        currencyList.add(spinnerRowModel54);
        SpinnerRowModel spinnerRowModel55 = new SpinnerRowModel();
        spinnerRowModel55.setLabel("GBP");
        spinnerRowModel55.setValue("£");
        currencyList.add(spinnerRowModel55);
        SpinnerRowModel spinnerRowModel56 = new SpinnerRowModel();
        spinnerRowModel56.setLabel("GEL");
        spinnerRowModel56.setValue("GEL");
        currencyList.add(spinnerRowModel56);
        SpinnerRowModel spinnerRowModel57 = new SpinnerRowModel();
        spinnerRowModel57.setLabel("GGP");
        spinnerRowModel57.setValue("£");
        currencyList.add(spinnerRowModel57);
        SpinnerRowModel spinnerRowModel58 = new SpinnerRowModel();
        spinnerRowModel58.setLabel("GHS");
        spinnerRowModel58.setValue("¢");
        currencyList.add(spinnerRowModel58);
        SpinnerRowModel spinnerRowModel59 = new SpinnerRowModel();
        spinnerRowModel59.setLabel("GIP");
        spinnerRowModel59.setValue("£");
        currencyList.add(spinnerRowModel59);
        SpinnerRowModel spinnerRowModel60 = new SpinnerRowModel();
        spinnerRowModel60.setLabel("GMD");
        spinnerRowModel60.setValue("GMD");
        currencyList.add(spinnerRowModel60);
        SpinnerRowModel spinnerRowModel61 = new SpinnerRowModel();
        spinnerRowModel61.setLabel("GNF");
        spinnerRowModel61.setValue("GNF");
        currencyList.add(spinnerRowModel61);
        SpinnerRowModel spinnerRowModel62 = new SpinnerRowModel();
        spinnerRowModel62.setLabel("GTQ");
        spinnerRowModel62.setValue("Q");
        currencyList.add(spinnerRowModel62);
        SpinnerRowModel spinnerRowModel63 = new SpinnerRowModel();
        spinnerRowModel63.setLabel("GWP");
        spinnerRowModel63.setValue("GWP");
        currencyList.add(spinnerRowModel63);
        SpinnerRowModel spinnerRowModel64 = new SpinnerRowModel();
        spinnerRowModel64.setLabel("GYD");
        spinnerRowModel64.setValue("$");
        currencyList.add(spinnerRowModel64);
        SpinnerRowModel spinnerRowModel65 = new SpinnerRowModel();
        spinnerRowModel65.setLabel("HKD");
        spinnerRowModel65.setValue("元");
        currencyList.add(spinnerRowModel65);
        SpinnerRowModel spinnerRowModel66 = new SpinnerRowModel();
        spinnerRowModel66.setLabel("HNL");
        spinnerRowModel66.setValue("L");
        currencyList.add(spinnerRowModel66);
        SpinnerRowModel spinnerRowModel67 = new SpinnerRowModel();
        spinnerRowModel67.setLabel("HRK");
        spinnerRowModel67.setValue("kn");
        currencyList.add(spinnerRowModel67);
        SpinnerRowModel spinnerRowModel68 = new SpinnerRowModel();
        spinnerRowModel68.setLabel("HTG");
        spinnerRowModel68.setValue("HTG");
        currencyList.add(spinnerRowModel68);
        SpinnerRowModel spinnerRowModel69 = new SpinnerRowModel();
        spinnerRowModel69.setLabel("HUF");
        spinnerRowModel69.setValue("Ft");
        currencyList.add(spinnerRowModel69);
        SpinnerRowModel spinnerRowModel70 = new SpinnerRowModel();
        spinnerRowModel70.setLabel("IDR");
        spinnerRowModel70.setValue("Rp");
        currencyList.add(spinnerRowModel70);
        SpinnerRowModel spinnerRowModel71 = new SpinnerRowModel();
        spinnerRowModel71.setLabel("ILS");
        spinnerRowModel71.setValue("ILS");
        currencyList.add(spinnerRowModel71);
        SpinnerRowModel spinnerRowModel72 = new SpinnerRowModel();
        spinnerRowModel72.setLabel("IMP");
        spinnerRowModel72.setValue("£");
        currencyList.add(spinnerRowModel72);
        SpinnerRowModel spinnerRowModel73 = new SpinnerRowModel();
        spinnerRowModel73.setLabel("INR");
        spinnerRowModel73.setValue("Rs.");
        currencyList.add(spinnerRowModel73);
        SpinnerRowModel spinnerRowModel74 = new SpinnerRowModel();
        spinnerRowModel74.setLabel("IQD");
        spinnerRowModel74.setValue("IQD");
        currencyList.add(spinnerRowModel74);
        SpinnerRowModel spinnerRowModel75 = new SpinnerRowModel();
        spinnerRowModel75.setLabel("IRR");
        spinnerRowModel75.setValue("IRR");
        currencyList.add(spinnerRowModel75);
        SpinnerRowModel spinnerRowModel76 = new SpinnerRowModel();
        spinnerRowModel76.setLabel("ISK");
        spinnerRowModel76.setValue("kr");
        currencyList.add(spinnerRowModel76);
        SpinnerRowModel spinnerRowModel77 = new SpinnerRowModel();
        spinnerRowModel77.setLabel("JEP");
        spinnerRowModel77.setValue("£");
        currencyList.add(spinnerRowModel77);
        SpinnerRowModel spinnerRowModel78 = new SpinnerRowModel();
        spinnerRowModel78.setLabel("JMD");
        spinnerRowModel78.setValue("J$");
        currencyList.add(spinnerRowModel78);
        SpinnerRowModel spinnerRowModel79 = new SpinnerRowModel();
        spinnerRowModel79.setLabel("JOD");
        spinnerRowModel79.setValue("JOD");
        currencyList.add(spinnerRowModel79);
        SpinnerRowModel spinnerRowModel80 = new SpinnerRowModel();
        spinnerRowModel80.setLabel("JPY");
        spinnerRowModel80.setValue("¥");
        currencyList.add(spinnerRowModel80);
        SpinnerRowModel spinnerRowModel81 = new SpinnerRowModel();
        spinnerRowModel81.setLabel("KES");
        spinnerRowModel81.setValue("KES");
        currencyList.add(spinnerRowModel81);
        SpinnerRowModel spinnerRowModel82 = new SpinnerRowModel();
        spinnerRowModel82.setLabel("KGS");
        spinnerRowModel82.setValue("KGS");
        currencyList.add(spinnerRowModel82);
        SpinnerRowModel spinnerRowModel83 = new SpinnerRowModel();
        spinnerRowModel83.setLabel("KHR");
        spinnerRowModel83.setValue("KHR");
        currencyList.add(spinnerRowModel83);
        SpinnerRowModel spinnerRowModel84 = new SpinnerRowModel();
        spinnerRowModel84.setLabel("KMF");
        spinnerRowModel84.setValue("KMF");
        currencyList.add(spinnerRowModel84);
        SpinnerRowModel spinnerRowModel85 = new SpinnerRowModel();
        spinnerRowModel85.setLabel("KPW");
        spinnerRowModel85.setValue("₩");
        currencyList.add(spinnerRowModel85);
        SpinnerRowModel spinnerRowModel86 = new SpinnerRowModel();
        spinnerRowModel86.setLabel("KRW");
        spinnerRowModel86.setValue("₩");
        currencyList.add(spinnerRowModel86);
        SpinnerRowModel spinnerRowModel87 = new SpinnerRowModel();
        spinnerRowModel87.setLabel("KWD");
        spinnerRowModel87.setValue("KWD");
        currencyList.add(spinnerRowModel87);
        SpinnerRowModel spinnerRowModel88 = new SpinnerRowModel();
        spinnerRowModel88.setLabel("KYD");
        spinnerRowModel88.setValue("$");
        currencyList.add(spinnerRowModel88);
        SpinnerRowModel spinnerRowModel89 = new SpinnerRowModel();
        spinnerRowModel89.setLabel("KZT");
        spinnerRowModel89.setValue("KZT");
        currencyList.add(spinnerRowModel89);
        SpinnerRowModel spinnerRowModel90 = new SpinnerRowModel();
        spinnerRowModel90.setLabel("LAK");
        spinnerRowModel90.setValue("LAK");
        currencyList.add(spinnerRowModel90);
        SpinnerRowModel spinnerRowModel91 = new SpinnerRowModel();
        spinnerRowModel91.setLabel("LBP");
        spinnerRowModel91.setValue("£");
        currencyList.add(spinnerRowModel91);
        SpinnerRowModel spinnerRowModel92 = new SpinnerRowModel();
        spinnerRowModel92.setLabel("LKR");
        spinnerRowModel92.setValue("Rs");
        currencyList.add(spinnerRowModel92);
        SpinnerRowModel spinnerRowModel93 = new SpinnerRowModel();
        spinnerRowModel93.setLabel("LRD");
        spinnerRowModel93.setValue("$");
        currencyList.add(spinnerRowModel93);
        SpinnerRowModel spinnerRowModel94 = new SpinnerRowModel();
        spinnerRowModel94.setLabel("LSL");
        spinnerRowModel94.setValue("LSL");
        currencyList.add(spinnerRowModel94);
        SpinnerRowModel spinnerRowModel95 = new SpinnerRowModel();
        spinnerRowModel95.setLabel("LTL");
        spinnerRowModel95.setValue("Lt");
        currencyList.add(spinnerRowModel95);
        SpinnerRowModel spinnerRowModel96 = new SpinnerRowModel();
        spinnerRowModel96.setLabel("LVL");
        spinnerRowModel96.setValue("Ls");
        currencyList.add(spinnerRowModel96);
        SpinnerRowModel spinnerRowModel97 = new SpinnerRowModel();
        spinnerRowModel97.setLabel("LYD");
        spinnerRowModel97.setValue("LYD");
        currencyList.add(spinnerRowModel97);
        SpinnerRowModel spinnerRowModel98 = new SpinnerRowModel();
        spinnerRowModel98.setLabel("MAD");
        spinnerRowModel98.setValue("MAD");
        currencyList.add(spinnerRowModel98);
        SpinnerRowModel spinnerRowModel99 = new SpinnerRowModel();
        spinnerRowModel99.setLabel("MDL");
        spinnerRowModel99.setValue("MDL");
        currencyList.add(spinnerRowModel99);
        SpinnerRowModel spinnerRowModel100 = new SpinnerRowModel();
        spinnerRowModel100.setLabel("MGA");
        spinnerRowModel100.setValue("MGA");
        currencyList.add(spinnerRowModel100);
        SpinnerRowModel spinnerRowModel101 = new SpinnerRowModel();
        spinnerRowModel101.setLabel("MKD");
        spinnerRowModel101.setValue("MKD");
        currencyList.add(spinnerRowModel101);
        SpinnerRowModel spinnerRowModel102 = new SpinnerRowModel();
        spinnerRowModel102.setLabel("MMK");
        spinnerRowModel102.setValue("MMK");
        currencyList.add(spinnerRowModel102);
        SpinnerRowModel spinnerRowModel103 = new SpinnerRowModel();
        spinnerRowModel103.setLabel("MNT");
        spinnerRowModel103.setValue("MNT");
        currencyList.add(spinnerRowModel103);
        SpinnerRowModel spinnerRowModel104 = new SpinnerRowModel();
        spinnerRowModel104.setLabel("MOP");
        spinnerRowModel104.setValue("MOP");
        currencyList.add(spinnerRowModel104);
        SpinnerRowModel spinnerRowModel105 = new SpinnerRowModel();
        spinnerRowModel105.setLabel("MRO");
        spinnerRowModel105.setValue("MRO");
        currencyList.add(spinnerRowModel105);
        SpinnerRowModel spinnerRowModel106 = new SpinnerRowModel();
        spinnerRowModel106.setLabel("MTL");
        spinnerRowModel106.setValue("MTL");
        currencyList.add(spinnerRowModel106);
        SpinnerRowModel spinnerRowModel107 = new SpinnerRowModel();
        spinnerRowModel107.setLabel("MUR");
        spinnerRowModel107.setValue("Rp");
        currencyList.add(spinnerRowModel107);
        SpinnerRowModel spinnerRowModel108 = new SpinnerRowModel();
        spinnerRowModel108.setLabel("MVR");
        spinnerRowModel108.setValue("MVR");
        currencyList.add(spinnerRowModel108);
        SpinnerRowModel spinnerRowModel109 = new SpinnerRowModel();
        spinnerRowModel109.setLabel("MWK");
        spinnerRowModel109.setValue("MWK");
        currencyList.add(spinnerRowModel109);
        SpinnerRowModel spinnerRowModel110 = new SpinnerRowModel();
        spinnerRowModel110.setLabel("MXN");
        spinnerRowModel110.setValue("$");
        currencyList.add(spinnerRowModel110);
        SpinnerRowModel spinnerRowModel111 = new SpinnerRowModel();
        spinnerRowModel111.setLabel("MXV");
        spinnerRowModel111.setValue("MXV");
        currencyList.add(spinnerRowModel111);
        SpinnerRowModel spinnerRowModel112 = new SpinnerRowModel();
        spinnerRowModel112.setLabel("MYR");
        spinnerRowModel112.setValue("RM");
        currencyList.add(spinnerRowModel112);
        SpinnerRowModel spinnerRowModel113 = new SpinnerRowModel();
        spinnerRowModel113.setLabel("MZN");
        spinnerRowModel113.setValue("MT");
        currencyList.add(spinnerRowModel113);
        SpinnerRowModel spinnerRowModel114 = new SpinnerRowModel();
        spinnerRowModel114.setLabel("NAD");
        spinnerRowModel114.setValue("$");
        currencyList.add(spinnerRowModel114);
        SpinnerRowModel spinnerRowModel115 = new SpinnerRowModel();
        spinnerRowModel115.setLabel("NGN");
        spinnerRowModel115.setValue("NGN");
        currencyList.add(spinnerRowModel115);
        SpinnerRowModel spinnerRowModel116 = new SpinnerRowModel();
        spinnerRowModel116.setLabel("NIO");
        spinnerRowModel116.setValue("C$");
        currencyList.add(spinnerRowModel116);
        SpinnerRowModel spinnerRowModel117 = new SpinnerRowModel();
        spinnerRowModel117.setLabel("NOK");
        spinnerRowModel117.setValue("kr");
        currencyList.add(spinnerRowModel117);
        SpinnerRowModel spinnerRowModel118 = new SpinnerRowModel();
        spinnerRowModel118.setLabel("NPR");
        spinnerRowModel118.setValue("Rp");
        currencyList.add(spinnerRowModel118);
        SpinnerRowModel spinnerRowModel119 = new SpinnerRowModel();
        spinnerRowModel119.setLabel("NZD");
        spinnerRowModel119.setValue("$");
        currencyList.add(spinnerRowModel119);
        SpinnerRowModel spinnerRowModel120 = new SpinnerRowModel();
        spinnerRowModel120.setLabel("OMR");
        spinnerRowModel120.setValue("OMR");
        currencyList.add(spinnerRowModel120);
        SpinnerRowModel spinnerRowModel121 = new SpinnerRowModel();
        spinnerRowModel121.setLabel("PAB");
        spinnerRowModel121.setValue("B/.");
        currencyList.add(spinnerRowModel121);
        SpinnerRowModel spinnerRowModel122 = new SpinnerRowModel();
        spinnerRowModel122.setLabel("PEN");
        spinnerRowModel122.setValue("S/.");
        currencyList.add(spinnerRowModel122);
        SpinnerRowModel spinnerRowModel123 = new SpinnerRowModel();
        spinnerRowModel123.setLabel("PGK");
        spinnerRowModel123.setValue("PGK");
        currencyList.add(spinnerRowModel123);
        SpinnerRowModel spinnerRowModel124 = new SpinnerRowModel();
        spinnerRowModel124.setLabel("PHP");
        spinnerRowModel124.setValue("Php");
        currencyList.add(spinnerRowModel124);
        SpinnerRowModel spinnerRowModel125 = new SpinnerRowModel();
        spinnerRowModel125.setLabel("PKR");
        spinnerRowModel125.setValue("Rs");
        currencyList.add(spinnerRowModel125);
        SpinnerRowModel spinnerRowModel126 = new SpinnerRowModel();
        spinnerRowModel126.setLabel("PLN");
        spinnerRowModel126.setValue("PLN");
        currencyList.add(spinnerRowModel126);
        SpinnerRowModel spinnerRowModel127 = new SpinnerRowModel();
        spinnerRowModel127.setLabel("PYG");
        spinnerRowModel127.setValue("Gs");
        currencyList.add(spinnerRowModel127);
        SpinnerRowModel spinnerRowModel128 = new SpinnerRowModel();
        spinnerRowModel128.setLabel("QAR");
        spinnerRowModel128.setValue("QAR");
        currencyList.add(spinnerRowModel128);
        SpinnerRowModel spinnerRowModel129 = new SpinnerRowModel();
        spinnerRowModel129.setLabel("ROL");
        spinnerRowModel129.setValue("ROL");
        currencyList.add(spinnerRowModel129);
        SpinnerRowModel spinnerRowModel130 = new SpinnerRowModel();
        spinnerRowModel130.setLabel("RON");
        spinnerRowModel130.setValue("lei");
        currencyList.add(spinnerRowModel130);
        SpinnerRowModel spinnerRowModel131 = new SpinnerRowModel();
        spinnerRowModel131.setLabel("RSD");
        spinnerRowModel131.setValue("RSD");
        currencyList.add(spinnerRowModel131);
        SpinnerRowModel spinnerRowModel132 = new SpinnerRowModel();
        spinnerRowModel132.setLabel("RUB");
        spinnerRowModel132.setValue("RUB");
        currencyList.add(spinnerRowModel132);
        SpinnerRowModel spinnerRowModel133 = new SpinnerRowModel();
        spinnerRowModel133.setLabel("RWF");
        spinnerRowModel133.setValue("RWF");
        currencyList.add(spinnerRowModel133);
        SpinnerRowModel spinnerRowModel134 = new SpinnerRowModel();
        spinnerRowModel134.setLabel("SAR");
        spinnerRowModel134.setValue("SAR");
        currencyList.add(spinnerRowModel134);
        SpinnerRowModel spinnerRowModel135 = new SpinnerRowModel();
        spinnerRowModel135.setLabel("SBD");
        spinnerRowModel135.setValue("$");
        currencyList.add(spinnerRowModel135);
        SpinnerRowModel spinnerRowModel136 = new SpinnerRowModel();
        spinnerRowModel136.setLabel("SCR");
        spinnerRowModel136.setValue("Rp");
        currencyList.add(spinnerRowModel136);
        SpinnerRowModel spinnerRowModel137 = new SpinnerRowModel();
        spinnerRowModel137.setLabel("SDD");
        spinnerRowModel137.setValue("SDD");
        currencyList.add(spinnerRowModel137);
        SpinnerRowModel spinnerRowModel138 = new SpinnerRowModel();
        spinnerRowModel138.setLabel("SDG");
        spinnerRowModel138.setValue("SDG");
        currencyList.add(spinnerRowModel138);
        SpinnerRowModel spinnerRowModel139 = new SpinnerRowModel();
        spinnerRowModel139.setLabel("SEK");
        spinnerRowModel139.setValue("kr");
        currencyList.add(spinnerRowModel139);
        SpinnerRowModel spinnerRowModel140 = new SpinnerRowModel();
        spinnerRowModel140.setLabel("SGD");
        spinnerRowModel140.setValue("$");
        currencyList.add(spinnerRowModel140);
        SpinnerRowModel spinnerRowModel141 = new SpinnerRowModel();
        spinnerRowModel141.setLabel("SHP");
        spinnerRowModel141.setValue("£");
        currencyList.add(spinnerRowModel141);
        SpinnerRowModel spinnerRowModel142 = new SpinnerRowModel();
        spinnerRowModel142.setLabel("SIT");
        spinnerRowModel142.setValue("SIT");
        currencyList.add(spinnerRowModel142);
        SpinnerRowModel spinnerRowModel143 = new SpinnerRowModel();
        spinnerRowModel143.setLabel("SKK");
        spinnerRowModel143.setValue("SKK");
        currencyList.add(spinnerRowModel143);
        SpinnerRowModel spinnerRowModel144 = new SpinnerRowModel();
        spinnerRowModel144.setLabel("SLL");
        spinnerRowModel144.setValue("SLL");
        currencyList.add(spinnerRowModel144);
        SpinnerRowModel spinnerRowModel145 = new SpinnerRowModel();
        spinnerRowModel145.setLabel("SOS");
        spinnerRowModel145.setValue("S");
        currencyList.add(spinnerRowModel145);
        SpinnerRowModel spinnerRowModel146 = new SpinnerRowModel();
        spinnerRowModel146.setLabel("SRD");
        spinnerRowModel146.setValue("$");
        currencyList.add(spinnerRowModel146);
        SpinnerRowModel spinnerRowModel147 = new SpinnerRowModel();
        spinnerRowModel147.setLabel("STD");
        spinnerRowModel147.setValue("STD");
        currencyList.add(spinnerRowModel147);
        SpinnerRowModel spinnerRowModel148 = new SpinnerRowModel();
        spinnerRowModel148.setLabel("SVC");
        spinnerRowModel148.setValue("$");
        currencyList.add(spinnerRowModel148);
        SpinnerRowModel spinnerRowModel149 = new SpinnerRowModel();
        spinnerRowModel149.setLabel("SYP");
        spinnerRowModel149.setValue("£");
        currencyList.add(spinnerRowModel149);
        SpinnerRowModel spinnerRowModel150 = new SpinnerRowModel();
        spinnerRowModel150.setLabel("SZL");
        spinnerRowModel150.setValue("SZL");
        currencyList.add(spinnerRowModel150);
        SpinnerRowModel spinnerRowModel151 = new SpinnerRowModel();
        spinnerRowModel151.setLabel("THB");
        spinnerRowModel151.setValue("THB");
        currencyList.add(spinnerRowModel151);
        SpinnerRowModel spinnerRowModel152 = new SpinnerRowModel();
        spinnerRowModel152.setLabel("TJS");
        spinnerRowModel152.setValue("TJS");
        currencyList.add(spinnerRowModel152);
        SpinnerRowModel spinnerRowModel153 = new SpinnerRowModel();
        spinnerRowModel153.setLabel("TMT");
        spinnerRowModel153.setValue("TMT");
        currencyList.add(spinnerRowModel153);
        SpinnerRowModel spinnerRowModel154 = new SpinnerRowModel();
        spinnerRowModel154.setLabel("TND");
        spinnerRowModel154.setValue("TND");
        currencyList.add(spinnerRowModel154);
        SpinnerRowModel spinnerRowModel155 = new SpinnerRowModel();
        spinnerRowModel155.setLabel("TOP");
        spinnerRowModel155.setValue("TOP");
        currencyList.add(spinnerRowModel155);
        SpinnerRowModel spinnerRowModel156 = new SpinnerRowModel();
        spinnerRowModel156.setLabel("TRL");
        spinnerRowModel156.setValue("TL");
        currencyList.add(spinnerRowModel156);
        SpinnerRowModel spinnerRowModel157 = new SpinnerRowModel();
        spinnerRowModel157.setLabel("TRY");
        spinnerRowModel157.setValue("YTL");
        currencyList.add(spinnerRowModel157);
        SpinnerRowModel spinnerRowModel158 = new SpinnerRowModel();
        spinnerRowModel158.setLabel("TTD");
        spinnerRowModel158.setValue("TT$");
        currencyList.add(spinnerRowModel158);
        SpinnerRowModel spinnerRowModel159 = new SpinnerRowModel();
        spinnerRowModel159.setLabel("TVD");
        spinnerRowModel159.setValue("$");
        currencyList.add(spinnerRowModel159);
        SpinnerRowModel spinnerRowModel160 = new SpinnerRowModel();
        spinnerRowModel160.setLabel("TWD");
        spinnerRowModel160.setValue("NT$");
        currencyList.add(spinnerRowModel160);
        SpinnerRowModel spinnerRowModel161 = new SpinnerRowModel();
        spinnerRowModel161.setLabel("TZS");
        spinnerRowModel161.setValue("TZS");
        currencyList.add(spinnerRowModel161);
        SpinnerRowModel spinnerRowModel162 = new SpinnerRowModel();
        spinnerRowModel162.setLabel("UAH");
        spinnerRowModel162.setValue("UAH");
        currencyList.add(spinnerRowModel162);
        SpinnerRowModel spinnerRowModel163 = new SpinnerRowModel();
        spinnerRowModel163.setLabel("UGX");
        spinnerRowModel163.setValue("UGX");
        currencyList.add(spinnerRowModel163);
        SpinnerRowModel spinnerRowModel164 = new SpinnerRowModel();
        spinnerRowModel164.setLabel("USD");
        spinnerRowModel164.setValue("$");
        currencyList.add(spinnerRowModel164);
        SpinnerRowModel spinnerRowModel165 = new SpinnerRowModel();
        spinnerRowModel165.setLabel("UYI");
        spinnerRowModel165.setValue("UYI");
        currencyList.add(spinnerRowModel165);
        SpinnerRowModel spinnerRowModel166 = new SpinnerRowModel();
        spinnerRowModel166.setLabel("UYU");
        spinnerRowModel166.setValue("$U");
        currencyList.add(spinnerRowModel166);
        SpinnerRowModel spinnerRowModel167 = new SpinnerRowModel();
        spinnerRowModel167.setLabel("UZS");
        spinnerRowModel167.setValue("UZS");
        currencyList.add(spinnerRowModel167);
        SpinnerRowModel spinnerRowModel168 = new SpinnerRowModel();
        spinnerRowModel168.setLabel("VEB");
        spinnerRowModel168.setValue("VEB");
        currencyList.add(spinnerRowModel168);
        SpinnerRowModel spinnerRowModel169 = new SpinnerRowModel();
        spinnerRowModel169.setLabel("VEF");
        spinnerRowModel169.setValue("VEF");
        currencyList.add(spinnerRowModel169);
        SpinnerRowModel spinnerRowModel170 = new SpinnerRowModel();
        spinnerRowModel170.setLabel("VND");
        spinnerRowModel170.setValue("VND");
        currencyList.add(spinnerRowModel170);
        SpinnerRowModel spinnerRowModel171 = new SpinnerRowModel();
        spinnerRowModel171.setLabel("VUV");
        spinnerRowModel171.setValue("VUV");
        currencyList.add(spinnerRowModel171);
        SpinnerRowModel spinnerRowModel172 = new SpinnerRowModel();
        spinnerRowModel172.setLabel("WST");
        spinnerRowModel172.setValue("WST");
        currencyList.add(spinnerRowModel172);
        SpinnerRowModel spinnerRowModel173 = new SpinnerRowModel();
        spinnerRowModel173.setLabel("XAF");
        spinnerRowModel173.setValue("XAF");
        currencyList.add(spinnerRowModel173);
        SpinnerRowModel spinnerRowModel174 = new SpinnerRowModel();
        spinnerRowModel174.setLabel("XCD");
        spinnerRowModel174.setValue("$");
        currencyList.add(spinnerRowModel174);
        SpinnerRowModel spinnerRowModel175 = new SpinnerRowModel();
        spinnerRowModel175.setLabel("XDR");
        spinnerRowModel175.setValue("XDR");
        currencyList.add(spinnerRowModel175);
        SpinnerRowModel spinnerRowModel176 = new SpinnerRowModel();
        spinnerRowModel176.setLabel("XOF");
        spinnerRowModel176.setValue("XOF");
        currencyList.add(spinnerRowModel176);
        SpinnerRowModel spinnerRowModel177 = new SpinnerRowModel();
        spinnerRowModel177.setLabel("XPF");
        spinnerRowModel177.setValue("XPF");
        currencyList.add(spinnerRowModel177);
        SpinnerRowModel spinnerRowModel178 = new SpinnerRowModel();
        spinnerRowModel178.setLabel("YER");
        spinnerRowModel178.setValue("YER");
        currencyList.add(spinnerRowModel178);
        SpinnerRowModel spinnerRowModel179 = new SpinnerRowModel();
        spinnerRowModel179.setLabel("ZAR");
        spinnerRowModel179.setValue("R");
        currencyList.add(spinnerRowModel179);
        SpinnerRowModel spinnerRowModel180 = new SpinnerRowModel();
        spinnerRowModel180.setLabel("ZMK");
        spinnerRowModel180.setValue("ZMK");
        currencyList.add(spinnerRowModel180);
        SpinnerRowModel spinnerRowModel181 = new SpinnerRowModel();
        spinnerRowModel181.setLabel("ZWD");
        spinnerRowModel181.setValue("Z$");
        currencyList.add(spinnerRowModel181);
    }

    private int getSelectedPositionByCurrencyLabel(ArrayList<SpinnerRowModel> arrayList, String str) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (((SpinnerRowModel) arrayList.get(i)).getLabel().equalsIgnoreCase(str)) {
                return i;
            }
        }
        return 0;
    }


    public void setSelectionAllCurrency(ArrayList<SpinnerRowModel> arrayList, boolean z) {
        for (int i = 0; i < arrayList.size(); i++) {
            ((SpinnerRowModel) arrayList.get(i)).setSelected(z);
        }
    }

    private void setDateFormatPicker() {
        fillDateFormatList();
        selectedDateFormatPos = getSelectedPositionByDateFormatValue(dateFormatList, AppPref.getDateFormat(context));
        ((SpinnerRowModel) dateFormatList.get(selectedDateFormatPos)).setSelected(true);
        binding.spinnerDateFormat.setAdapter(new CustomSpinnerAdapter(getContext(), dateFormatList, false));
        binding.spinnerDateFormat.setSelection(selectedDateFormatPos);
        binding.spinnerDateFormat.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                setSelectionAllDateFormat(dateFormatList, false);
                selectedDateFormatPos = i;
                AppPref.setDateFormat(context, ((SpinnerRowModel) dateFormatList.get(selectedDateFormatPos)).getValue());
                ((SpinnerRowModel) dateFormatList.get(selectedDateFormatPos)).setSelected(true);
            }
        });
    }

    private void fillDateFormatList() {
        dateFormatList = new ArrayList<>();
        String str = ".";
        SpinnerRowModel spinnerRowModel = new SpinnerRowModel();
        StringBuilder sb = new StringBuilder();
        sb.append("MM");
        sb.append(str);
        sb.append("dd");
        sb.append(str);
        sb.append("yy");
        spinnerRowModel.setValue(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("10");
        sb2.append(str);
        sb2.append("28");
        sb2.append(str);
        sb2.append("15");
        spinnerRowModel.setLabel(sb2.toString());
        dateFormatList.add(spinnerRowModel);
        SpinnerRowModel spinnerRowModel2 = new SpinnerRowModel();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("dd");
        sb3.append(str);
        sb3.append("MM");
        sb3.append(str);
        sb3.append("yy");
        spinnerRowModel2.setValue(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("28");
        sb4.append(str);
        sb4.append("10");
        sb4.append(str);
        sb4.append("15");
        spinnerRowModel2.setLabel(sb4.toString());
        dateFormatList.add(spinnerRowModel2);
        SpinnerRowModel spinnerRowModel3 = new SpinnerRowModel();
        StringBuilder sb5 = new StringBuilder();
        sb5.append("yy");
        sb5.append(str);
        sb5.append("MM");
        sb5.append(str);
        sb5.append("dd");
        spinnerRowModel3.setValue(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("15");
        sb6.append(str);
        sb6.append("10");
        sb6.append(str);
        sb6.append("28");
        spinnerRowModel3.setLabel(sb6.toString());
        dateFormatList.add(spinnerRowModel3);
        SpinnerRowModel spinnerRowModel4 = new SpinnerRowModel();
        StringBuilder sb7 = new StringBuilder();
        sb7.append("MM");
        sb7.append(str);
        sb7.append("dd");
        sb7.append(str);
        sb7.append("yyyy");
        spinnerRowModel4.setValue(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append("10");
        sb8.append(str);
        sb8.append("28");
        sb8.append(str);
        sb8.append("2015");
        spinnerRowModel4.setLabel(sb8.toString());
        dateFormatList.add(spinnerRowModel4);
        SpinnerRowModel spinnerRowModel5 = new SpinnerRowModel();
        StringBuilder sb9 = new StringBuilder();
        sb9.append("dd");
        sb9.append(str);
        sb9.append("MM");
        sb9.append(str);
        sb9.append("yyyy");
        spinnerRowModel5.setValue(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append("28");
        sb10.append(str);
        sb10.append("10");
        sb10.append(str);
        sb10.append("2015");
        spinnerRowModel5.setLabel(sb10.toString());
        dateFormatList.add(spinnerRowModel5);
        SpinnerRowModel spinnerRowModel6 = new SpinnerRowModel();
        StringBuilder sb11 = new StringBuilder();
        sb11.append("yyyy");
        sb11.append(str);
        sb11.append("MM");
        sb11.append(str);
        sb11.append("dd");
        spinnerRowModel6.setValue(sb11.toString());
        StringBuilder sb12 = new StringBuilder();
        sb12.append("2015");
        sb12.append(str);
        sb12.append("10");
        sb12.append(str);
        sb12.append("28");
        spinnerRowModel6.setLabel(sb12.toString());
        dateFormatList.add(spinnerRowModel6);
        String str2 = "-";
        SpinnerRowModel spinnerRowModel7 = new SpinnerRowModel();
        StringBuilder sb13 = new StringBuilder();
        sb13.append("MM");
        sb13.append(str2);
        sb13.append("dd");
        sb13.append(str2);
        sb13.append("yy");
        spinnerRowModel7.setValue(sb13.toString());
        StringBuilder sb14 = new StringBuilder();
        sb14.append("10");
        sb14.append(str2);
        sb14.append("28");
        sb14.append(str2);
        sb14.append("15");
        spinnerRowModel7.setLabel(sb14.toString());
        dateFormatList.add(spinnerRowModel7);
        SpinnerRowModel spinnerRowModel8 = new SpinnerRowModel();
        StringBuilder sb15 = new StringBuilder();
        sb15.append("dd");
        sb15.append(str2);
        sb15.append("MM");
        sb15.append(str2);
        sb15.append("yy");
        spinnerRowModel8.setValue(sb15.toString());
        StringBuilder sb16 = new StringBuilder();
        sb16.append("28");
        sb16.append(str2);
        sb16.append("10");
        sb16.append(str2);
        sb16.append("15");
        spinnerRowModel8.setLabel(sb16.toString());
        dateFormatList.add(spinnerRowModel8);
        SpinnerRowModel spinnerRowModel9 = new SpinnerRowModel();
        StringBuilder sb17 = new StringBuilder();
        sb17.append("yy");
        sb17.append(str2);
        sb17.append("MM");
        sb17.append(str2);
        sb17.append("dd");
        spinnerRowModel9.setValue(sb17.toString());
        StringBuilder sb18 = new StringBuilder();
        sb18.append("15");
        sb18.append(str2);
        sb18.append("10");
        sb18.append(str2);
        sb18.append("28");
        spinnerRowModel9.setLabel(sb18.toString());
        dateFormatList.add(spinnerRowModel9);
        SpinnerRowModel spinnerRowModel10 = new SpinnerRowModel();
        StringBuilder sb19 = new StringBuilder();
        sb19.append("MM");
        sb19.append(str2);
        sb19.append("dd");
        sb19.append(str2);
        sb19.append("yyyy");
        spinnerRowModel10.setValue(sb19.toString());
        StringBuilder sb20 = new StringBuilder();
        sb20.append("10");
        sb20.append(str2);
        sb20.append("28");
        sb20.append(str2);
        sb20.append("2015");
        spinnerRowModel10.setLabel(sb20.toString());
        dateFormatList.add(spinnerRowModel10);
        SpinnerRowModel spinnerRowModel11 = new SpinnerRowModel();
        StringBuilder sb21 = new StringBuilder();
        sb21.append("dd");
        sb21.append(str2);
        sb21.append("MM");
        sb21.append(str2);
        sb21.append("yyyy");
        spinnerRowModel11.setValue(sb21.toString());
        StringBuilder sb22 = new StringBuilder();
        sb22.append("28");
        sb22.append(str2);
        sb22.append("10");
        sb22.append(str2);
        sb22.append("2015");
        spinnerRowModel11.setLabel(sb22.toString());
        dateFormatList.add(spinnerRowModel11);
        SpinnerRowModel spinnerRowModel12 = new SpinnerRowModel();
        StringBuilder sb23 = new StringBuilder();
        sb23.append("yyyy");
        sb23.append(str2);
        sb23.append("MM");
        sb23.append(str2);
        sb23.append("dd");
        spinnerRowModel12.setValue(sb23.toString());
        StringBuilder sb24 = new StringBuilder();
        sb24.append("2015");
        sb24.append(str2);
        sb24.append("10");
        sb24.append(str2);
        sb24.append("28");
        spinnerRowModel12.setLabel(sb24.toString());
        dateFormatList.add(spinnerRowModel12);
        String str3 = "/";
        SpinnerRowModel spinnerRowModel13 = new SpinnerRowModel();
        StringBuilder sb25 = new StringBuilder();
        sb25.append("MM");
        sb25.append(str3);
        sb25.append("dd");
        sb25.append(str3);
        sb25.append("yy");
        spinnerRowModel13.setValue(sb25.toString());
        StringBuilder sb26 = new StringBuilder();
        sb26.append("10");
        sb26.append(str3);
        sb26.append("28");
        sb26.append(str3);
        sb26.append("15");
        spinnerRowModel13.setLabel(sb26.toString());
        dateFormatList.add(spinnerRowModel13);
        SpinnerRowModel spinnerRowModel14 = new SpinnerRowModel();
        StringBuilder sb27 = new StringBuilder();
        sb27.append("dd");
        sb27.append(str3);
        sb27.append("MM");
        sb27.append(str3);
        sb27.append("yy");
        spinnerRowModel14.setValue(sb27.toString());
        StringBuilder sb28 = new StringBuilder();
        sb28.append("28");
        sb28.append(str3);
        sb28.append("10");
        sb28.append(str3);
        sb28.append("15");
        spinnerRowModel14.setLabel(sb28.toString());
        dateFormatList.add(spinnerRowModel14);
        SpinnerRowModel spinnerRowModel15 = new SpinnerRowModel();
        StringBuilder sb29 = new StringBuilder();
        sb29.append("yy");
        sb29.append(str3);
        sb29.append("MM");
        sb29.append(str3);
        sb29.append("dd");
        spinnerRowModel15.setValue(sb29.toString());
        StringBuilder sb30 = new StringBuilder();
        sb30.append("15");
        sb30.append(str3);
        sb30.append("10");
        sb30.append(str3);
        sb30.append("28");
        spinnerRowModel15.setLabel(sb30.toString());
        dateFormatList.add(spinnerRowModel15);
        SpinnerRowModel spinnerRowModel16 = new SpinnerRowModel();
        StringBuilder sb31 = new StringBuilder();
        sb31.append("MM");
        sb31.append(str3);
        sb31.append("dd");
        sb31.append(str3);
        sb31.append("yyyy");
        spinnerRowModel16.setValue(sb31.toString());
        StringBuilder sb32 = new StringBuilder();
        sb32.append("10");
        sb32.append(str3);
        sb32.append("28");
        sb32.append(str3);
        sb32.append("2015");
        spinnerRowModel16.setLabel(sb32.toString());
        dateFormatList.add(spinnerRowModel16);
        SpinnerRowModel spinnerRowModel17 = new SpinnerRowModel();
        StringBuilder sb33 = new StringBuilder();
        sb33.append("dd");
        sb33.append(str3);
        sb33.append("MM");
        sb33.append(str3);
        sb33.append("yyyy");
        spinnerRowModel17.setValue(sb33.toString());
        StringBuilder sb34 = new StringBuilder();
        sb34.append("28");
        sb34.append(str3);
        sb34.append("10");
        sb34.append(str3);
        sb34.append("2015");
        spinnerRowModel17.setLabel(sb34.toString());
        dateFormatList.add(spinnerRowModel17);
        SpinnerRowModel spinnerRowModel18 = new SpinnerRowModel();
        StringBuilder sb35 = new StringBuilder();
        sb35.append("yyyy");
        sb35.append(str3);
        sb35.append("MM");
        sb35.append(str3);
        sb35.append("dd");
        spinnerRowModel18.setValue(sb35.toString());
        StringBuilder sb36 = new StringBuilder();
        sb36.append("2015");
        sb36.append(str3);
        sb36.append("10");
        sb36.append(str3);
        sb36.append("28");
        spinnerRowModel18.setLabel(sb36.toString());
        dateFormatList.add(spinnerRowModel18);
        String str4 = " ";
        SpinnerRowModel spinnerRowModel19 = new SpinnerRowModel();
        StringBuilder sb37 = new StringBuilder();
        sb37.append("MMM");
        sb37.append(str4);
        sb37.append("dd");
        sb37.append(str4);
        sb37.append("yyyy");
        spinnerRowModel19.setValue(sb37.toString());
        StringBuilder sb38 = new StringBuilder();
        sb38.append("Oct");
        sb38.append(str4);
        sb38.append("28");
        sb38.append(str4);
        sb38.append("2015");
        spinnerRowModel19.setLabel(sb38.toString());
        dateFormatList.add(spinnerRowModel19);
        SpinnerRowModel spinnerRowModel20 = new SpinnerRowModel();
        StringBuilder sb39 = new StringBuilder();
        sb39.append("dd");
        sb39.append(str4);
        sb39.append("MMM");
        sb39.append(str4);
        sb39.append("yyyy");
        spinnerRowModel20.setValue(sb39.toString());
        StringBuilder sb40 = new StringBuilder();
        sb40.append("28");
        sb40.append(str4);
        sb40.append("Oct");
        sb40.append(str4);
        sb40.append("2015");
        spinnerRowModel20.setLabel(sb40.toString());
        dateFormatList.add(spinnerRowModel20);
        SpinnerRowModel spinnerRowModel21 = new SpinnerRowModel();
        StringBuilder sb41 = new StringBuilder();
        sb41.append("yyyy");
        sb41.append(str4);
        sb41.append("MMM");
        sb41.append(str4);
        sb41.append("dd");
        spinnerRowModel21.setValue(sb41.toString());
        StringBuilder sb42 = new StringBuilder();
        sb42.append("2015");
        sb42.append(str4);
        sb42.append("Oct");
        sb42.append(str4);
        sb42.append("28");
        spinnerRowModel21.setLabel(sb42.toString());
        dateFormatList.add(spinnerRowModel21);
        SpinnerRowModel spinnerRowModel22 = new SpinnerRowModel();
        StringBuilder sb43 = new StringBuilder();
        sb43.append("MMMM");
        sb43.append(str4);
        sb43.append("dd");
        sb43.append(str4);
        sb43.append("yyyy");
        spinnerRowModel22.setValue(sb43.toString());
        StringBuilder sb44 = new StringBuilder();
        sb44.append("October");
        sb44.append(str4);
        sb44.append("28");
        sb44.append(str4);
        sb44.append("2015");
        spinnerRowModel22.setLabel(sb44.toString());
        dateFormatList.add(spinnerRowModel22);
        SpinnerRowModel spinnerRowModel23 = new SpinnerRowModel();
        StringBuilder sb45 = new StringBuilder();
        sb45.append("dd");
        sb45.append(str4);
        sb45.append("MMMM");
        sb45.append(str4);
        sb45.append("yyyy");
        spinnerRowModel23.setValue(sb45.toString());
        StringBuilder sb46 = new StringBuilder();
        sb46.append("28");
        sb46.append(str4);
        sb46.append("October");
        sb46.append(str4);
        sb46.append("2015");
        spinnerRowModel23.setLabel(sb46.toString());
        dateFormatList.add(spinnerRowModel23);
        SpinnerRowModel spinnerRowModel24 = new SpinnerRowModel();
        StringBuilder sb47 = new StringBuilder();
        sb47.append("yyyy");
        sb47.append(str4);
        sb47.append("MMMM");
        sb47.append(str4);
        sb47.append("dd");
        spinnerRowModel24.setValue(sb47.toString());
        StringBuilder sb48 = new StringBuilder();
        sb48.append("2015");
        sb48.append(str4);
        sb48.append("October");
        sb48.append(str4);
        sb48.append("28");
        spinnerRowModel24.setLabel(sb48.toString());
        dateFormatList.add(spinnerRowModel24);
        SpinnerRowModel spinnerRowModel25 = new SpinnerRowModel();
        StringBuilder sb49 = new StringBuilder();
        sb49.append("EEE");
        sb49.append(str4);
        sb49.append("MMMM");
        sb49.append(str4);
        sb49.append("dd");
        sb49.append(str4);
        sb49.append("yyyy");
        spinnerRowModel25.setValue(sb49.toString());
        StringBuilder sb50 = new StringBuilder();
        sb50.append("Thu");
        sb50.append(str4);
        sb50.append("October");
        sb50.append(str4);
        sb50.append("28");
        sb50.append(str4);
        sb50.append("2015");
        spinnerRowModel25.setLabel(sb50.toString());
        dateFormatList.add(spinnerRowModel25);
        SpinnerRowModel spinnerRowModel26 = new SpinnerRowModel();
        StringBuilder sb51 = new StringBuilder();
        sb51.append("EEEEEE");
        sb51.append(str4);
        sb51.append("MMMM");
        sb51.append(str4);
        sb51.append("dd");
        sb51.append(str4);
        sb51.append("yyyy");
        spinnerRowModel26.setValue(sb51.toString());
        StringBuilder sb52 = new StringBuilder();
        sb52.append("Thursday");
        sb52.append(str4);
        sb52.append("October");
        sb52.append(str4);
        sb52.append("28");
        sb52.append(str4);
        sb52.append("2015");
        spinnerRowModel26.setLabel(sb52.toString());
        dateFormatList.add(spinnerRowModel26);
        String str5 = " ";
        SpinnerRowModel spinnerRowModel27 = new SpinnerRowModel();
        StringBuilder sb53 = new StringBuilder();
        sb53.append("MMM");
        sb53.append(str5);
        sb53.append("dd,");
        sb53.append(str5);
        sb53.append("yyyy");
        spinnerRowModel27.setValue(sb53.toString());
        StringBuilder sb54 = new StringBuilder();
        sb54.append("Oct");
        sb54.append(str5);
        sb54.append("28,");
        sb54.append(str5);
        sb54.append("2015");
        spinnerRowModel27.setLabel(sb54.toString());
        dateFormatList.add(spinnerRowModel27);
        SpinnerRowModel spinnerRowModel28 = new SpinnerRowModel();
        StringBuilder sb55 = new StringBuilder();
        sb55.append("dd");
        sb55.append(str5);
        sb55.append("MMM,");
        sb55.append(str5);
        sb55.append("yyyy");
        spinnerRowModel28.setValue(sb55.toString());
        StringBuilder sb56 = new StringBuilder();
        sb56.append("28");
        sb56.append(str5);
        sb56.append("Oct,");
        sb56.append(str5);
        sb56.append("2015");
        spinnerRowModel28.setLabel(sb56.toString());
        dateFormatList.add(spinnerRowModel28);
        SpinnerRowModel spinnerRowModel29 = new SpinnerRowModel();
        StringBuilder sb57 = new StringBuilder();
        sb57.append("MMMM");
        sb57.append(str5);
        sb57.append("dd,");
        sb57.append(str5);
        sb57.append("yyyy");
        spinnerRowModel29.setValue(sb57.toString());
        StringBuilder sb58 = new StringBuilder();
        sb58.append("October");
        sb58.append(str5);
        sb58.append("28,");
        sb58.append(str5);
        sb58.append("2015");
        spinnerRowModel29.setLabel(sb58.toString());
        dateFormatList.add(spinnerRowModel29);
        SpinnerRowModel spinnerRowModel30 = new SpinnerRowModel();
        StringBuilder sb59 = new StringBuilder();
        sb59.append("dd");
        sb59.append(str5);
        sb59.append("MMMM,");
        sb59.append(str5);
        sb59.append("yyyy");
        spinnerRowModel30.setValue(sb59.toString());
        StringBuilder sb60 = new StringBuilder();
        sb60.append("28");
        sb60.append(str5);
        sb60.append("October,");
        sb60.append(str5);
        sb60.append("2015");
        spinnerRowModel30.setLabel(sb60.toString());
        dateFormatList.add(spinnerRowModel30);
        SpinnerRowModel spinnerRowModel31 = new SpinnerRowModel();
        StringBuilder sb61 = new StringBuilder();
        sb61.append("EEE,");
        sb61.append(str5);
        sb61.append("MMMM");
        sb61.append(str5);
        sb61.append("dd,");
        sb61.append(str5);
        sb61.append("yyyy");
        spinnerRowModel31.setValue(sb61.toString());
        StringBuilder sb62 = new StringBuilder();
        sb62.append("Thu,");
        sb62.append(str5);
        sb62.append("October");
        sb62.append(str5);
        sb62.append("28,");
        sb62.append(str5);
        sb62.append("2015");
        spinnerRowModel31.setLabel(sb62.toString());
        dateFormatList.add(spinnerRowModel31);
        SpinnerRowModel spinnerRowModel32 = new SpinnerRowModel();
        StringBuilder sb63 = new StringBuilder();
        sb63.append("EEEEEE,");
        sb63.append(str5);
        sb63.append("MMMM");
        sb63.append(str5);
        sb63.append("dd,");
        sb63.append(str5);
        sb63.append("yyyy");
        spinnerRowModel32.setValue(sb63.toString());
        StringBuilder sb64 = new StringBuilder();
        sb64.append("Thursday,");
        sb64.append(str5);
        sb64.append("October");
        sb64.append(str5);
        sb64.append("28,");
        sb64.append(str5);
        sb64.append("2015");
        spinnerRowModel32.setLabel(sb64.toString());
        dateFormatList.add(spinnerRowModel32);
    }

    private int getSelectedPositionByDateFormatValue(ArrayList<SpinnerRowModel> arrayList, String str) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (((SpinnerRowModel) arrayList.get(i)).getValue().equalsIgnoreCase(str)) {
                return i;
            }
        }
        return 0;
    }


    public void setSelectionAllDateFormat(ArrayList<SpinnerRowModel> arrayList, boolean z) {
        for (int i = 0; i < arrayList.size(); i++) {
            ((SpinnerRowModel) arrayList.get(i)).setSelected(z);
        }
    }

    private void setTimeFormatPicker() {
        fillTimeFormatList();
        selectedTimeFormatPos = getSelectedPositionByTimeFormatValue(timeFormatList, AppPref.getTimeFormat(context));
        ((SpinnerRowModel) timeFormatList.get(selectedTimeFormatPos)).setSelected(true);
        binding.spinnerTimeFormat.setAdapter(new CustomSpinnerAdapter(getContext(), timeFormatList, false));
        binding.spinnerTimeFormat.setSelection(selectedTimeFormatPos);
        binding.spinnerTimeFormat.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                setSelectionAllTimeFormat(timeFormatList, false);
                selectedTimeFormatPos = i;
                AppPref.setTimeFormat(context, ((SpinnerRowModel) timeFormatList.get(selectedTimeFormatPos)).getValue());
                ((SpinnerRowModel) timeFormatList.get(selectedTimeFormatPos)).setSelected(true);
            }
        });
    }

    private void fillTimeFormatList() {
        timeFormatList = new ArrayList<>();
        String str = ":";
        SpinnerRowModel spinnerRowModel = new SpinnerRowModel();
        StringBuilder sb = new StringBuilder();
        sb.append("hh");
        sb.append(str);
        sb.append("mm a");
        spinnerRowModel.setValue(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("02");
        sb2.append(str);
        sb2.append("30 PM");
        spinnerRowModel.setLabel(sb2.toString());
        timeFormatList.add(spinnerRowModel);
        SpinnerRowModel spinnerRowModel2 = new SpinnerRowModel();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("HH");
        sb3.append(str);
        sb3.append("mm");
        spinnerRowModel2.setValue(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("14");
        sb4.append(str);
        sb4.append("30");
        spinnerRowModel2.setLabel(sb4.toString());
        timeFormatList.add(spinnerRowModel2);
    }

    private int getSelectedPositionByTimeFormatValue(ArrayList<SpinnerRowModel> arrayList, String str) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (((SpinnerRowModel) arrayList.get(i)).getValue().equalsIgnoreCase(str)) {
                return i;
            }
        }
        return 0;
    }


    public void setSelectionAllTimeFormat(ArrayList<SpinnerRowModel> arrayList, boolean z) {
        for (int i = 0; i < arrayList.size(); i++) {
            ((SpinnerRowModel) arrayList.get(i)).setSelected(z);
        }
    }

    private void checkPermAndBackup() {
        if (isHasPermissions(getContext(), "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")) {
            backupData();
            return;
        }
        requestPermissions(getContext(), getString(R.string.rationale_export), Constants.REQUEST_PERM_FILE, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
    }

    private void backupData() {
        showBackupDialog();
    }

    public void setBackupDialog() {
        dialogBackupBinding = (AlertDialogBackupBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.alert_dialog_backup_act, null, false);
        dialogBackup = new Dialog(context);
        dialogBackup.setContentView(dialogBackupBinding.getRoot());
        dialogBackup.getWindow().setBackgroundDrawableResource(17170445);
        dialogBackup.getWindow().setLayout(-1, -2);
        dialogBackupBinding.btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                backupData(dialogBackupBinding.radioLocal.isChecked());
                try {
                    dialogBackup.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialogBackupBinding.btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    dialogBackup.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showBackupDialog() {
        try {
            if (dialogBackup != null && !dialogBackup.isShowing()) {
                dialogBackup.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    private RemoteBackup remoteBackup;
//    private boolean isBackup = true;
    public void backupData(boolean z) {
//        remoteBackup = new RemoteBackup(getActivity());
//        isBackup = true;
//        remoteBackup.connectToDrive(isBackup);
        backupRestore.backupRestore(progressDialog, z, true, null, false, new OnBackupRestore() {
            public void getList(ArrayList<RestoreRowModel> arrayList) {
            }

            public void onSuccess(boolean z) {
                if (z) {
                    AppConstants.toastShort(context, context.getString(R.string.import_successfully));
                } else {
                    AppConstants.toastShort(context, context.getString(R.string.failed_to_import));
                }
            }
        });
    }


    private GoogleSignInClient buildGoogleSignInClient() {
        return GoogleSignIn.getClient(getActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope("https://www.googleapis.com/auth/drive.appdata"), new Scope[0]).requestEmail().requestProfile().build());
    }

    private boolean isHasPermissions(Context context, String... strArr) {
        return EasyPermissions.hasPermissions(context, strArr);
    }

    private void requestPermissions(Context context, String str, int i, String... strArr) {
        EasyPermissions.requestPermissions((Activity) context, str, i, strArr);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
    }

    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        if (i == 1053) {
            backupData();
        }
    }

    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(getActivity(), list)) {
            new Builder(getActivity()).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("errordfgfd", "fglhfdmb;" + resultCode);

        if (resultCode != -1) {
            return;
        }
        Log.e("errordfgfd", "fglhfdmb;" + requestCode);

        if (requestCode == 1002) {
            getActivity().setResult(-1);
        } else if (requestCode == 1005) {
            Log.e("errordfgfd", "fglhfdmb;");
            handleSignIn(data);
        }
    }

    /*
        public static final int REQUEST_CODE_SIGN_IN = 0;
        public static final int REQUEST_CODE_OPENING = 1;
        public static final int REQUEST_CODE_CREATION = 2;
        public static final int REQUEST_CODE_PERMISSIONS = 2;
        private static final String TAG = "Google Drive Activity";
        @Override
        public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            Log.e("errordfgfd", "fglhfdmb;");

            if (resultCode != -1) {
                return;
            }
            Log.e("errordfgfd", "fglhfdmb;");

            if (requestCode == 1002) {
                getActivity().setResult(-1);
                return;
            }
            switch (requestCode) {

                case REQUEST_CODE_SIGN_IN:
                    Log.i(TAG, "Sign in request code");
                    // Called after user is signed in.
                    if (resultCode == RESULT_OK) {
                        remoteBackup.connectToDrive(isBackup);
                    }
                    break;

                case REQUEST_CODE_CREATION:
                    // Called after a file is saved to Drive.
                    if (resultCode == RESULT_OK) {
                        Log.i(TAG, "Backup successfully saved.");
                        Toast.makeText(this, "Backup successufly loaded!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case REQUEST_CODE_OPENING:
                    if (resultCode == RESULT_OK) {
                        DriveId driveId = data.getParcelableExtra(
                                OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                        remoteBackup.mOpenItemTaskSource.setResult(driveId);
                    } else {
                        remoteBackup.mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                    }

            }
        }
    */
    private void handleSignIn(Intent intent) {
        backupRestore.handleSignInResult(intent, true, false, null, progressDialog, new OnBackupRestore() {
            public void getList(ArrayList<RestoreRowModel> arrayList) {
            }

            public void onSuccess(boolean z) {
            }
        });
    }


    private static final int REQUEST_CODE = 0;

    private void cancelIntents() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getNotificationPublisherIntent(null));
    }

    public PendingIntent getNotificationPublisherIntent(Notification notification) {
        Intent intent = new Intent(getContext(), NotificationPublisher.class);
        if (notification != null) {
            intent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        }

        return PendingIntent.getBroadcast(
                getContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void scheduleNotification(Calendar startTime) {
        scheduleIntent(getNotificationPublisherIntent(null), startTime);
    }

    public void scheduleIntent(PendingIntent intent, Calendar startTime) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, intent);
    }


    public static Calendar valueToCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, getHours(480));
        calendar.set(Calendar.MINUTE, getMinutes(480));
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    private static int getHours(int minAfterMidnight) {
        return minAfterMidnight / 60;
    }

    private static int getMinutes(int minAfterMidnight) {
        return minAfterMidnight % 60;
    }

}
