package com.crazytrends.expensemanager.appBase.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.databinding.AlertDialogPdfReportBinding;
import com.crazytrends.expensemanager.databinding.AlertDialogRestoreBinding;
import com.crazytrends.expensemanager.databinding.AlertDialogTwoButtonBinding;


import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AppConstants {
    public static final int[] CUSTOM_COLORS = {ColorTemplate.rgb("#49a142"), ColorTemplate.rgb("#ff4a4a"), ColorTemplate.rgb("#2987e8"), ColorTemplate.rgb("#f57c4f"), ColorTemplate.rgb("#cb93ee"), ColorTemplate.rgb("#c2d468"), ColorTemplate.rgb("#4ec2e7"), ColorTemplate.rgb("#2F4F4F"), ColorTemplate.rgb("#D2691E"), ColorTemplate.rgb("#FFD700"), ColorTemplate.rgb("#20B2AA"), ColorTemplate.rgb("#00CED1"), ColorTemplate.rgb("#DEB887"), ColorTemplate.rgb("#1E90FF"), ColorTemplate.rgb("#9ACD32"), ColorTemplate.rgb("#FF6347"), ColorTemplate.rgb("#228B22"), ColorTemplate.rgb("#DC143C"), ColorTemplate.rgb("#800000"), ColorTemplate.rgb("#556B2F"), ColorTemplate.rgb("#9932CC"), ColorTemplate.rgb("#32CD32"), ColorTemplate.rgb("#708090"), ColorTemplate.rgb("#F08080"), ColorTemplate.rgb("#DA70D6"), ColorTemplate.rgb("#FF8C00"), ColorTemplate.rgb("#9400D3"), ColorTemplate.rgb("#DAA520"), ColorTemplate.rgb("#4169E1"), ColorTemplate.rgb("#4682B4")};

    public static void logDebug(Context context, String str, String str2) {
        if (AppPref.isEnableDebugToast(context)) {
            StringBuilder sb = new StringBuilder();
            sb.append("toastDebug ->> ");
            sb.append(str);
            sb.append(" : \nmsg ->> ");
            sb.append(str2);
            sb.append(" : \ncontext ->> ");
            sb.append(context.getClass().getSimpleName());
            toastShort(context, sb.toString());
        } else if (AppPref.isEnableDebugLog(context)) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("context -->> ");
            sb2.append(context.getClass().getSimpleName());
            sb2.append(" msg -->> ");
            sb2.append(str2);
            logDebug(str, sb2.toString());
        } else {
            logDebug(str, str2);
        }
    }

    public static void logDebug(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append("logDebug -->> ");
        sb.append(str);
        Log.d(sb.toString(), str2);
    }

    @SuppressLint("WrongConstant")
    public static void toastShort(Context context, String str) {
        Toast.makeText(context, str, 0).show();
    }


    public static void shareApp(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            StringBuilder sb = new StringBuilder();
            sb.append("Expense Manager - Track your Expense\nBest expense manager for financial planning, expense tracking and reports\n- Add Income Description with Date and Time\n- Reports with great filters\n- Add Your Income or Expense details\n- Set Currency Symbol, Set Date Format\n- Reports for every Transactions\n\nhttps://play.google.com/store/apps/details?id=");
            sb.append(context.getPackageName());
            intent.putExtra("android.intent.extra.TEXT", sb.toString());
            context.startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            logDebug(context, "shareApp", e.toString());
        }
    }


    public static void openUrl(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
        intent.addFlags(1208483840);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Constants.PRIVACY_POLICY_URL)));
        }
    }

    public static String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static boolean isNotEmpty(Context context, EditText editText, String str) {
        if (editText == null || str == null) {
            return false;
        }
        if (!editText.getText().toString().trim().isEmpty()) {
            return true;
        }
        requestFocusAndError(context, editText, str);
        return false;
    }

    public static void requestFocusAndError(Context context, EditText editText, String str) {
        hideKeyboard(context, editText);
        editText.requestFocus();
        toastShort(context, str);
    }

    public static void hideKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getFormattedPriceEdit(double d) {
        return d != Utils.DOUBLE_EPSILON ? new DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.US)).format(d) : "0";
    }

    public static String getFormattedPrice(double d) {
        return d != Utils.DOUBLE_EPSILON ? new DecimalFormat("#,###.00", DecimalFormatSymbols.getInstance(Locale.US)).format(d) : "0.00";
    }

    public static String getSearchableTextPattern(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(str.toLowerCase());
        sb.append("%");
        return sb.toString();
    }

    public static long getCurrentDateInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getFormattedDate(long j, DateFormat dateFormat) {
        return dateFormat.format(new Date(j));
    }

    public static String getFormattedDate(long j, String str) {
        return new SimpleDateFormat(str).format(new Date(j));
    }

    public static long getMonthOnly(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        instance.set(5, 1);
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        Calendar instance2 = Calendar.getInstance();
        instance2.setTimeInMillis(instance.getTimeInMillis());
        instance2.add(2, 1);
        instance2.add(5, -1);
        return instance2.getTimeInMillis();
    }

    public static long getDateOnly(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        return instance.getTimeInMillis();
    }

    public static Calendar getWeekFirstDate(long j, int i) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        instance.set(7, i);
        return instance;
    }

    @SuppressLint("WrongConstant")
    public static Calendar getWeekLastDate(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        instance.add(5, 6);
        return instance;
    }

    public static int getCategoryIconId(String str) {
        char c;
        int hashCode = str.hashCode();
        if (hashCode == -1082186784) {
            if (str.equals(Constants.CAT_TYPE_BUSINESS)) {
                c = 0;
                switch (c) {
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }
        } else if (hashCode == 2373904 && str.equals(Constants.CAT_TYPE_LOAN)) {
            c = 1;
            switch (c) {
                case 0:
                    return R.drawable.add;
                case 1:
                    return R.drawable.add;
                default:
                    return 0;
            }
        }
        c = 65535;
        switch (c) {
            case 0:
                break;
            case 1:
                break;
        }
        return hashCode;
    }

    public static String getDefaultModeId(String str) {
        char c;
        switch (str.hashCode()) {
            case -1594336764:
                if (str.equals(Constants.MODE_TYPE_DEBIT_CARD)) {
                    c = 2;
                    break;
                }
            case 2092883:
                if (str.equals(Constants.MODE_TYPE_CASH)) {
                    c = 0;
                    break;
                }
            case 955363427:
                if (str.equals(Constants.MODE_TYPE_NET_BANKING)) {
                    c = 3;
                    break;
                }
            case 1304940503:
                if (str.equals(Constants.MODE_TYPE_CREDIT_CARD)) {
                    c = 1;
                    break;
                }
            case 2017320513:
                if (str.equals(Constants.MODE_TYPE_CHEQUE)) {
                    c = 4;
                    break;
                }
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return "1";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
            case 4:
                return "5";
            default:
                return "1";
        }
    }

    public static String getDefaultCategoryId(String str) {
        char c;
        switch (str.hashCode()) {
            case -2053342301:
                if (str.equals(Constants.CAT_TYPE_MERCHANDISE)) {
                    c = 12;
                    break;
                }
            case -1825851926:
                if (str.equals(Constants.CAT_TYPE_SALARY)) {
                    c = 2;
                    break;
                }
            case -1689537935:
                if (str.equals(Constants.CAT_TYPE_MEDICAL)) {
                    c = 11;
                    break;
                }
            case -1238034679:
                if (str.equals(Constants.CAT_TYPE_TRANSPORT)) {
                    c = 20;
                    break;
                }
            case -1082186784:
                if (str.equals(Constants.CAT_TYPE_BUSINESS)) {
                    c = 0;
                    break;
                }
            case -279816824:
                if (str.equals(Constants.CAT_TYPE_SHOPPING)) {
                    c = 18;
                    break;
                }
            case -238984614:
                if (str.equals(Constants.CAT_TYPE_HOSPITAL)) {
                    c = 9;
                    break;
                }
            case 71007:
                if (str.equals(Constants.CAT_TYPE_FUN)) {
                    c = 8;
                    break;
                }
            case 2195582:
                if (str.equals(Constants.CAT_TYPE_FOOD)) {
                    c = 6;
                    break;
                }
            case 2201046:
                if (str.equals(Constants.CAT_TYPE_FUEL)) {
                    c = 7;
                    break;
                }
            case 2373904:
                if (str.equals(Constants.CAT_TYPE_LOAN)) {
                    c = 1;
                    break;
                }
            case 2484052:
                if (str.equals(Constants.CAT_TYPE_PETS)) {
                    c = 16;
                    break;
                }
            case 2606936:
                if (str.equals(Constants.CAT_TYPE_TIPS)) {
                    c = 19;
                    break;
                }
            case 69915028:
                if (str.equals(Constants.CAT_TYPE_HOTEL)) {
                    c = 10;
                    break;
                }
            case 74534672:
                if (str.equals(Constants.CAT_TYPE_MOVIE)) {
                    c = '\r';
                    break;
                }
            case 76517104:
                if (str.equals(Constants.CAT_TYPE_OTHER)) {
                    c = 14;
                    break;
                }
            case 220997469:
                if (str.equals(Constants.CAT_TYPE_RESTAURANT)) {
                    c = 17;
                    break;
                }
            case 507808352:
                if (str.equals(Constants.CAT_TYPE_PERSONAL)) {
                    c = 15;
                    break;
                }
            case 1158492072:
                if (str.equals(Constants.CAT_TYPE_CLOTHING)) {
                    c = 3;
                    break;
                }
            case 1713211272:
                if (str.equals(Constants.CAT_TYPE_EDUCATION)) {
                    c = 5;
                    break;
                }
            case 2055300859:
                if (str.equals(Constants.CAT_TYPE_DRINKS)) {
                    c = 4;
                    break;
                }
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return "1";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
            case 4:
                return "5";
            case 5:
                return "6";
            case 6:
                return "7";
            case 7:
                return "8";
            case 8:
                return "9";
            case 9:
                return "10";
            case 10:
                return "11";
            case 11:
                return "12";
            case 12:
                return "13";
            case 13:
                return "14";
            case 14:
                return "15";
            case 15:
                return "16";
            case 16:
                return "17";
            case 17:
                return "18";
            case 18:
                return "19";
            case 19:
                return "20";
            case 20:
                return "21";
            default:
                return "1";
        }
    }

    public static String getResourcePathWithPackage(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("android.resource://");
        sb.append(context.getPackageName());
        sb.append("/");
        return sb.toString();
    }

    public static void showTwoButtonDialog(Context context, String str, String str2, boolean z, boolean z2, String str3, String str4, final TwoButtonDialogListener twoButtonDialogListener) {
        int i = 0;
        AlertDialogTwoButtonBinding alertDialogTwoButtonBinding = (AlertDialogTwoButtonBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.alert_dialog_two_button, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(alertDialogTwoButtonBinding.getRoot());
        dialog.setCancelable(z);
        dialog.getWindow().setBackgroundDrawableResource(17170445);
        alertDialogTwoButtonBinding.txtTitle.setText(str);
        alertDialogTwoButtonBinding.txtDec.setText(Html.fromHtml(str2));
        TextView textView = alertDialogTwoButtonBinding.btnCancel;
        if (!z2) {
            i = 8;
        }
        textView.setVisibility(i);
        alertDialogTwoButtonBinding.btnCancel.setText(str4);
        alertDialogTwoButtonBinding.btnOk.setText(str3);
        alertDialogTwoButtonBinding.btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                twoButtonDialogListener.onCancel();
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialogTwoButtonBinding.btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                twoButtonDialogListener.onOk();
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pdfReportDialog(Context context, final TwoButtonDialogListener twoButtonDialogListener) {
        AlertDialogPdfReportBinding alertDialogPdfReportBinding = (AlertDialogPdfReportBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.alert_dialog_pdf_report, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(alertDialogPdfReportBinding.getRoot());
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(17170445);
        alertDialogPdfReportBinding.imgAdd.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialogPdfReportBinding.btnReports.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                twoButtonDialogListener.onCancel();
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialogPdfReportBinding.btnExport.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                twoButtonDialogListener.onOk();
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileSize(long j) {
        if (j <= 0) {
            return "0 byte";
        }
        String[] strArr = {"byte", "kb", "mb", "gb", "tb"};
        double d = (double) j;
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        StringBuilder sb = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double pow = Math.pow(1024.0d, (double) log10);
        Double.isNaN(d);
        sb.append(decimalFormat.format(d / pow));
        sb.append(" ");
        sb.append(strArr[log10]);
        return sb.toString();
    }

    public static void showRestoreDialog(Context context, String str, String str2, boolean z, boolean z2, String str3, String str4, final TwoButtonDialogListener twoButtonDialogListener) {
        int i = 0;
        AlertDialogRestoreBinding alertDialogRestoreBinding = (AlertDialogRestoreBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.alert_dialog_restore, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(alertDialogRestoreBinding.getRoot());
        dialog.setCancelable(z);
        dialog.getWindow().setBackgroundDrawableResource(17170445);
        alertDialogRestoreBinding.txtTitle.setText(str);
        alertDialogRestoreBinding.txtDec.setText(Html.fromHtml(str2));
        TextView textView = alertDialogRestoreBinding.btnCancel;
        if (!z2) {
            i = 8;
        }
        textView.setVisibility(i);
        alertDialogRestoreBinding.btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialogRestoreBinding.btnDelete.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                twoButtonDialogListener.onOk();
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialogRestoreBinding.btnMerge.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                twoButtonDialogListener.onCancel();
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTempFile(Context context) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(getRootPath(context));
            sb.append("/temp");
            File file = new File(sb.toString());
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRootPath(Context context) {
        return new File(context.getDatabasePath(Constants.APP_DB_NAME).getParent()).getParent();
    }

    public static String getPrefPath(Context context) {
        return new File(new File(context.getDatabasePath(Constants.APP_DB_NAME).getParent()).getParent(), "shared_prefs").getAbsolutePath();
    }

    public static String getLocalFileDir() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append("/");
        sb.append(Constants.DB_BACKUP_DIRECTORY);
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static String getTempFileDir(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(getRootPath(context));
        sb.append("/temp");
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static String getLocalZipFilePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLocalFileDir());
        sb.append(File.separator);
        sb.append(getBackupName());
        return sb.toString();
    }

    public static String getRemoteZipFilePath(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(getTempFileDir(context));
        sb.append(File.separator);
        sb.append(getBackupName());
        return sb.toString();
    }

    public static String getBackupName() {
        StringBuilder sb = new StringBuilder();
        sb.append("Backup_");
        sb.append(getFormattedDate(System.currentTimeMillis(), (DateFormat) Constants.FILE_DATE_FORMAT));
        sb.append(".zip");
        return sb.toString();
    }
}
