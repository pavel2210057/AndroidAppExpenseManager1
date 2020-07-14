package com.crazytrends.expensemanager.appBase.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.MyApp;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.appBase.baseClass.BaseActivityBinding;
import com.crazytrends.expensemanager.appBase.models.StatisticsListModel;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.models.transaction.TransactionFilterModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.statistics.StatisticRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.transation.TransactionRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import com.crazytrends.expensemanager.databinding.ActivityMainBinding;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainAct extends BaseActivityBinding {
    static Context maincontext;
    private ActivityMainBinding binding;
    private Calendar calendar;
    private ArrayList<PieEntry> chartList;
    private AppDataBase db;
    private TransactionFilterModel filterModel;
    private StatisticsListModel model;
    private ToolbarModel toolbarModel;
    private Dialog dialog;


    public void setBinding() {


        binding = DataBindingUtil.setContentView(MainAct.this, R.layout.act_main);
        db = AppDataBase.getAppDatabase(context);
        maincontext = context;
        setModelDetail();
        binding.setStatisticsListModel(model);


        dialog = new Dialog(MainAct.this);
        dialog.setContentView(R.layout.alert_dialog_language_act);

        final TextView btnno = dialog.findViewById(R.id.btnCancel);
        final TextView btnyes = dialog.findViewById(R.id.btnOk);
        final RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);

        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        SharedPreferences prefs = getSharedPreferences("language", MODE_PRIVATE);

        int idName = prefs.getInt("idName", 0);
        switch (idName) {
            case  R.id.radioEnglish:
                radioGroup.check( R.id.radioEnglish);
                break;
            case R.id.radioHindi:
                radioGroup.check(R.id.radioHindi);
                break;
            case R.id.radioUrdu:
                radioGroup.check(R.id.radioUrdu);
                break;
            case R.id.radioGujrati:
                radioGroup.check(R.id.radioGujrati);
                break;
            default:
                radioGroup.check( R.id.radioEnglish);
                break;
        }
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("language", MODE_PRIVATE).edit();
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioEnglish) {
                    Locale eng = new Locale("en", "IN");
                    updateLocale(eng);
                    editor.putInt("idName", R.id.radioEnglish);
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioHindi) {
                    Locale Hindi = new Locale("hi", "IN");
                    updateLocale(Hindi);
                    editor.putInt("idName", R.id.radioHindi);
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioUrdu) {
                    Locale urdu = new Locale("ur", "IN");
                    updateLocale(urdu);
                    editor.putInt("idName",  R.id.radioUrdu);
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioGujrati) {
                    Locale guj = new Locale("gu", "IN");
                    updateLocale(guj);
                    editor.putInt("idName", R.id.radioGujrati);
                } else {
                }
                editor.apply();
                dialog.dismiss();

            }
        });

    }

    private void setModelDetail() {
        model = new StatisticsListModel();
        model.setArrayList(new ArrayList());
        model.setNoDataIcon(R.drawable.no_data_transaction);
        model.setNoDataText(getString(R.string.noDataTitleTransaction));
        model.setNoDataDetail(getString(R.string.noDataDescTransaction));
        model.setNoDataIconTrip(R.drawable.no_data_transaction);
        model.setNoDataTextTrip(getString(R.string.noDataTitleTransaction));
        model.setNoDataDetailTrip(getString(R.string.noDataDescTransaction));
        setFilterDetail();
        model.setFilterModel(filterModel);
    }

    private void setFilterDetail() {
        filterModel = new TransactionFilterModel();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        filterModel.setToDateInMillis(calendar.getTimeInMillis());
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(calendar.getTimeInMillis());
        instance.add(Calendar.DATE, -2);
        filterModel.setFromDateInMillis(instance.getTimeInMillis());
        filterModel.setCategoryArrayList(new ArrayList());
        filterModel.setCategoryList(new ArrayList());
        filterModel.setModeArrayList(new ArrayList());
        filterModel.setModeList(new ArrayList());
        filterModel.setMainFilter(true);
        setFilterDates(Constants.SORT_TYPE_THIS_MONTH, false);
    }

    private void setFilterDates(int i, boolean z) {
        filterModel.setSortType(i);
        if (!z) {
            filterModel.setDateFilter(false);
        }
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        Calendar instance2 = Calendar.getInstance();
        instance2.setTimeInMillis(instance.getTimeInMillis());
        if (i == Constants.SORT_TYPE_YESTERDAY) {
            instance2.add(Calendar.DATE, -1);
            instance.add(Calendar.DATE, -1);
        } else if (i == Constants.SORT_TYPE_LAST_WEEK) {
            Calendar instance3 = Calendar.getInstance();
            instance3.setTimeInMillis(AppConstants.getWeekFirstDate(instance2.getTimeInMillis(), 1).getTimeInMillis());
            instance3.add(Calendar.DATE, -7);
            instance2.setTimeInMillis(instance3.getTimeInMillis());
            instance.setTimeInMillis(AppConstants.getWeekLastDate(instance3.getTimeInMillis()).getTimeInMillis());
        } else if (i == Constants.SORT_TYPE_THIS_MONTH) {
            instance2.set(Calendar.DATE, 1);
            Calendar instance4 = Calendar.getInstance();
            instance4.setTimeInMillis(instance2.getTimeInMillis());
            instance4.add(Calendar.MONTH, 1);
            instance4.add(Calendar.DATE, -1);
            instance.setTimeInMillis(instance4.getTimeInMillis());
        } else if (i == Constants.SORT_TYPE_LAST_MONTH) {
            instance2.add(Calendar.MONTH, -1);
            instance2.set(Calendar.DATE, 1);
            Calendar instance5 = Calendar.getInstance();
            instance5.setTimeInMillis(instance2.getTimeInMillis());
            instance5.add(Calendar.MONTH, 1);
            instance5.add(Calendar.DATE, -1);
            instance.setTimeInMillis(instance5.getTimeInMillis());
        } else if (i == Constants.SORT_TYPE_BETWEEN_DATES || i == Constants.SORT_TYPE_ALL) {
            instance2.add(Calendar.DATE, -2);
        }
        filterModel.setFromDateInMillis(instance2.getTimeInMillis());
        filterModel.setToDateInMillis(instance.getTimeInMillis());
        StringBuilder sb = new StringBuilder();
        sb.append("model.getFromDateInMillis() : ");
        sb.append(AppConstants.getFormattedDate(filterModel.getFromDateInMillis(), AppPref.getDateFormat(MyApp.getInstance())));
        Log.i("setFilterDates", sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("model.getToDateInMillis() : ");
        sb2.append(AppConstants.getFormattedDate(filterModel.getToDateInMillis(), AppPref.getDateFormat(MyApp.getInstance())));
        Log.i("setFilterDates", sb2.toString());
    }


    public void setToolbar() {
        toolbarModel = new ToolbarModel();
        toolbarModel.setTitle(getString(R.string.app_name));
        toolbarModel.setBackMenu(false);
        toolbarModel.setAdd(true);
        toolbarModel.setHome(true);
        binding.includedToolbar.imgAdd.setImageResource(R.drawable.btn_language);
        binding.includedToolbar.setToolbarModel(toolbarModel);
        setSupportActionBar(binding.includedToolbar.toolbar);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.drawer_privacy_policy :
                AppConstants.openUrl(context, Constants.PRIVACY_POLICY_URL);
                return true;
            case R.id.drawer_ratting :
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case R.id.drawer_share :
                AppConstants.shareApp(context);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }


    public void setOnClicks() {
        color();
        binding.ivHome.setColorFilter(ContextCompat.getColor(context, R.color.primey), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.includedToolbar.imgAdd.setOnClickListener(this);
        binding.linAddIncome.setOnClickListener(this);
        binding.linAddExpense.setOnClickListener(this);
        binding.ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color();
                visibul();
                binding.ivHome.setColorFilter(ContextCompat.getColor(context, R.color.primey), android.graphics.PorterDuff.Mode.MULTIPLY);
                refreshChart();
            }
        });

        binding.ivReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color();
                gone();
                binding.ivReports.setColorFilter(ContextCompat.getColor(context, R.color.primey), android.graphics.PorterDuff.Mode.MULTIPLY);
                Fragment fragment;
                fragment = new StatisticsAct();
                loadFragment(fragment);
            }
        });

        binding.ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color();
                gone();
                binding.ivSetting.setColorFilter(ContextCompat.getColor(context, R.color.primey), android.graphics.PorterDuff.Mode.MULTIPLY);
                Fragment fragment;
                fragment = new SettingActivity();
                loadFragment(fragment);
            }
        });

        binding.ivTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color();
                gone();
                binding.ivTransaction.setColorFilter(ContextCompat.getColor(context, R.color.primey), android.graphics.PorterDuff.Mode.MULTIPLY);
                Fragment fragment;
                fragment = new TransactionListAct();
                loadFragment(fragment);

            }
        });


    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void color() {
        binding.ivHome.setColorFilter(ContextCompat.getColor(context, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.ivReports.setColorFilter(ContextCompat.getColor(context, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.ivSetting.setColorFilter(ContextCompat.getColor(context, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.ivTransaction.setColorFilter(ContextCompat.getColor(context, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void gone() {
        if (binding.rlhome.getVisibility() == View.VISIBLE) {
            binding.rlhome.setVisibility(View.GONE);
        }
    }

    private void visibul() {
        if (binding.rlhome.getVisibility() == View.GONE) {
            binding.rlhome.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.linAddExpense :
                z=false;

                    addItem();

                return;
            case R.id.linAddIncome :
                z=true;
                    addItem();
                return;
            case R.id.imgAdd :

                dialog.show();
                return;
            default:
                return;
        }
    }


    public void initMethods() {
        fillData();
        initChart();
        setChartData();
        setChartDetails();
    }

    boolean z = true;

    private void addItem() {
        TransactionRowModel transactionRowModel = new TransactionRowModel();
        transactionRowModel.setId(AppConstants.getUniqueId());
        String defaultCategoryId = AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_BUSINESS);
        transactionRowModel.setCategoryId(defaultCategoryId);
        transactionRowModel.setCategoryRowModel(db.categoryDao().getDetail(defaultCategoryId));
        String defaultModeId = AppConstants.getDefaultModeId(Constants.MODE_TYPE_CASH);
        transactionRowModel.setModeId(defaultModeId);
        transactionRowModel.setModeRowModel(db.modeDao().getDetail(defaultModeId));
        if (z) {
            transactionRowModel.setType(Constants.CAT_TYPE_INCOME);
        } else {
            transactionRowModel.setType(Constants.CAT_TYPE_EXPENSE);
        }
        openItemDetail(-1, transactionRowModel, false);
    }

    private void openItemDetail(int i, TransactionRowModel transactionRowModel, boolean z) {
        Intent intent = new Intent(context, AddEditTransactionAct.class);
        intent.putExtra(AddEditTransactionAct.EXTRA_IS_EDIT, z);
        intent.putExtra(AddEditTransactionAct.EXTRA_POSITION, i);
        intent.putExtra(AddEditTransactionAct.EXTRA_MODEL, transactionRowModel);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1002);
    }


    public void fillData() {
        model.setArrayList(new ArrayList());
        try {
            model.getArrayList().addAll(db.statisticsDao().getFilterList(new SimpleSQLiteQuery(model.getFilterModel().getQueryFilter())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setViewVisibility();
    }

    private void setViewVisibility() {
        int i = 8;
        binding.linData.setVisibility(isData() ? View.VISIBLE : View.GONE);
        LinearLayout linearLayout = binding.linNoData;
        if (!isData()) {
            i = 0;
        }
        linearLayout.setVisibility(i);
    }

    private boolean isData() {
        if (!model.getFilterModel().getStatisticsByType().equalsIgnoreCase(Constants.STATISTICS_BY_TYPE)) {
            return model.isListData();
        }
        for (int i = 0; i < model.getArrayList().size(); i++) {
            if (((StatisticRowModel) model.getArrayList().get(i)).getCatTotal() > Utils.DOUBLE_EPSILON) {
                return true;
            }
        }
        return false;
    }

    private void initChart() {
        binding.chart.setUsePercentValues(true);
        binding.chart.getDescription().setText("");
        binding.chart.getDescription().setEnabled(true);
        binding.chart.setDrawHoleEnabled(true);
        binding.chart.setHoleColor(-1);
        binding.chart.setTransparentCircleColor(-1);
        binding.chart.setTransparentCircleAlpha(120);
        binding.chart.setCenterTextSize(10);
        binding.chart.setHoleRadius(35.0f);
        binding.chart.setTransparentCircleRadius(36.0f);
        binding.chart.setRotationAngle(0.0f);
        binding.chart.setRotationEnabled(true);
        binding.chart.setHighlightPerTapEnabled(true);
        binding.chart.setDrawEntryLabels(false);
        binding.chart.setDrawSlicesUnderHole(false);
        binding.chart.animateY(1200, Easing.EaseInOutQuad);
        binding.chart.setEntryLabelColor(-1);
        binding.chart.setEntryLabelTextSize(12.0f);
        binding.chart.getLegend().setWordWrapEnabled(true);
        binding.chart.setCenterText(AppConstants.getFormattedDate(System.currentTimeMillis(), (DateFormat) Constants.SIMPLE_DATE_FORMAT_HEADER));
        binding.chart.getLegend().setEnabled(false);
        binding.chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            public void onValueSelected(Entry entry, Highlight highlight) {
                if (entry != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Value: ");
                    sb.append(entry.getY());
                    sb.append(", index: ");
                    sb.append(highlight.getX());
                    sb.append(", DataSet index: ");
                    sb.append(highlight.getDataSetIndex());
                    Log.i("VAL SELECTED", sb.toString());
                }
            }

            public void onNothingSelected() {
                Log.i("PieChart", "nothing selected");
            }
        });
    }

    private void setChartData() {
        chartList = new ArrayList<>();
        fillChartData();
        PieDataSet pieDataSet = new PieDataSet(chartList, "");
        pieDataSet.setDrawIcons(false);
        pieDataSet.setIconsOffset(new MPPointF(0.0f, 40.0f));
        pieDataSet.setSelectionShift(5.0f);
        ArrayList arrayList = new ArrayList();
        for (int valueOf : AppConstants.CUSTOM_COLORS) {
            arrayList.add(Integer.valueOf(valueOf));
        }
        pieDataSet.setColors((List<Integer>) arrayList);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(binding.chart));
        pieData.setValueTextSize(12.0f);
        pieData.setValueTextColor(-1);
        binding.chart.setData(pieData);
        binding.chart.highlightValues(null);
        binding.chart.invalidate();
    }

    private void fillChartData() {
        chartList.clear();
        for (int i = 0; i < model.getArrayList().size(); i++) {
            chartList.add(new PieEntry(Float.valueOf((float) ((StatisticRowModel) model.getArrayList().get(i)).getCatTotal()).floatValue(), ((StatisticRowModel) model.getArrayList().get(i)).getName()));
        }
    }

    private void setChartDetails() {
        TextView textView = binding.txtMonth;
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.currentMonthChart));
        sb.append(" ");
        sb.append(AppConstants.getFormattedDate(System.currentTimeMillis(), (DateFormat) Constants.SIMPLE_DATE_FORMAT_HEADER));
        textView.setText(sb.toString());
        double d = Utils.DOUBLE_EPSILON;
        double d2 = 0.0d;
        for (int i = 0; i < model.getArrayList().size(); i++) {
            if (((StatisticRowModel) model.getArrayList().get(i)).getName().equalsIgnoreCase(getString(R.string.income))) {
                d = ((StatisticRowModel) model.getArrayList().get(i)).getCatTotal();
                TextView textView2 = binding.txtIncome;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(getString(R.string.income));
                sb2.append("\n");
                sb2.append(((StatisticRowModel) model.getArrayList().get(i)).getCatTotalLabel());
                textView2.setText(sb2.toString());
            } else {
                d2 = ((StatisticRowModel) model.getArrayList().get(i)).getCatTotal();
                TextView textView3 = binding.txtExpense;
                StringBuilder sb3 = new StringBuilder();
                sb3.append(getString(R.string.expense));
                sb3.append("\n");
                sb3.append(((StatisticRowModel) model.getArrayList().get(i)).getCatTotalLabel());
                textView3.setText(sb3.toString());
            }
        }
        TextView textView4 = binding.txtBalance;
        StringBuilder sb4 = new StringBuilder();
        sb4.append(getString(R.string.balance));
        sb4.append("\n");
        sb4.append(AppPref.getCurrencySymbol(context));
        sb4.append("");
        sb4.append(AppConstants.getFormattedPrice(d - d2));
        textView4.setText(sb4.toString());
    }


    public void onActivityResult(int i, int i2, @Nullable Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(i, i2, intent);
        }
        if (i2 != -1) {
            return;
        }
        if (i == 1002) {
            refreshChart();
        } else if (i == 1004) {
            refreshChart();
        }
    }

    private void refreshChart() {
        fillData();
        fillChartData();
        binding.chart.notifyDataSetChanged();
        binding.chart.invalidate();
        setChartDetails();
    }

    public void onBackPressed() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        } else {
            final Dialog dialog = new Dialog(MainAct.this);
            dialog.setContentView(R.layout.adview_layout_exit_act);


            Button btnno = dialog.findViewById(R.id.btnno);
            Button btnyes = dialog.findViewById(R.id.btnyes);



            btnno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });


            btnyes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                    finish();
                    System.exit(0);
                }
            });


            dialog.show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
