package com.crazytrends.expensemanager.appBase.adapter;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.roomsDB.statistics.StatisticRowModel;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.databinding.RowStatisticCategoryBinding;
import java.util.ArrayList;

public class StatisticCategoryAdapter extends RecyclerView.Adapter {
    private ArrayList<StatisticRowModel> arrayList;
    private Context context;
    private RecyclerItemClick recyclerItemClick;

    private class RowHolder extends RecyclerView.ViewHolder {

        public RowStatisticCategoryBinding binding;

        public RowHolder(View view) {
            super(view);
            this.binding = (RowStatisticCategoryBinding) DataBindingUtil.bind(view);
        }
    }

    public StatisticCategoryAdapter(Context context2, ArrayList<StatisticRowModel> arrayList2, RecyclerItemClick recyclerItemClick2) {
        this.context = context2;
        this.arrayList = arrayList2;
        this.recyclerItemClick = recyclerItemClick2;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RowHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_statistic_category_act, viewGroup, false));
    }



    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RowHolder) {
            RowHolder rowHolder = (RowHolder) viewHolder;
            rowHolder.binding.setStatisticRowModel((StatisticRowModel) this.arrayList.get(i));
            rowHolder.binding.executePendingBindings();
        }
    }

    public int getItemCount() {
        return this.arrayList.size();
    }
}
