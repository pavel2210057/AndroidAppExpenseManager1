package com.crazytrends.expensemanager.appBase.adapter;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.roomsDB.demo.DemoRowModel;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.databinding.RowDemoBinding;
import java.util.ArrayList;

public class DemoAdapter extends RecyclerView.Adapter {
    private ArrayList<DemoRowModel> arrayList;
    private Context context;

    public RecyclerItemClick recyclerItemClick;

    private class RowHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public RowDemoBinding binding;

        public RowHolder(View view) {
            super(view);
            this.binding = (RowDemoBinding) DataBindingUtil.bind(view);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            DemoAdapter.this.recyclerItemClick.onClick(getAdapterPosition(), 2);
        }
    }

    public DemoAdapter(Context context2, ArrayList<DemoRowModel> arrayList2, RecyclerItemClick recyclerItemClick2) {
        this.context = context2;
        this.arrayList = arrayList2;
        this.recyclerItemClick = recyclerItemClick2;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RowHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_demo, viewGroup, false));
    }



    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RowHolder) {
            RowHolder rowHolder = (RowHolder) viewHolder;
            rowHolder.binding.setDemoRowModel((DemoRowModel) this.arrayList.get(i));
            rowHolder.binding.executePendingBindings();
        }
    }

    public int getItemCount() {
        return this.arrayList.size();
    }
}
