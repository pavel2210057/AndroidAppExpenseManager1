package com.crazytrends.expensemanager.backupRestore;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import java.util.ArrayList;

public class RestoreAdapter extends RecyclerView.Adapter<RestoreAdapter.ViewHolder> {
    private ArrayList<RestoreRowModel> arrayList;
    Context context;

    public RecyclerItemClick itemClick;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDelete;
        ImageView imgRestore;
        TextView txtFileDate;
        TextView txtFileName;
        TextView txtFileSize;

        ViewHolder(View view) {
            super(view);
            this.txtFileName = (TextView) view.findViewById(R.id.txtName);
            this.txtFileDate = (TextView) view.findViewById(R.id.txtDate);
            this.txtFileSize = (TextView) view.findViewById(R.id.txtSize);
            this.imgRestore = (ImageView) view.findViewById(R.id.imgRestore);
            this.imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RestoreAdapter.this.itemClick.onClick(ViewHolder.this.getAdapterPosition(), 1);
                }
            });
            this.imgDelete.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RestoreAdapter.this.itemClick.onClick(ViewHolder.this.getAdapterPosition(), 2);
                }
            });
        }
    }

    public RestoreAdapter(Context context2, ArrayList<RestoreRowModel> arrayList2, RecyclerItemClick recyclerItemClick) {
        this.arrayList = arrayList2;
        this.context = context2;
        this.itemClick = recyclerItemClick;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.restore_item, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        RestoreRowModel restoreRowModel = (RestoreRowModel) this.arrayList.get(i);
        viewHolder.txtFileName.setText(restoreRowModel.getTitle());
        viewHolder.txtFileDate.setText(restoreRowModel.getDateModified());
        viewHolder.txtFileSize.setText(restoreRowModel.getSize());
    }

    public int getItemCount() {
        return this.arrayList.size();
    }
}
