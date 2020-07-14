package com.crazytrends.expensemanager.appBase.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.databinding.RowCategoryFilterBinding;
import java.util.ArrayList;

public class CategoryFilterAdapter extends RecyclerView.Adapter {

    public ArrayList<CategoryRowModel> arrayList;
    private Context context;
    ArrayList<String> list;

    public RecyclerItemClick recyclerItemClick;

    private class RowHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public RowCategoryFilterBinding binding;

        public RowHolder(View view) {
            super(view);
            this.binding =  DataBindingUtil.bind(view);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            ((CategoryRowModel) CategoryFilterAdapter.this.arrayList.get(getAdapterPosition())).setSelected(!((CategoryRowModel) CategoryFilterAdapter.this.arrayList.get(getAdapterPosition())).isSelected());
            CategoryFilterAdapter.this.addRemoveList((CategoryRowModel) CategoryFilterAdapter.this.arrayList.get(getAdapterPosition()));
            CategoryFilterAdapter.this.recyclerItemClick.onClick(getAdapterPosition(), 1);
        }
    }

    public CategoryFilterAdapter(Context context2, ArrayList<CategoryRowModel> arrayList2, ArrayList<String> arrayList3, RecyclerItemClick recyclerItemClick2) {
        this.context = context2;
        this.arrayList = arrayList2;
        this.list = arrayList3;
        this.recyclerItemClick = recyclerItemClick2;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RowHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_category_filter_act, viewGroup, false));
    }



    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RowHolder) {
            RowHolder rowHolder = (RowHolder) viewHolder;
            rowHolder.binding.setCategoryRowModel((CategoryRowModel) this.arrayList.get(i));
            rowHolder.binding.executePendingBindings();
        }
    }

    public int getItemCount() {
        return this.arrayList.size();
    }


    public void addRemoveList(CategoryRowModel categoryRowModel) {
        String id = categoryRowModel.getId();
        if (categoryRowModel.isSelected()) {
            if (!this.list.contains(id)) {
                this.list.add(id);
            }
        } else if (this.list.contains(id)) {
            this.list.remove(id);
        }
    }
}
