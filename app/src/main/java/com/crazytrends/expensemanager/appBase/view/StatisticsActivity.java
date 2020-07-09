package com.crazytrends.expensemanager.appBase.view;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.crazytrends.expensemanager.intro.Util;
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
import com.crazytrends.expensemanager.appBase.adapter.CustomSpinnerAdapter;
import com.crazytrends.expensemanager.appBase.adapter.StatisticCategoryAdapter;
import com.crazytrends.expensemanager.appBase.baseClass.BaseFragmentbind;
import com.crazytrends.expensemanager.appBase.models.StatisticsListModel;
import com.crazytrends.expensemanager.appBase.models.spinner.SpinnerRowModel;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.models.transaction.TransactionFilterModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.statistics.StatisticRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.BackgroundAsync;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import com.crazytrends.expensemanager.appBase.utils.OnAsyncBackground;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.appBase.utils.adBackScreenListener;
import com.crazytrends.expensemanager.databinding.ActivityStatisticsBinding;
import com.crazytrends.expensemanager.databinding.FragmentMainBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsActivity extends BaseFragmentbind implements adBackScreenListener {

    public ActivityStatisticsBinding binding;
    private Calendar calendar;
    private ArrayList<PieEntry> chartList;
    private AppDataBase db;
    private TransactionFilterModel filterModel;

    public StatisticsListModel model;

    public ArrayList<SpinnerRowModel> reportTypeList;

    public int selectedReportTypePos = 0;
    public ToolbarModel toolbarModel;
    public StatisticsActivity() {
    }
    public void callApi() {
    }


    public void setBinding(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        this.binding = (ActivityStatisticsBinding) DataBindingUtil.inflate(layoutInflater, R.layout.activity_statistics, viewGroup, false);

        this.db = AppDataBase.getAppDatabase(this.context);
        setModelDetail();
        this.binding.setStatisticsListModel(this.model);
    }


    public View getViewBinding() {
        return this.binding.getRoot();
    }

    private void setModelDetail() {
        this.model = new StatisticsListModel();
        this.model.setArrayList(new ArrayList());
        this.model.setNoDataIcon(R.drawable.no_data_transaction);
        this.model.setNoDataText(getString(R.string.noDataTitleTransaction));
        this.model.setNoDataDetail(getString(R.string.noDataDescTransaction));
        this.model.setNoDataIconTrip(R.drawable.no_data_transaction);
        this.model.setNoDataTextTrip(getString(R.string.noDataTitleTransaction));
        this.model.setNoDataDetailTrip(getString(R.string.noDataDescTransaction));
        setFilterDetail();
        this.model.setFilterModel(this.filterModel);
    }

    private void setFilterDetail() {
        this.filterModel = new TransactionFilterModel();
        this.calendar = Calendar.getInstance();
        this.calendar.set(11, 0);
        this.calendar.set(12, 0);
        this.calendar.set(13, 0);
        this.calendar.set(14, 0);
        this.filterModel.setToDateInMillis(this.calendar.getTimeInMillis());
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(this.calendar.getTimeInMillis());
        instance.add(5, -2);
        this.filterModel.setFromDateInMillis(instance.getTimeInMillis());
        this.filterModel.setCategoryArrayList(new ArrayList());
        this.filterModel.setCategoryList(new ArrayList());
        this.filterModel.setModeArrayList(new ArrayList());
        this.filterModel.setModeList(new ArrayList());
        fillCategory();
        fillModes();
    }

    private void fillCategory() {
        try {
            this.filterModel.getCategoryArrayList().addAll(this.db.categoryDao().getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillModes() {
        try {
            this.filterModel.getModeArrayList().addAll(this.db.modeDao().getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setToolbar() {
        this.toolbarModel = new ToolbarModel();
        this.toolbarModel.setTitle(getString(R.string.reports));
        this.toolbarModel.setDelete(true);
        setupFilterIcon();
        this.binding.includedToolbar.setToolbarModel(this.toolbarModel);
        ((AppCompatActivity)getActivity()).setSupportActionBar(this.binding.includedToolbar.toolbar);
    }


    public void setupFilterIcon() {
        this.binding.includedToolbar.imgDelete.setImageResource(this.model.getFilterModel().isFilter() ? R.drawable.filter : R.drawable.filter);
    }


    public void setOnClicks() {
        this.binding.includedToolbar.imgBack.setOnClickListener(this);
        this.binding.includedToolbar.imgDelete.setOnClickListener(this);
    }



    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack :
                getActivity().onBackPressed();
                return;
            case R.id.imgDelete :
                    openFilter();
                return;
            default:
                return;
        }
    }


    public void fillData() {
        new BackgroundAsync(this.context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
            }

            public void doInBackground() {
                fillDBData();
            }

            public void onPostExecute() {
                notifyAdapter();
            }
        }).execute(new Object[0]);
    }


    public void fillDBData() {
        try {
            this.model.getArrayList().addAll(this.db.statisticsDao().getFilterList(new SimpleSQLiteQuery(this.model.getFilterModel().getQueryFilter())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setRecycler() {
        this.binding.recycler.setNestedScrollingEnabled(false);
        this.binding.recycler.setLayoutManager(new LinearLayoutManager(this.context));
        this.binding.recycler.setAdapter(new StatisticCategoryAdapter(this.context, this.model.getArrayList(), new RecyclerItemClick() {
            public void onClick(int i, int i2) {
            }
        }));
    }


    public void notifyAdapter() {
        setViewVisibility();
        if (this.binding.recycler.getAdapter() != null) {
            this.binding.recycler.getAdapter().notifyDataSetChanged();
        }
    }

    @SuppressLint("WrongConstant")
    private void setViewVisibility() {
        int i = 8;
        this.binding.linData.setVisibility(isData() ? 0 : 8);
        LinearLayout linearLayout = this.binding.linNoData;
        if (!isData()) {
            i = 0;
        }
        linearLayout.setVisibility(i);
    }

    private boolean isData() {
        if (!this.model.getFilterModel().getStatisticsByType().equalsIgnoreCase(Constants.STATISTICS_BY_TYPE)) {
            return this.model.isListData();
        }
        for (int i = 0; i < this.model.getArrayList().size(); i++) {
            if (((StatisticRowModel) this.model.getArrayList().get(i)).getCatTotal() > Utils.DOUBLE_EPSILON) {
                return true;
            }
        }
        return false;
    }


    public void initMethods() {
        setViewVisibility();
        initChart();
        setChartData();
        setReportTypePicker();
    }

    private void initChart() {
        this.binding.chart.setUsePercentValues(true);
        this.binding.chart.getDescription().setText("");
        this.binding.chart.getDescription().setEnabled(false);
        this.binding.chart.setDrawHoleEnabled(true);
        this.binding.chart.setHoleColor(-1);
        this.binding.chart.setTransparentCircleColor(-1);
        this.binding.chart.setTransparentCircleAlpha(120);
        this.binding.chart.setHoleRadius(28.0f);
        this.binding.chart.setTransparentCircleRadius(36.0f);
        this.binding.chart.setRotationAngle(0.0f);
        this.binding.chart.setRotationEnabled(true);
        this.binding.chart.setHighlightPerTapEnabled(true);
        this.binding.chart.setDrawEntryLabels(false);
        this.binding.chart.setDrawSlicesUnderHole(false);
        this.binding.chart.animateY(1200, Easing.EaseInOutQuad);
        this.binding.chart.setEntryLabelColor(-1);
        this.binding.chart.setEntryLabelTextSize(12.0f);
        this.binding.chart.getLegend().setWordWrapEnabled(true);
        this.binding.chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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
        this.chartList = new ArrayList<>();
        fillChartData();
        PieDataSet pieDataSet = new PieDataSet(this.chartList, "");
        pieDataSet.setDrawIcons(false);
        pieDataSet.setIconsOffset(new MPPointF(0.0f, 40.0f));
        pieDataSet.setSelectionShift(5.0f);
        ArrayList arrayList = new ArrayList();
        for (int valueOf : AppConstants.CUSTOM_COLORS) {
            arrayList.add(Integer.valueOf(valueOf));
        }
        pieDataSet.setColors((List<Integer>) arrayList);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(this.binding.chart));
        pieData.setValueTextSize(12.0f);
        pieData.setValueTextColor(-1);
        this.binding.chart.setData(pieData);
        this.binding.chart.highlightValues(null);
        this.binding.chart.invalidate();
    }


    public void fillChartData() {
        this.chartList.clear();
        for (int i = 0; i < this.model.getArrayList().size(); i++) {
            this.chartList.add(new PieEntry(Float.valueOf((float) ((StatisticRowModel) this.model.getArrayList().get(i)).getCatTotal()).floatValue(), ((StatisticRowModel) this.model.getArrayList().get(i)).getName()));
        }
    }

    private void setReportTypePicker() {
        fillReportTypeList();
        this.selectedReportTypePos = getSelectedPositionByReportTypeValue(this.reportTypeList, Constants.STATISTICS_BY_TYPE);
        ((SpinnerRowModel) this.reportTypeList.get(this.selectedReportTypePos)).setSelected(true);
        this.binding.spinnerTimeFormat.setAdapter(new CustomSpinnerAdapter(getContext(), this.reportTypeList, true));
        this.binding.spinnerTimeFormat.setSelection(this.selectedReportTypePos);
        this.binding.spinnerTimeFormat.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                setSelectionAllReportType(reportTypeList, false);
                selectedReportTypePos = i;
                ((SpinnerRowModel) reportTypeList.get(selectedReportTypePos)).setSelected(true);
                model.getFilterModel().setStatisticsByType(((SpinnerRowModel) reportTypeList.get(selectedReportTypePos)).getValue());
                applyFilter();
            }
        });
    }

    private void fillReportTypeList() {
        this.reportTypeList = new ArrayList<>();
        SpinnerRowModel spinnerRowModel = new SpinnerRowModel();
        spinnerRowModel.setValue(Constants.STATISTICS_BY_TYPE);
        this.reportTypeList.add(spinnerRowModel);
        SpinnerRowModel spinnerRowModel2 = new SpinnerRowModel();
        spinnerRowModel2.setValue(Constants.STATISTICS_BY_CATEGORY);
        this.reportTypeList.add(spinnerRowModel2);
        SpinnerRowModel spinnerRowModel3 = new SpinnerRowModel();
        spinnerRowModel3.setValue(Constants.STATISTICS_BY_MODE);
        this.reportTypeList.add(spinnerRowModel3);
    }

    private int getSelectedPositionByReportTypeValue(ArrayList<SpinnerRowModel> arrayList, String str) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (((SpinnerRowModel) arrayList.get(i)).getValue().equalsIgnoreCase(str)) {
                return i;
            }
        }
        return 0;
    }


    public void setSelectionAllReportType(ArrayList<SpinnerRowModel> arrayList, boolean z) {
        for (int i = 0; i < arrayList.size(); i++) {
            ((SpinnerRowModel) arrayList.get(i)).setSelected(z);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 1004) {
            updateListFilter(intent);
        }
    }

    private void openFilter() {
        Intent intent = new Intent(this.context, AddEditTransactionFilterActivity.class);
        intent.putExtra(AddEditTransactionFilterActivity.EXTRA_MODEL, this.model.getFilterModel());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1004);
    }

    private void updateListFilter(Intent intent) {
        if (intent != null) {
            try {
                if (intent.hasExtra(AddEditTransactionActivity.EXTRA_MODEL)) {
                    this.model.setFilterModel((TransactionFilterModel) intent.getParcelableExtra(AddEditTransactionActivity.EXTRA_MODEL));
                    applyFilter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void applyFilter() {
        new BackgroundAsync(this.context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
                model.getArrayList().clear();
            }

            public void doInBackground() {
                fillDBData();
            }

            public void onPostExecute() {
                notifyAdapter();
                fillChartData();
                binding.chart.notifyDataSetChanged();
                binding.chart.invalidate();
                setupFilterIcon();
            }
        }).execute(new Object[0]);
    }

    @Override
    public void BackScreen() {
        getActivity().onBackPressed();
    }
}
