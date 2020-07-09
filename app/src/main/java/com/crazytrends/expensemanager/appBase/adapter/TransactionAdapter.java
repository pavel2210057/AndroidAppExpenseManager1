package com.crazytrends.expensemanager.appBase.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.roomsDB.transation.TransactionRowModel;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.databinding.RowTransactionBinding;
import com.crazytrends.expensemanager.databinding.RowTransactionHeaderBinding;
import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter {
    private ArrayList<TransactionRowModel> arrayList;
    private Context context;

    public RecyclerItemClick recyclerItemClick;

    private class HeaderHolder extends RecyclerView.ViewHolder {

        public RowTransactionHeaderBinding binding;

        public HeaderHolder(View view) {
            super(view);
            this.binding = (RowTransactionHeaderBinding) DataBindingUtil.bind(view);
        }
    }

    private class RowHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public RowTransactionBinding binding;

        public RowHolder(View view) {
            super(view);
            this.binding = (RowTransactionBinding) DataBindingUtil.bind(view);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            TransactionAdapter.this.recyclerItemClick.onClick(getAdapterPosition(), 2);
        }
    }

    public TransactionAdapter(Context context2, ArrayList<TransactionRowModel> arrayList2, RecyclerItemClick recyclerItemClick2) {
        this.context = context2;
        this.arrayList = arrayList2;
        this.recyclerItemClick = recyclerItemClick2;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new HeaderHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_transaction_header, viewGroup, false));
        }
        return new RowHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_transaction, viewGroup, false));
    }



    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RowHolder) {
            RowHolder rowHolder = (RowHolder) viewHolder;
            rowHolder.binding.setTransactionRowModel((TransactionRowModel) this.arrayList.get(i));
            rowHolder.binding.executePendingBindings();

                if(1 ==arrayList.get(i).getType()){
                    rowHolder.binding.categori.setTextColor(Color.parseColor("#ff49a142"));
                    rowHolder.binding.balanse.setTextColor(Color.parseColor("#ff49a142"));
                }else {
                    rowHolder.binding.categori.setTextColor(Color.parseColor("#ffff4a4a"));
                    rowHolder.binding.balanse.setTextColor(Color.parseColor("#ffff4a4a"));
                }
        } else if (viewHolder instanceof HeaderHolder) {
            HeaderHolder headerHolder = (HeaderHolder) viewHolder;
            headerHolder.binding.setTransactionRowModel((TransactionRowModel) this.arrayList.get(i));
            headerHolder.binding.executePendingBindings();
        }
    }

    public int getItemCount() {
        return this.arrayList.size();
    }

    public int getItemViewType(int i) {
        return ((TransactionRowModel) this.arrayList.get(i)).getViewType();
    }
}
