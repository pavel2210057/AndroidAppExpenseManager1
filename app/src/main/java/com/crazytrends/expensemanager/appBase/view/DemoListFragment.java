package com.crazytrends.expensemanager.appBase.view;

import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.adapter.DemoAdapter;
import com.crazytrends.expensemanager.appBase.baseClass.BaseFragmentRecyclerBinding;
import com.crazytrends.expensemanager.appBase.models.demo.DemoListModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.demo.DemoRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.BackgroundAsync;
import com.crazytrends.expensemanager.appBase.utils.OnAsyncBackground;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.databinding.FragmentDemoListBinding;
import java.util.ArrayList;

public class DemoListFragment extends BaseFragmentRecyclerBinding {
    private static final String ARG_PARAM_LIST = "list";

    public FragmentDemoListBinding binding;

    public AppDataBase db;
    private ArrayList<DemoRowModel> list;

    public DemoListModel model;


    public void callApi() {
    }


    public void initMethods() {
    }


    public void setToolbar() {
    }

    public void updateList(String str) {
    }

    public static DemoListFragment newInstance(ArrayList<DemoRowModel> arrayList) {
        DemoListFragment demoListFragment = new DemoListFragment();
        demoListFragment.setArguments(new Bundle());
        return demoListFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getArguments();
    }


    public View getViewBinding() {
        return this.binding.getRoot();
    }


    public void setBinding(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        this.binding = (FragmentDemoListBinding) DataBindingUtil.inflate(layoutInflater, R.layout.fragment_demo_list, viewGroup, false);
        this.model = new DemoListModel();
        this.model.setArrayList(new ArrayList());
        this.model.setNoDataIcon(R.drawable.no_data);
        this.model.setNoDataText(getString(R.string.noDataTitleDemo));
        this.model.setNoDataDetail(getString(R.string.noDataDescDemo));
        this.binding.setDemoListModel(this.model);
        this.db = AppDataBase.getAppDatabase(this.context);
    }


    public void setOnClicks() {
        this.binding.fabAdd.setOnClickListener(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.fabAdd) {
            openItemDetail(-1, new DemoRowModel(), false);
        }
    }


    public void fillData() {
        new BackgroundAsync(this.context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
            }

            public void doInBackground() {
                try {
                    DemoListFragment.this.model.getArrayList().addAll(DemoListFragment.this.db.demoDao().getAll());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onPostExecute() {
                DemoListFragment.this.notifyAdapter();
            }
        }).execute(new Object[0]);
    }

    private void addDummyData() {
        for (int i = 0; i < 10; i++) {
            DemoRowModel demoRowModel = new DemoRowModel();
            demoRowModel.setId(AppConstants.getUniqueId());
            StringBuilder sb = new StringBuilder();
            sb.append("note ");
            sb.append(i);
            demoRowModel.setNote(sb.toString());
            try {
                this.db.demoDao().insert(demoRowModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setRecycler() {
        this.binding.recycler.setLayoutManager(new LinearLayoutManager(this.context));
        this.binding.recycler.setAdapter(new DemoAdapter(this.context, this.model.getArrayList(), new RecyclerItemClick() {
            public void onClick(int i, int i2) {
                if (i2 == 2) {
                    DemoListFragment.this.openItemDetail(i, (DemoRowModel) DemoListFragment.this.model.getArrayList().get(i), true);
                }
            }
        }));
        this.binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                if (i2 > 0 && DemoListFragment.this.binding.fabAdd.getVisibility() == 0) {
                    DemoListFragment.this.binding.fabAdd.hide();
                } else if (i2 < 0 && DemoListFragment.this.binding.fabAdd.getVisibility() != 0) {
                    DemoListFragment.this.binding.fabAdd.show();
                }
            }
        });
    }


    public void openItemDetail(int i, DemoRowModel demoRowModel, boolean z) {
        Intent intent = new Intent(this.context, AddEditDemoActivity.class);
        intent.putExtra(AddEditDemoActivity.EXTRA_IS_EDIT, z);
        intent.putExtra(AddEditDemoActivity.EXTRA_POSITION, i);
        intent.putExtra(AddEditDemoActivity.EXTRA_MODEL, demoRowModel);
        intent.setFlags(67108864);
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
        this.binding.linData.setVisibility(this.model.isListData() ? 0 : 8);
        LinearLayout linearLayout = this.binding.linNoData;
        if (!this.model.isListData()) {
            i = 0;
        }
        linearLayout.setVisibility(i);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1 && i == 1002) {
            updateList(intent);
        }
    }

    private void updateList(Intent intent) {
        if (intent != null) {
            try {
                if (intent.hasExtra(AddEditDemoActivity.EXTRA_MODEL)) {
                    DemoRowModel demoRowModel = (DemoRowModel) intent.getParcelableExtra(AddEditDemoActivity.EXTRA_MODEL);
                    if (intent.getBooleanExtra(AddEditDemoActivity.EXTRA_IS_DELETED, false)) {
                        this.model.getArrayList().remove(intent.getIntExtra(AddEditDemoActivity.EXTRA_POSITION, 0));
                    } else if (intent.getBooleanExtra(AddEditDemoActivity.EXTRA_IS_EDIT, false)) {
                        this.model.getArrayList().set(intent.getIntExtra(AddEditDemoActivity.EXTRA_POSITION, 0), demoRowModel);
                    } else {
                        this.model.getArrayList().add(demoRowModel);
                    }
                    notifyAdapter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
