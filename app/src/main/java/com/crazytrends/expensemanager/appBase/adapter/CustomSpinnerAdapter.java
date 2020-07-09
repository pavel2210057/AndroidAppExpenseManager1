package com.crazytrends.expensemanager.appBase.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;

import androidx.databinding.DataBindingUtil;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.models.spinner.SpinnerRowModel;
import com.crazytrends.expensemanager.databinding.RowCustomSpinnerItemBinding;
import com.crazytrends.expensemanager.databinding.RowCustomSpinnerItemOpenBinding;
import com.crazytrends.expensemanager.databinding.RowCustomSpinnerItemOpenValueBinding;
import java.util.ArrayList;

public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    private final Context context;
    private boolean isValueOnly;
    private ArrayList<SpinnerRowModel> list;

    public long getItemId(int i) {
        return (long) i;
    }

    public CustomSpinnerAdapter(Context context2, ArrayList<SpinnerRowModel> arrayList, boolean z) {
        this.context = context2;
        this.list = arrayList;
        this.isValueOnly = z;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int i) {
        return this.list.get(i);
    }

    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (!this.isValueOnly) {
            RowCustomSpinnerItemOpenBinding rowCustomSpinnerItemOpenBinding = (RowCustomSpinnerItemOpenBinding) DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.row_custom_spinner_item_open, viewGroup, false);
            rowCustomSpinnerItemOpenBinding.setSpinnerRowModel((SpinnerRowModel) this.list.get(i));
            rowCustomSpinnerItemOpenBinding.executePendingBindings();
            return rowCustomSpinnerItemOpenBinding.getRoot();
        }
        RowCustomSpinnerItemOpenValueBinding rowCustomSpinnerItemOpenValueBinding = (RowCustomSpinnerItemOpenValueBinding) DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.row_custom_spinner_item_open_value, viewGroup, false);
        rowCustomSpinnerItemOpenValueBinding.setSpinnerRowModel((SpinnerRowModel) this.list.get(i));
        rowCustomSpinnerItemOpenValueBinding.executePendingBindings();
        return rowCustomSpinnerItemOpenValueBinding.getRoot();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        RowCustomSpinnerItemBinding rowCustomSpinnerItemBinding = (RowCustomSpinnerItemBinding) DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.row_custom_spinner_item, viewGroup, false);
        rowCustomSpinnerItemBinding.setSpinnerRowModel((SpinnerRowModel) this.list.get(i));
        rowCustomSpinnerItemBinding.executePendingBindings();
        return rowCustomSpinnerItemBinding.getRoot();
    }
}
