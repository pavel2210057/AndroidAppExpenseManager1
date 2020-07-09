package com.crazytrends.expensemanager.appBase.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.adapter.ModeAdapter;
import com.crazytrends.expensemanager.appBase.baseClass.BaseActivityRecyclerBinding;
import com.crazytrends.expensemanager.appBase.models.paymentmode.PaymentModeListModel;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.BackgroundAsync;
import com.crazytrends.expensemanager.appBase.utils.OnAsyncBackground;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.appBase.utils.TwoButtonDialogListener;
import com.crazytrends.expensemanager.databinding.ActivityPaymentModeListBinding;
import com.crazytrends.expensemanager.databinding.AlertDialogNewCategoryBinding;
import java.util.ArrayList;

public class PaymentModeListActivity extends BaseActivityRecyclerBinding {
    private ActivityPaymentModeListBinding binding;
    ModeRowModel catRowModel;

    public AppDataBase db;

    public Dialog dialogNewCat;

    public AlertDialogNewCategoryBinding dialogNewCatBinding;

    public ArrayList<ModeRowModel> listMain;

    public PaymentModeListModel model;

    public String searchText = "";

    public int selectedCatPos = 0;
    private ToolbarModel toolbarModel;


    public void callApi() {
    }


    public void setBinding() {
        this.binding = (ActivityPaymentModeListBinding) DataBindingUtil.setContentView(this, R.layout.activity_payment_mode_list);
        this.model = new PaymentModeListModel();
        this.model.setArrayList(new ArrayList());
        this.listMain = new ArrayList<>();
        this.model.setNoDataIcon(R.drawable.no_data);
        this.model.setNoDataText(getString(R.string.noDataTitlePaymentMode));
        this.model.setNoDataDetail(getString(R.string.noDataDescPaymentMode));
        this.binding.setPaymentModeListModel(this.model);
        this.db = AppDataBase.getAppDatabase(this.context);
    }


    public void setToolbar() {
        this.toolbarModel = new ToolbarModel();
        this.toolbarModel.setTitle(getString(R.string.managePaymentModes));
        this.toolbarModel.setAdd(true);
        this.binding.includedToolbar.setToolbarModel(this.toolbarModel);
    }


    public void setOnClicks() {
        this.binding.includedToolbar.imgBack.setOnClickListener(this);
        this.binding.includedToolbar.imgAdd.setOnClickListener(this);
        this.binding.fabAdd.setOnClickListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fabAdd || id == R.id.imgAdd) {
            showNewCatList(-1);
        } else if (id == R.id.imgBack) {
            onBackPressed();
        }
    }


    public void fillData() {
        new BackgroundAsync(this.context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
            }

            public void doInBackground() {
                PaymentModeListActivity.this.fillFromDB();
            }

            public void onPostExecute() {
                PaymentModeListActivity.this.notifyAdapter();
            }
        }).execute(new Object[0]);
    }


    public void fillFromDB() {
        try {
            this.listMain.addAll(this.db.modeDao().getAll());
            this.model.getArrayList().addAll(this.listMain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setRecycler() {
        this.binding.recycler.setLayoutManager(new LinearLayoutManager(this.context));
        this.binding.recycler.setAdapter(new ModeAdapter(this.context, true, this.model.getArrayList(), new RecyclerItemClick() {
            public void onClick(int i, int i2) {
                if (i2 == 2) {
                    PaymentModeListActivity.this.deleteItem(i);
                } else {
                    PaymentModeListActivity.this.showNewCatList(i);
                }
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
        this.binding.linData.setVisibility(this.model.isListData() ? 0 : 8);
        LinearLayout linearLayout = this.binding.linNoData;
        if (!this.model.isListData()) {
            i = 0;
        }
        linearLayout.setVisibility(i);
    }


    public void initMethods() {
        newCatDialogSetup();
        setSearch();
    }

    public void deleteItem(final int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.delete_msg));
        sb.append("<br /> <b>");
        sb.append(((ModeRowModel) this.model.getArrayList().get(i)).getName());
        sb.append("</b> <br />");
        sb.append(getString(R.string.delete_all_related_data));
        sb.append(" mode");
        AppConstants.showTwoButtonDialog(this.context, getString(R.string.app_name), sb.toString(), true, true, getString(R.string.delete), getString(R.string.cancel), new TwoButtonDialogListener() {
            public void onCancel() {
            }

            public void onOk() {
                PaymentModeListActivity.this.deleteCategory(i);
            }
        });
    }


    public void deleteCategory(final int i) {
        new BackgroundAsync(this.context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
            }

            public void doInBackground() {
                try {
                    PaymentModeListActivity.this.db.transactionDao().deleteAllMode(((ModeRowModel) PaymentModeListActivity.this.model.getArrayList().get(i)).getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onPostExecute() {
                try {
                    PaymentModeListActivity.this.db.modeDao().delete((ModeRowModel) PaymentModeListActivity.this.model.getArrayList().get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PaymentModeListActivity.this.model.getArrayList().remove(i);
                PaymentModeListActivity.this.notifyAdapter();
            }
        }).execute(new Object[0]);
    }

    private void newCatDialogSetup() {
        setNewCatDialog();
    }

    public void setNewCatDialog() {
        this.dialogNewCatBinding = (AlertDialogNewCategoryBinding) DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.alert_dialog_new_category, null, false);
        this.dialogNewCat = new Dialog(this.context);
        this.dialogNewCat.setContentView(this.dialogNewCatBinding.getRoot());
        this.dialogNewCat.getWindow().setBackgroundDrawableResource(17170445);
        this.dialogNewCat.getWindow().setLayout(-1, -2);
        this.dialogNewCatBinding.txtTitle.setText(R.string.add_payment_mode);
        this.dialogNewCatBinding.btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    PaymentModeListActivity.this.dialogNewCat.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.dialogNewCatBinding.btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                long j = 0;
                long j2;
                if (PaymentModeListActivity.this.isValidNewCat(PaymentModeListActivity.this.dialogNewCatBinding)) {
                    PaymentModeListActivity.this.catRowModel.setName(PaymentModeListActivity.this.dialogNewCatBinding.etName.getText().toString().trim());
                    Exception e;
                    try {
                        PaymentModeListActivity.this.catRowModel.getName().trim();
                        if (PaymentModeListActivity.this.selectedCatPos != -1) {
                            try {
                                j = (long) PaymentModeListActivity.this.db.modeDao().update(PaymentModeListActivity.this.catRowModel);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                j = 0;
                            }
                            try {
                                if (PaymentModeListActivity.this.searchText == null || PaymentModeListActivity.this.searchText.length() <= 0) {
                                    PaymentModeListActivity.this.model.getArrayList().set(PaymentModeListActivity.this.selectedCatPos, PaymentModeListActivity.this.catRowModel);
                                } else if (PaymentModeListActivity.this.catRowModel.getName().toLowerCase().contains(PaymentModeListActivity.this.searchText.toLowerCase())) {
                                    PaymentModeListActivity.this.model.getArrayList().set(PaymentModeListActivity.this.selectedCatPos, PaymentModeListActivity.this.catRowModel);
                                } else {
                                    PaymentModeListActivity.this.model.getArrayList().remove(PaymentModeListActivity.this.selectedCatPos);
                                }
                                PaymentModeListActivity.this.listMain.set(PaymentModeListActivity.this.selectedCatPos, PaymentModeListActivity.this.catRowModel);
                            } catch (Exception e2) {
                                e = e2;
                                e.printStackTrace();
                                if (j > 0) {
                                }
                                PaymentModeListActivity.this.dialogNewCat.dismiss();
                            }
                            if (j > 0) {
                                PaymentModeListActivity.this.notifyAdapter();
                            }
                            try {
                                PaymentModeListActivity.this.dialogNewCat.dismiss();
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                        } else {
                            try {
                                j2 = PaymentModeListActivity.this.db.modeDao().insert(PaymentModeListActivity.this.catRowModel);
                            } catch (Exception e4) {
                                e4.printStackTrace();
                                j2 = 0;
                            }
                            if (PaymentModeListActivity.this.searchText == null || PaymentModeListActivity.this.searchText.length() <= 0) {
                                PaymentModeListActivity.this.model.getArrayList().add(PaymentModeListActivity.this.catRowModel);
                            } else if (PaymentModeListActivity.this.catRowModel.getName().toLowerCase().contains(PaymentModeListActivity.this.searchText.toLowerCase())) {
                                PaymentModeListActivity.this.model.getArrayList().add(PaymentModeListActivity.this.catRowModel);
                            }
                            PaymentModeListActivity.this.listMain.add(PaymentModeListActivity.this.catRowModel);
                            if (j > 0) {
                            }
                            PaymentModeListActivity.this.dialogNewCat.dismiss();
                        }
                    } catch (Exception e5) {
                        e = e5;
                        j = 0;
                        e.printStackTrace();
                        if (j > 0) {
                        }
                        PaymentModeListActivity.this.dialogNewCat.dismiss();
                    }
                }
            }
        });
    }


    public boolean isValidNewCat(AlertDialogNewCategoryBinding alertDialogNewCategoryBinding) {
        Context context = this.context;
        EditText editText = alertDialogNewCategoryBinding.etName;
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.please_enter));
        sb.append(" ");
        sb.append(getString(R.string.name));
        return AppConstants.isNotEmpty(context, editText, sb.toString());
    }


    public void showNewCatList(int i) {
        this.selectedCatPos = i;
        if (this.selectedCatPos != -1) {
            this.catRowModel = new ModeRowModel(((ModeRowModel) this.model.getArrayList().get(this.selectedCatPos)).getId(), ((ModeRowModel) this.model.getArrayList().get(this.selectedCatPos)).getName(), ((ModeRowModel) this.model.getArrayList().get(this.selectedCatPos)).isDefault());
            this.dialogNewCatBinding.txtTitle.setText(R.string.update_payment_mode);
            this.dialogNewCatBinding.etName.setText(this.catRowModel.getName());
        } else {
            this.catRowModel = new ModeRowModel();
            this.catRowModel.setId(AppConstants.getUniqueId());
            this.dialogNewCatBinding.txtTitle.setText(R.string.add_payment_mode);
            this.dialogNewCatBinding.etName.setText("");
        }
        try {
            if (this.dialogNewCat != null && !this.dialogNewCat.isShowing()) {
                this.dialogNewCat.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSearch() {
        this.binding.includedToolbar.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            public boolean onQueryTextChange(String str) {
                PaymentModeListActivity.this.updateList(str);
                return false;
            }
        });
    }

    public void updateList(String str) {
        if (str == null || str.length() <= 0) {
            this.searchText = "";
            this.model.getArrayList().clear();
            this.model.getArrayList().addAll(this.listMain);
        } else {
            this.searchText = str.trim().toLowerCase();
            this.model.getArrayList().clear();
            for (int i = 0; i < this.listMain.size(); i++) {
                ModeRowModel modeRowModel = (ModeRowModel) this.listMain.get(i);
                if (modeRowModel.getName().toLowerCase().contains(str.trim().toLowerCase())) {
                    this.model.getArrayList().add(modeRowModel);
                }
            }
        }
        notifyAdapter();
    }
}
