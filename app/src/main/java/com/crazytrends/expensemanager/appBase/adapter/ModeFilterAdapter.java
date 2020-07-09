package com.crazytrends.expensemanager.appBase.adapter;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeRowModel;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.databinding.RowModeFilterBinding;
import java.util.ArrayList;

public class ModeFilterAdapter extends RecyclerView.Adapter {

    public ArrayList<ModeRowModel> arrayList;
    private Context context;
    ArrayList<String> list;

    public RecyclerItemClick recyclerItemClick;

    private class RowHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public RowModeFilterBinding binding;

        public RowHolder(View view) {
            super(view);
            this.binding = (RowModeFilterBinding) DataBindingUtil.bind(view);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            ((ModeRowModel) ModeFilterAdapter.this.arrayList.get(getAdapterPosition())).setSelected(!((ModeRowModel) ModeFilterAdapter.this.arrayList.get(getAdapterPosition())).isSelected());
            ModeFilterAdapter.this.addRemoveList((ModeRowModel) ModeFilterAdapter.this.arrayList.get(getAdapterPosition()));
            ModeFilterAdapter.this.recyclerItemClick.onClick(getAdapterPosition(), 1);
        }
    }

    public ModeFilterAdapter(Context context2, ArrayList<ModeRowModel> arrayList2, ArrayList<String> arrayList3, RecyclerItemClick recyclerItemClick2) {
        this.context = context2;
        this.arrayList = arrayList2;
        this.list = arrayList3;
        this.recyclerItemClick = recyclerItemClick2;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RowHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_mode_filter, viewGroup, false));
    }



    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RowHolder) {
            RowHolder rowHolder = (RowHolder) viewHolder;
            rowHolder.binding.setModeRowModel((ModeRowModel) this.arrayList.get(i));
            rowHolder.binding.executePendingBindings();
        }
    }

    public int getItemCount() {
        return this.arrayList.size();
    }


    public void addRemoveList(ModeRowModel modeRowModel) {
        String id = modeRowModel.getId();
        if (modeRowModel.isSelected()) {
            if (!this.list.contains(id)) {
                this.list.add(id);
            }
        } else if (this.list.contains(id)) {
            this.list.remove(id);
        }
    }
}
