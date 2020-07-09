package com.crazytrends.expensemanager.appBase.adapter;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.models.drawer.DrawerRowModel;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.databinding.RowDashboardItemBinding;
import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter {
    private ArrayList<DrawerRowModel> arrayList;
    private Context context;

    public RecyclerItemClick recyclerItemClick;

    private class RowHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public RowDashboardItemBinding binding;

        public RowHolder(View view) {
            super(view);
            this.binding = (RowDashboardItemBinding) DataBindingUtil.bind(view);
            view.setOnClickListener(this);
            this.binding.linMain.setOnClickListener(this);
        }

        public void onClick(View view) {
            view.getId();
            DashboardAdapter.this.recyclerItemClick.onClick(getAdapterPosition(), 1);
        }
    }

    public DashboardAdapter(Context context2, ArrayList<DrawerRowModel> arrayList2, RecyclerItemClick recyclerItemClick2) {
        this.context = context2;
        this.arrayList = arrayList2;
        this.recyclerItemClick = recyclerItemClick2;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RowHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_dashboard_item, viewGroup, false));
    }



    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RowHolder) {
            RowHolder rowHolder = (RowHolder) viewHolder;
            rowHolder.binding.setDrawerRowModel((DrawerRowModel) this.arrayList.get(i));
            rowHolder.binding.executePendingBindings();
        }
    }

    public int getItemCount() {
        return this.arrayList.size();
    }
}
