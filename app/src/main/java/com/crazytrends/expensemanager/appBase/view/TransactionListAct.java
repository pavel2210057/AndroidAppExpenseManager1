package com.crazytrends.expensemanager.appBase.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.adapter.TransactionAdapter;
import com.crazytrends.expensemanager.appBase.baseClass.BaseFragmentbind;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.models.transaction.TransactionFilterModel;
import com.crazytrends.expensemanager.appBase.models.transaction.TransactionListModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.transation.TransactionRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.BackgroundAsync;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import com.crazytrends.expensemanager.appBase.utils.OnAsyncBackground;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.appBase.utils.adBackScreenListener;
import com.crazytrends.expensemanager.databinding.ActivityTransationListBinding;
import com.crazytrends.expensemanager.databinding.AlertDialogExportBinding;
import com.crazytrends.expensemanager.pdfReport.ReportRowModel;
import com.crazytrends.expensemanager.pdfReport.ReportsListAct;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import pub.devrel.easypermissions.AppSettingsDialog.Builder;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;
import pub.devrel.easypermissions.EasyPermissions.RationaleCallbacks;

public class TransactionListAct extends BaseFragmentbind implements PermissionCallbacks, RationaleCallbacks, adBackScreenListener {
    private ActivityTransationListBinding binding;
    private Calendar calendar;
    private CategoryRowModel catDetail = null;
    private AppDataBase db;

    public Dialog dialogExport;

    public AlertDialogExportBinding dialogExportBinding;
    private File dir;
    private Document document;
    private WritableSheet excelSheet;
    private String fileName = null;
    private TransactionFilterModel filterModel;
    private ArrayList<TransactionRowModel> mainList;
    private ModeRowModel modeDetail = null;

    public TransactionListModel model;
    private Paragraph paragraph;
    private String repoTitle = "List";
    private String repoType = "Transaction";
    private ToolbarModel toolbarModel;
    private WritableWorkbook workbook;
    private WritableCellFormat writableCellFormatHeader;
    private WritableCellFormat writableCellFormatHeaderBlue;
    private WritableCellFormat writableCellFormatHeaderGreen;
    private WritableCellFormat writableCellFormatHeaderRed;
    private WritableCellFormat writableCellFormatRow;
    private WritableCellFormat writableCellFormatRowGreen;
    private WritableCellFormat writableCellFormatRowRed;
    private PdfWriter writer = null;

    public TransactionListAct() {
    }

    public class PdfFooterPageEvent extends PdfPageEventHelper {
        public PdfFooterPageEvent() {
        }

        public void onEndPage(PdfWriter pdfWriter, Document document) {
            try {
                PdfContentByte directContent = pdfWriter.getDirectContent();
                StringBuilder sb = new StringBuilder();
                sb.append("Created by : ");
                sb.append(getString(R.string.app_name));
                ColumnText.showTextAligned(directContent, 1, new Phrase(sb.toString(), new Font(FontFamily.TIMES_ROMAN, 16.0f, 1)), document.leftMargin() + ((document.right() - document.left()) / 2.0f), document.bottom() + 10.0f, 0.0f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void callApi() {
    }

    public void onRationaleAccepted(int i) {
    }

    public void onRationaleDenied(int i) {
    }

    public View getViewBinding() {
        return this.binding.getRoot();
    }


    public void setBinding(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        this.binding = (ActivityTransationListBinding) DataBindingUtil.inflate(layoutInflater, R.layout.act_transation_list, viewGroup, false);
        this.db = AppDataBase.getAppDatabase(this.context);
        setModelDetail();
        this.binding.setTransactionListModel(this.model);
    }

    private void setModelDetail() {
        this.model = new TransactionListModel();
        this.model.setArrayList(new ArrayList());
        this.mainList = new ArrayList<>();
        this.model.setNoDataIcon(R.drawable.no_data_transaction);
        this.model.setNoDataText(getString(R.string.noDataTitleTransaction));
        this.model.setNoDataDetail(getString(R.string.noDataDescTransaction));
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
        this.toolbarModel.setTitle(getString(R.string.transactions));
        this.toolbarModel.setDelete(true);
        setupFilterIcon();
        this.toolbarModel.setOtherMenu(true);
        this.binding.includedToolbar.imgOther.setImageResource(R.drawable.export);
        this.binding.includedToolbar.setToolbarModel(this.toolbarModel);
    }


    public void setupFilterIcon() {
        this.binding.includedToolbar.imgDelete.setImageResource(this.model.getFilterModel().isFilter() ? R.drawable.filter : R.drawable.filter);
    }


    public void setOnClicks() {
        this.binding.includedToolbar.imgBack.setOnClickListener(this);
        this.binding.includedToolbar.imgAdd.setOnClickListener(this);
        this.binding.includedToolbar.imgDelete.setOnClickListener(this);
        this.binding.includedToolbar.imgOther.setOnClickListener(this);
        this.binding.fabAdd.setOnClickListener(this);
        this.binding.imgShow.setOnClickListener(this);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                addItem();
                return;
            case R.id.imgBack:
                getActivity().onBackPressed();
                return;
            case R.id.imgDelete:
                openFilter();
                return;
            case R.id.imgOther:
                checkPermExport();
                return;
            case R.id.imgShow:
                this.model.setShowSummary(!this.model.isShowSummary());
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
                fillFromDB();
            }

            public void onPostExecute() {
                notifyAdapter();
            }
        }).execute(new Object[0]);
    }


    public void fillFromDB() {
        ArrayList arrayList = new ArrayList();
        try {
            arrayList.addAll(this.db.transactionDao().getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < arrayList.size(); i++) {
            checkAndAddHeader((TransactionRowModel) arrayList.get(i), false, true, true);
        }
        this.model.getArrayList().addAll(this.mainList);
    }

    private void checkAndAddHeader(TransactionRowModel transactionRowModel, boolean z, boolean z2, boolean z3) {
        checkHeader(transactionRowModel, z2);
        if (z3) {
            try {
                this.catDetail = this.db.categoryDao().getDetail(transactionRowModel.getCategoryId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            transactionRowModel.setCategoryRowModel(new CategoryRowModel());
            transactionRowModel.setCategoryRowModel(this.catDetail);
            try {
                this.modeDetail = this.db.modeDao().getDetail(transactionRowModel.getModeId());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            transactionRowModel.setModeRowModel(new ModeRowModel());
            transactionRowModel.setModeRowModel(this.modeDetail);
        }
        try {
            transactionRowModel.setViewType(2);
            if (z2) {
                this.mainList.add(transactionRowModel);
            } else {
                this.model.getArrayList().add(transactionRowModel);
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        if (z) {
            sortByDateAndTime();
        }
    }

    private void checkHeader(TransactionRowModel transactionRowModel, boolean z) {
        TransactionRowModel dateHeader = getDateHeader(transactionRowModel);
        if (z) {
            if (!this.mainList.contains(dateHeader)) {
                this.mainList.add(dateHeader);
            }
        } else if (!this.model.getArrayList().contains(dateHeader)) {
            this.model.getArrayList().add(dateHeader);
        }
    }

    private TransactionRowModel getDateHeader(TransactionRowModel transactionRowModel) {
        TransactionRowModel transactionRowModel2 = new TransactionRowModel();
        transactionRowModel2.setDateTimeInMillis(transactionRowModel.getMonthOnly());
        transactionRowModel2.setViewType(1);
        return transactionRowModel2;
    }

    private void sortByDateAndTime() {
        try {
            Collections.sort(this.mainList, new Comparator<TransactionRowModel>() {
                @RequiresApi(api = 19)
                public int compare(TransactionRowModel transactionRowModel, TransactionRowModel transactionRowModel2) {
                    return (transactionRowModel2.getDateTimeInMillis() > transactionRowModel.getDateTimeInMillis() ? 1 : (transactionRowModel2.getDateTimeInMillis() == transactionRowModel.getDateTimeInMillis() ? 0 : -1));
                }
            });
            Collections.sort(this.model.getArrayList(), new Comparator<TransactionRowModel>() {
                @RequiresApi(api = 19)
                public int compare(TransactionRowModel transactionRowModel, TransactionRowModel transactionRowModel2) {
                    return (transactionRowModel2.getDateTimeInMillis() > transactionRowModel.getDateTimeInMillis() ? 1 : (transactionRowModel2.getDateTimeInMillis() == transactionRowModel.getDateTimeInMillis() ? 0 : -1));
                }
            });
            this.mainList.size();
            this.model.getArrayList().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setRecycler() {
        this.binding.recycler.setLayoutManager(new LinearLayoutManager(this.context));
        this.binding.recycler.setAdapter(new TransactionAdapter(this.context, this.model.getArrayList(), new RecyclerItemClick() {
            public void onClick(int i, int i2) {
                if (i2 == 2) {
                    openItemDetail(i, (TransactionRowModel) model.getArrayList().get(i), true);
                }
            }
        }));
    }

    private void addItem() {
        TransactionRowModel transactionRowModel = new TransactionRowModel();
        transactionRowModel.setId(AppConstants.getUniqueId());
        String defaultCategoryId = AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_BUSINESS);
        transactionRowModel.setCategoryId(defaultCategoryId);
        transactionRowModel.setCategoryRowModel(this.db.categoryDao().getDetail(defaultCategoryId));
        String defaultModeId = AppConstants.getDefaultModeId(Constants.MODE_TYPE_CASH);
        transactionRowModel.setModeId(defaultModeId);
        transactionRowModel.setModeRowModel(this.db.modeDao().getDetail(defaultModeId));
        openItemDetail(-1, transactionRowModel, false);
    }


    public void openItemDetail(int i, TransactionRowModel transactionRowModel, boolean z) {
        Intent intent = new Intent(this.context, AddEditTransactionAct.class);
        intent.putExtra(AddEditTransactionAct.EXTRA_IS_EDIT, z);
        intent.putExtra(AddEditTransactionAct.EXTRA_POSITION, i);
        intent.putExtra(AddEditTransactionAct.EXTRA_MODEL, transactionRowModel);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1002);
    }


    public void notifyAdapter() {
        setViewVisibility();
        if (this.binding.recycler.getAdapter() != null) {
            this.binding.recycler.getAdapter().notifyDataSetChanged();
        }
    }

    private void setViewVisibility() {
        int i = 8;
        this.binding.linData.setVisibility(this.model.isListData() ? View.VISIBLE : View.GONE);
        LinearLayout linearLayout = this.binding.linNoData;
        if (!this.model.isListData()) {
            i = 0;
        }
        linearLayout.setVisibility(i);
    }


    public void initMethods() {
        setTotalSummary();
        sortByDialogSetup();
    }

    private void setTotalSummary() {
        this.model.setIncome(this.db.transactionDao().getTypeTotal(Constants.CAT_TYPE_INCOME));
        this.model.setExpense(this.db.transactionDao().getTypeTotal(Constants.CAT_TYPE_EXPENSE));
        this.model.setBalance(this.model.getIncome() - this.model.getExpense());
    }


    public void onActivityResult(int i, int i2, @Nullable Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 != -1) {
            return;
        }
        if (i == 1002) {
            updateList(intent);
        } else if (i == 1004) {
            updateListFilter(intent);
        }
    }

    private void updateList(Intent intent) {
        getActivity().setResult(-1);
        if (intent != null) {
            try {
                if (intent.hasExtra(AddEditTransactionAct.EXTRA_MODEL)) {
                    TransactionRowModel transactionRowModel = (TransactionRowModel) intent.getParcelableExtra(AddEditTransactionAct.EXTRA_MODEL);
                    if (intent.getBooleanExtra(AddEditTransactionAct.EXTRA_IS_DELETED, false)) {
                        deleteItem(intent.getIntExtra(AddEditTransactionAct.EXTRA_POSITION, 0));
                    } else if (!intent.getBooleanExtra(AddEditTransactionAct.EXTRA_IS_EDIT, false)) {
                        checkAndAddHeader(transactionRowModel, true, false, true);
                    } else if (((TransactionRowModel) this.model.getArrayList().get(intent.getIntExtra(AddEditTransactionAct.EXTRA_POSITION, 0))).getMonthOnly() != transactionRowModel.getMonthOnly()) {
                        deleteItem(intent.getIntExtra(AddEditTransactionAct.EXTRA_POSITION, 0));
                        checkAndAddHeader(transactionRowModel, true, false, true);
                    } else {
                        this.model.getArrayList().set(intent.getIntExtra(AddEditTransactionAct.EXTRA_POSITION, 0), transactionRowModel);
                        sortByDateAndTime();
                    }
                    try {
                        setTotalSummary();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyAdapter();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void deleteItem(int i) {
        try {
            if (i == this.model.getArrayList().size() - 1) {
                int i2 = i - 1;
                if (((TransactionRowModel) this.model.getArrayList().get(i2)).getViewType() == 1) {
                    this.mainList.remove(i);
                    this.mainList.remove(i2);
                    this.model.getArrayList().remove(i);
                    this.model.getArrayList().remove(i2);
                    return;
                }
            }
            int i3 = i - 1;
            if (((TransactionRowModel) this.model.getArrayList().get(i3)).getViewType() == 1 && ((TransactionRowModel) this.model.getArrayList().get(i + 1)).getViewType() == 1) {
                this.mainList.remove(i);
                this.mainList.remove(i3);
                this.model.getArrayList().remove(i);
                this.model.getArrayList().remove(i3);
                return;
            }
            this.mainList.remove(i);
            this.model.getArrayList().remove(i);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Exception => ");
            sb.append(e.getMessage());
            AppConstants.logDebug(this.context, "resetFilter", sb.toString());
            e.printStackTrace();
        }
    }

    private void checkPermExport() {
        if (this.model.getArrayList().size() > 0) {
            if (isHasPermissions(getContext(), "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")) {
                showExport();
                return;
            }
            requestPermissions(getContext(), getString(R.string.rationale_save), Constants.REQUEST_PERM_FILE, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
            return;
        }
        AppConstants.toastShort(this.context, this.model.getNoDataText());
    }

    private void sortByDialogSetup() {
        setExportDialog();
    }

    public void setExportDialog() {
        this.dialogExportBinding = (AlertDialogExportBinding) DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.alert_dialog_export_act, null, false);
        this.dialogExport = new Dialog(this.context);
        this.dialogExport.setContentView(this.dialogExportBinding.getRoot());
        this.dialogExport.getWindow().setBackgroundDrawableResource(17170445);
        this.dialogExport.getWindow().setLayout(-1, -2);
        this.dialogExportBinding.btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                    exportData(dialogExportBinding.radioPdf.isChecked());
                    try {
                        dialogExport.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        });
        this.dialogExportBinding.btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    dialogExport.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showExport() {
        try {
            if (this.dialogExport != null && !this.dialogExport.isShowing()) {
                this.dialogExport.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void exportData(boolean z) {
        if (z) {
            savePdf();
        } else {
            saveExcel();
        }
    }


    public void initPdf() {
        Document document2 = new Document(PageSize.A4, 16.0f, 16.0f, 16.0f, 16.0f);
        this.document = document2;
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(Constants.REPORT_DIRECTORY);
        this.dir = new File(sb.toString());
        if (!this.dir.exists()) {
            this.dir.mkdirs();
        }
        try {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(getCurrentDateTime());
            sb2.append(".pdf");
            this.fileName = sb2.toString();
            this.writer = PdfWriter.getInstance(this.document, new FileOutputStream(new File(this.dir, this.fileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        this.document.open();
    }


    public void fillPdfData() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.repoType);
        sb.append(" ");
        sb.append(this.repoTitle);
        this.paragraph = new Paragraph(sb.toString().toUpperCase(), new Font(FontFamily.TIMES_ROMAN, 18.0f, 1));
        this.paragraph.setAlignment(1);
        this.paragraph.add((Element) new Paragraph(" "));
        try {
            this.document.add(this.paragraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        try {
            this.document.add(getPdfTable(this.model));
        } catch (DocumentException e2) {
            e2.printStackTrace();
        }
        this.paragraph = new Paragraph("");
        this.paragraph.add((Element) new Paragraph(" "));
        try {
            this.document.add(this.paragraph);
        } catch (DocumentException e3) {
            e3.printStackTrace();
        }
        Paragraph paragraph2 = new Paragraph();
        paragraph2.add((Element) new Chunk("Income : ", new Font(FontFamily.TIMES_ROMAN, 12.0f, 1, new BaseColor(0, 128, 0))));
        paragraph2.add((Element) new Chunk(this.model.getIncomeLabel(), new Font(FontFamily.TIMES_ROMAN, 12.0f, 1)));
        this.paragraph.add((Element) paragraph2);
        Paragraph paragraph3 = new Paragraph();
        paragraph3.add((Element) new Chunk("Expense : ", new Font(FontFamily.TIMES_ROMAN, 12.0f, 1, new BaseColor(255, 0, 0))));
        paragraph3.add((Element) new Chunk(this.model.getExpenseLabel(), new Font(FontFamily.TIMES_ROMAN, 12.0f, 1)));
        this.paragraph.add((Element) paragraph3);
        Paragraph paragraph4 = new Paragraph();
        paragraph4.add((Element) new Chunk("Balance : ", new Font(FontFamily.TIMES_ROMAN, 12.0f, 1, BaseColor.BLUE)));
        paragraph4.add((Element) new Chunk(this.model.getBalanceLabel(), new Font(FontFamily.TIMES_ROMAN, 12.0f, 1)));
        this.paragraph.add((Element) paragraph4);
        try {
            this.document.add(this.paragraph);
        } catch (DocumentException e4) {
            e4.printStackTrace();
        }
    }

    private PdfPTable getPdfTable(TransactionListModel transactionListModel) {
        ArrayList fillPdfList = fillPdfList(transactionListModel);
        PdfPTable pdfPTable = new PdfPTable(((ReportRowModel) fillPdfList.get(0)).getValueList().size());
        float f = (float) 18;
        float f2 = (float) 12;
        float[] fArr = {(float) 8, f, f, f2, (float) 14, f2, f};
        pdfPTable.setTotalWidth(100.0f);
        try {
            pdfPTable.setWidths(fArr);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        pdfPTable.getDefaultCell().setVerticalAlignment(1);
        pdfPTable.getDefaultCell().setHorizontalAlignment(1);
        pdfPTable.setWidthPercentage(100.0f);
        pdfPTable.setSpacingBefore(0.0f);
        pdfPTable.setSpacingAfter(0.0f);
        for (int i = 0; i < fillPdfList.size(); i++) {
            for (int i2 = 0; i2 < ((ReportRowModel) fillPdfList.get(i)).getValueList().size(); i2++) {
                if (((String) ((ReportRowModel) fillPdfList.get(i)).getValueList().get(i2)).equalsIgnoreCase("#Expense")) {
                    pdfPTable.addCell((Phrase) new Paragraph(((String) ((ReportRowModel) fillPdfList.get(i)).getValueList().get(i2)).replace("#", ""), new Font(FontFamily.TIMES_ROMAN, 12.0f, 0, new BaseColor(255, 0, 0))));
                } else if (((String) ((ReportRowModel) fillPdfList.get(i)).getValueList().get(i2)).equalsIgnoreCase("#Income")) {
                    pdfPTable.addCell((Phrase) new Paragraph(((String) ((ReportRowModel) fillPdfList.get(i)).getValueList().get(i2)).replace("#", ""), new Font(FontFamily.TIMES_ROMAN, 12.0f, 0, new BaseColor(0, 128, 0))));
                } else {
                    pdfPTable.addCell((String) ((ReportRowModel) fillPdfList.get(i)).getValueList().get(i2));
                }
            }
            if (i == 0) {
                pdfPTable.setHeaderRows(1);
                PdfPCell[] cells = pdfPTable.getRow(0).getCells();
                for (PdfPCell backgroundColor : cells) {
                    backgroundColor.setBackgroundColor(BaseColor.GRAY);
                }
            }
        }
        return pdfPTable;
    }

    private ArrayList<ReportRowModel> fillPdfList(TransactionListModel transactionListModel) {
        ArrayList<ReportRowModel> arrayList = new ArrayList<>();
        arrayList.clear();
        ReportRowModel reportRowModel = new ReportRowModel();
        reportRowModel.setValueList(new ArrayList());
        reportRowModel.getValueList().add("Sr No.");
        reportRowModel.getValueList().add("Date");
        reportRowModel.getValueList().add("Amount");
        reportRowModel.getValueList().add(Constants.STATISTICS_BY_TYPE);
        reportRowModel.getValueList().add(Constants.STATISTICS_BY_CATEGORY);
        reportRowModel.getValueList().add("Payment");
        reportRowModel.getValueList().add("Notes");
        arrayList.add(reportRowModel);
        int i = 0;
        for (int i2 = 0; i2 < transactionListModel.getArrayList().size(); i2++) {
            if (((TransactionRowModel) transactionListModel.getArrayList().get(i2)).getViewType() == 2) {
                ReportRowModel reportRowModel2 = new ReportRowModel();
                reportRowModel2.setValueList(new ArrayList());
                i++;
                ArrayList valueList = reportRowModel2.getValueList();
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(i);
                valueList.add(sb.toString());
                reportRowModel2.getValueList().add(((TransactionRowModel) transactionListModel.getArrayList().get(i2)).getDateFormatted());
                reportRowModel2.getValueList().add(((TransactionRowModel) transactionListModel.getArrayList().get(i2)).getAmountWithCurrency());
                reportRowModel2.getValueList().add(((TransactionRowModel) transactionListModel.getArrayList().get(i2)).getType() == Constants.CAT_TYPE_INCOME ? "#Income" : "#Expense");
                reportRowModel2.getValueList().add(((TransactionRowModel) transactionListModel.getArrayList().get(i2)).getCategoryRowModel().getName());
                reportRowModel2.getValueList().add(((TransactionRowModel) transactionListModel.getArrayList().get(i2)).getModeRowModel().getName());
                reportRowModel2.getValueList().add(((TransactionRowModel) transactionListModel.getArrayList().get(i2)).getNote());
                arrayList.add(reportRowModel2);
            }
        }
        return arrayList;
    }


    public void addingPdfFooter() {
        new PdfFooterPageEvent().onEndPage(this.writer, this.document);
        try {
            this.document.close();
            openReportList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openReportList() {
        startActivity(new Intent(getContext(), ReportsListAct.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void savePdf() {
        new BackgroundAsync(this.context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
                initPdf();
            }

            public void doInBackground() {
                fillPdfData();
            }

            public void onPostExecute() {
                addingPdfFooter();
            }
        }).execute(new Object[0]);
    }


    public void initExcel() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(Constants.REPORT_DIRECTORY);
        this.dir = new File(sb.toString());
        if (!this.dir.exists()) {
            this.dir.mkdirs();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(getCurrentDateTime());
        sb2.append(".xls");
        this.fileName = sb2.toString();
        File file = new File(this.dir, this.fileName);
        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setLocale(Locale.US);
        try {
            this.workbook = Workbook.createWorkbook(file, workbookSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        WritableWorkbook writableWorkbook = this.workbook;
        StringBuilder sb3 = new StringBuilder();
        sb3.append(this.repoType);
        sb3.append(" ");
        sb3.append(this.repoTitle);
        writableWorkbook.createSheet(sb3.toString(), 0);
        this.excelSheet = this.workbook.getSheet(0);
        this.writableCellFormatRow = new WritableCellFormat(new WritableFont(WritableFont.TIMES, 10));
        try {
            this.writableCellFormatRow.setWrap(true);
        } catch (WriteException e2) {
            e2.printStackTrace();
        }
        WritableFont writableFont = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
        this.writableCellFormatRowRed = new WritableCellFormat(writableFont);
        try {
            this.writableCellFormatRowRed.setWrap(true);
        } catch (WriteException e3) {
            e3.printStackTrace();
        }
        WritableFont writableFont2 = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.GREEN);
        this.writableCellFormatRowGreen = new WritableCellFormat(writableFont2);
        try {
            this.writableCellFormatRowGreen.setWrap(true);
        } catch (WriteException e4) {
            e4.printStackTrace();
        }
        WritableFont writableFont3 = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        this.writableCellFormatHeader = new WritableCellFormat(writableFont3);
        try {
            this.writableCellFormatHeader.setWrap(true);
        } catch (WriteException e5) {
            e5.printStackTrace();
        }
        WritableFont writableFont4 = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
        this.writableCellFormatHeaderRed = new WritableCellFormat(writableFont4);
        try {
            this.writableCellFormatHeaderRed.setWrap(true);
        } catch (WriteException e6) {
            e6.printStackTrace();
        }
        WritableFont writableFont5 = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.GREEN);
        this.writableCellFormatHeaderGreen = new WritableCellFormat(writableFont5);
        try {
            this.writableCellFormatHeaderGreen.setWrap(true);
        } catch (WriteException e7) {
            e7.printStackTrace();
        }
        WritableFont writableFont6 = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLUE);
        this.writableCellFormatHeaderBlue = new WritableCellFormat(writableFont6);
        try {
            this.writableCellFormatHeaderBlue.setWrap(true);
        } catch (WriteException e8) {
            e8.printStackTrace();
        }
        CellView cellView = new CellView();
        cellView.setFormat(this.writableCellFormatHeader);
        cellView.setFormat(this.writableCellFormatHeaderRed);
        cellView.setFormat(this.writableCellFormatHeaderGreen);
        cellView.setFormat(this.writableCellFormatHeaderBlue);
        cellView.setFormat(this.writableCellFormatRow);
        cellView.setFormat(this.writableCellFormatRowRed);
        cellView.setFormat(this.writableCellFormatRowGreen);
        cellView.setAutosize(true);
    }


    public void fillExcelData() {
        addLabel(this.excelSheet, 0, 0, "Sr No.", this.writableCellFormatHeader);
        addLabel(this.excelSheet, 1, 0, "Date", this.writableCellFormatHeader);
        addLabel(this.excelSheet, 2, 0, "Amount", this.writableCellFormatHeader);
        addLabel(this.excelSheet, 3, 0, Constants.STATISTICS_BY_TYPE, this.writableCellFormatHeader);
        addLabel(this.excelSheet, 4, 0, Constants.STATISTICS_BY_CATEGORY, this.writableCellFormatHeader);
        addLabel(this.excelSheet, 5, 0, "Payment", this.writableCellFormatHeader);
        addLabel(this.excelSheet, 6, 0, "Notes", this.writableCellFormatHeader);
        int i = 0;
        for (int i2 = 0; i2 < this.model.getArrayList().size(); i2++) {
            if (((TransactionRowModel) this.model.getArrayList().get(i2)).getViewType() == 2) {
                int i3 = i + 1;
                int i4 = i3;
                addNumber(this.excelSheet, 0, i4, (double) i3, this.writableCellFormatRow);
                addLabel(this.excelSheet, 1, i4, ((TransactionRowModel) this.model.getArrayList().get(i2)).getDateFormatted(), this.writableCellFormatRow);
                addNumber(this.excelSheet, 2, i4, ((TransactionRowModel) this.model.getArrayList().get(i2)).getAmount(), this.writableCellFormatRow);
                int i5 = i3;
                addLabel(this.excelSheet, 3, i5, ((TransactionRowModel) this.model.getArrayList().get(i2)).getType() == Constants.CAT_TYPE_INCOME ? "Income" : "Expense", ((TransactionRowModel) this.model.getArrayList().get(i2)).getType() == Constants.CAT_TYPE_INCOME ? this.writableCellFormatRowGreen : this.writableCellFormatRowRed);
                addLabel(this.excelSheet, 4, i5, ((TransactionRowModel) this.model.getArrayList().get(i2)).getCategoryRowModel().getName(), this.writableCellFormatRow);
                addLabel(this.excelSheet, 5, i5, ((TransactionRowModel) this.model.getArrayList().get(i2)).getModeRowModel().getName(), this.writableCellFormatRow);
                addLabel(this.excelSheet, 6, i5, ((TransactionRowModel) this.model.getArrayList().get(i2)).getNote(), this.writableCellFormatRow);
                i = i3;
            }
        }
        int i6 = i + 5;
        int i7 = i6;
        addLabel(this.excelSheet, 0, i7, "Income", this.writableCellFormatHeaderGreen, Border.TOP, BorderLineStyle.THICK);
        addLabel(this.excelSheet, 1, i7, "Expense", this.writableCellFormatHeaderRed, Border.TOP, BorderLineStyle.THICK);
        addLabel(this.excelSheet, 2, i7, "Balance", this.writableCellFormatHeaderBlue, Border.TOP, BorderLineStyle.THICK);
        int i8 = i6 + 1;
        addLabel(this.excelSheet, 0, i8, this.model.getIncomeLabel(), this.writableCellFormatHeader, Border.BOTTOM, BorderLineStyle.THICK);
        addLabel(this.excelSheet, 1, i8, this.model.getExpenseLabel(), this.writableCellFormatHeader, Border.BOTTOM, BorderLineStyle.THICK);
        addLabel(this.excelSheet, 2, i8, this.model.getBalanceLabel(), this.writableCellFormatHeader, Border.BOTTOM, BorderLineStyle.THICK);
        sheetAutoFitColumns(this.excelSheet);
    }


    public void closeExcel() {
        try {
            this.workbook.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.workbook.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (WriteException e3) {
            e3.printStackTrace();
        }
        openReportList();
    }

    private void addNumber(WritableSheet writableSheet, int i, int i2, double d, WritableCellFormat writableCellFormat) {
        Number number = new Number(i, i2, d, writableCellFormat);
        try {
            writableSheet.addCell(number);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void addLabel(WritableSheet writableSheet, int i, int i2, String str, WritableCellFormat writableCellFormat) {
        try {
            writableSheet.addCell(new Label(i, i2, str, writableCellFormat));
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void addLabel(WritableSheet writableSheet, int i, int i2, String str, WritableCellFormat writableCellFormat, Border border, BorderLineStyle borderLineStyle) {
        try {
            writableCellFormat.setBorder(border, borderLineStyle);
        } catch (WriteException e) {
            e.printStackTrace();
        }
        try {
            writableSheet.addCell(new Label(i, i2, str, writableCellFormat));
        } catch (WriteException e2) {
            e2.printStackTrace();
        }
    }

    private void sheetAutoFitColumns(WritableSheet writableSheet) {
        for (int i = 0; i < writableSheet.getColumns(); i++) {
            Cell[] column = writableSheet.getColumn(i);
            if (column.length != 0) {
                int i2 = -1;
                for (int i3 = 0; i3 < column.length; i3++) {
                    if (column[i3].getContents().length() > i2) {
                        String contents = column[i3].getContents();
                        if (contents != null && !contents.isEmpty()) {
                            i2 = contents.trim().length();
                        }
                    }
                }
                if (i2 != -1) {
                    int i4 = 255;
                    if (i2 <= 255) {
                        i4 = i2;
                    }
                    CellView columnView = writableSheet.getColumnView(i);
                    columnView.setSize((i4 * 256) + 100);
                    writableSheet.setColumnView(i, columnView);
                }
            }
        }
    }

    private void saveExcel() {
        new BackgroundAsync(this.context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
                initExcel();
            }

            public void doInBackground() {
                fillExcelData();
            }

            public void onPostExecute() {
                closeExcel();
            }
        }).execute(new Object[0]);
    }

    public String getCurrentDateTime() {
        return new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss_a").format(new Date(Calendar.getInstance().getTimeInMillis()));
    }

    private void openFilter() {
        Intent intent = new Intent(this.context, AddEditTransactionFilterAct.class);
        intent.putExtra(AddEditTransactionFilterAct.EXTRA_MODEL, this.model.getFilterModel());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1004);
    }

    private void updateListFilter(Intent intent) {
        if (intent != null) {
            try {
                if (intent.hasExtra(AddEditTransactionAct.EXTRA_MODEL)) {
                    this.model.setFilterModel((TransactionFilterModel) intent.getParcelableExtra(AddEditTransactionAct.EXTRA_MODEL));
                    applyFilter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void applyFilter() {
        new BackgroundAsync(this.context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
                model.getArrayList().clear();
            }

            public void doInBackground() {
                filterList();
            }

            public void onPostExecute() {
                notifyAdapter();
                setupFilterIcon();
            }
        }).execute(new Object[0]);
    }


    public void filterList() {
        for (int i = 0; i < this.mainList.size(); i++) {
            if (((TransactionRowModel) this.mainList.get(i)).getViewType() == 2 && this.model.getFilterModel().isContains((TransactionRowModel) this.mainList.get(i))) {
                checkAndAddHeader((TransactionRowModel) this.mainList.get(i), false, false, false);
            }
        }
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
            showExport();
        }
    }

    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(getActivity(), list)) {
            new Builder(getActivity()).build().show();
        }
    }


    public void BackScreen() {
        getActivity().onBackPressed();
    }
}
