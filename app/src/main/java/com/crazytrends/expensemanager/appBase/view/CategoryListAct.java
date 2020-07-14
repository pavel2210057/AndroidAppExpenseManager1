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
import com.crazytrends.expensemanager.appBase.adapter.CategoryAdapter;
import com.crazytrends.expensemanager.appBase.baseClass.BaseActivityRecyclerBinding;
import com.crazytrends.expensemanager.appBase.models.category.CategoryListModel;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.BackgroundAsync;
import com.crazytrends.expensemanager.appBase.utils.OnAsyncBackground;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.appBase.utils.TwoButtonDialogListener;
import com.crazytrends.expensemanager.databinding.ActivityCategoryListBinding;
import com.crazytrends.expensemanager.databinding.AlertDialogNewCategoryBinding;
import java.util.ArrayList;

public class CategoryListAct extends BaseActivityRecyclerBinding {
    private ActivityCategoryListBinding binding;
    CategoryRowModel catRowModel;

    public AppDataBase db;

    public Dialog dialogNewCat;

    public AlertDialogNewCategoryBinding dialogNewCatBinding;

    public ArrayList<CategoryRowModel> listMain;

    public CategoryListModel model;

    public String searchText = "";

    public int selectedCatPos = 0;
    private ToolbarModel toolbarModel;


    public void callApi() {
    }


    public void setBinding() {
        this.binding = (ActivityCategoryListBinding) DataBindingUtil.setContentView(this, R.layout.act_category_list);
        this.model = new CategoryListModel();
        this.model.setArrayList(new ArrayList());
        this.listMain = new ArrayList<>();
        this.model.setNoDataIcon(R.drawable.no_data);
        this.model.setNoDataText(getString(R.string.noDataTitleCategory));
        this.model.setNoDataDetail(getString(R.string.noDataDescCategory));
        this.binding.setCategoryListModel(this.model);
        this.db = AppDataBase.getAppDatabase(this.context);
    }


    public void setToolbar() {
        this.toolbarModel = new ToolbarModel();
        this.toolbarModel.setTitle("Manage Category");
        this.toolbarModel.setAdd(true);
        this.toolbarModel.setSearchMenu(true);
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
                CategoryListAct.this.fillFromDB();
            }

            public void onPostExecute() {
                CategoryListAct.this.notifyAdapter();
            }
        }).execute(new Object[0]);
    }


    public void fillFromDB() {
        try {
            this.listMain.addAll(this.db.categoryDao().getAll());
            this.model.getArrayList().addAll(this.listMain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setRecycler() {
        this.binding.recycler.setLayoutManager(new LinearLayoutManager(this.context));
        this.binding.recycler.setAdapter(new CategoryAdapter(this.context, true, this.model.getArrayList(), new RecyclerItemClick() {
            public void onClick(int i, int i2) {
                if (i2 == 2) {
                    CategoryListAct.this.deleteItem(i);
                } else {
                    CategoryListAct.this.showNewCatList(i);
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
        sb.append(((CategoryRowModel) this.model.getArrayList().get(i)).getName());
        sb.append("</b> <br />");
        sb.append(getString(R.string.delete_all_related_data));
        sb.append(" category");
        AppConstants.showTwoButtonDialog(this.context, getString(R.string.app_name), sb.toString(), true, true, getString(R.string.delete), getString(R.string.cancel), new TwoButtonDialogListener() {
            public void onCancel() {
            }

            public void onOk() {
                CategoryListAct.this.deleteCategory(i);
            }
        });
    }


    public void deleteCategory(final int i) {
        new BackgroundAsync(this.context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
            }

            public void doInBackground() {
                try {
                    CategoryListAct.this.db.transactionDao().deleteAllCategory(((CategoryRowModel) CategoryListAct.this.model.getArrayList().get(i)).getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onPostExecute() {
                try {
                    CategoryListAct.this.db.categoryDao().delete((CategoryRowModel) CategoryListAct.this.model.getArrayList().get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CategoryListAct.this.model.getArrayList().remove(i);
                CategoryListAct.this.notifyAdapter();
            }
        }).execute(new Object[0]);
    }

    private void newCatDialogSetup() {
        setNewCatDialog();
    }

    public void setNewCatDialog() {
        this.dialogNewCatBinding = (AlertDialogNewCategoryBinding) DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.alert_dialog_new_category_act, null, false);
        this.dialogNewCat = new Dialog(this.context);
        this.dialogNewCat.setContentView(this.dialogNewCatBinding.getRoot());
        this.dialogNewCat.getWindow().setBackgroundDrawableResource(17170445);
        this.dialogNewCat.getWindow().setLayout(-1, -2);
        this.dialogNewCatBinding.txtTitle.setText(R.string.add_category);
        this.dialogNewCatBinding.btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    CategoryListAct.this.dialogNewCat.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.dialogNewCatBinding.btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                long j = 0;
                long j2;
                if (CategoryListAct.this.isValidNewCat(CategoryListAct.this.dialogNewCatBinding)) {
                    CategoryListAct.this.catRowModel.setName(CategoryListAct.this.dialogNewCatBinding.etName.getText().toString().trim());
                    Exception e;
                    try {
                        CategoryListAct.this.catRowModel.getName().trim();
                        if (CategoryListAct.this.selectedCatPos != -1) {
                            try {
                                j = (long) CategoryListAct.this.db.categoryDao().update(CategoryListAct.this.catRowModel);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                j = 0;
                            }
                            try {
                                if (CategoryListAct.this.searchText == null || CategoryListAct.this.searchText.length() <= 0) {
                                    CategoryListAct.this.model.getArrayList().set(CategoryListAct.this.selectedCatPos, CategoryListAct.this.catRowModel);
                                } else if (CategoryListAct.this.catRowModel.getName().toLowerCase().contains(CategoryListAct.this.searchText.toLowerCase())) {
                                    CategoryListAct.this.model.getArrayList().set(CategoryListAct.this.selectedCatPos, CategoryListAct.this.catRowModel);
                                } else {
                                    CategoryListAct.this.model.getArrayList().remove(CategoryListAct.this.selectedCatPos);
                                }
                                CategoryListAct.this.listMain.set(CategoryListAct.this.selectedCatPos, CategoryListAct.this.catRowModel);
                            } catch (Exception e2) {
                                e = e2;
                                e.printStackTrace();
                                if (j > 0) {
                                }
                                CategoryListAct.this.dialogNewCat.dismiss();
                            }
                            if (j > 0) {
                                CategoryListAct.this.notifyAdapter();
                            }
                            try {
                                CategoryListAct.this.dialogNewCat.dismiss();
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                        } else {
                            try {
                                j2 = CategoryListAct.this.db.categoryDao().insert(CategoryListAct.this.catRowModel);
                            } catch (Exception e4) {
                                e4.printStackTrace();
                                j2 = 0;
                            }
                            if (CategoryListAct.this.searchText == null || CategoryListAct.this.searchText.length() <= 0) {
                                CategoryListAct.this.model.getArrayList().add(CategoryListAct.this.catRowModel);
                            } else if (CategoryListAct.this.catRowModel.getName().toLowerCase().contains(CategoryListAct.this.searchText.toLowerCase())) {
                                CategoryListAct.this.model.getArrayList().add(CategoryListAct.this.catRowModel);
                            }
                            CategoryListAct.this.listMain.add(CategoryListAct.this.catRowModel);
                            if (j > 0) {
                            }
                            CategoryListAct.this.dialogNewCat.dismiss();
                        }
                    } catch (Exception e5) {
                        e = e5;
                        j = 0;
                        e.printStackTrace();
                        if (j > 0) {
                        }
                        CategoryListAct.this.dialogNewCat.dismiss();
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
            this.catRowModel = new CategoryRowModel(((CategoryRowModel) this.model.getArrayList().get(this.selectedCatPos)).getId(), ((CategoryRowModel) this.model.getArrayList().get(this.selectedCatPos)).getName(), ((CategoryRowModel) this.model.getArrayList().get(this.selectedCatPos)).isDefault());
            this.dialogNewCatBinding.txtTitle.setText(R.string.update_category);
            this.dialogNewCatBinding.etName.setText(this.catRowModel.getName());
        } else {
            this.catRowModel = new CategoryRowModel();
            this.catRowModel.setId(AppConstants.getUniqueId());
            this.dialogNewCatBinding.txtTitle.setText(R.string.add_category);
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
                CategoryListAct.this.updateList(str);
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
                CategoryRowModel categoryRowModel = (CategoryRowModel) this.listMain.get(i);
                if (categoryRowModel.getName().toLowerCase().contains(str.trim().toLowerCase())) {
                    this.model.getArrayList().add(categoryRowModel);
                }
            }
        }
        notifyAdapter();
    }
}
