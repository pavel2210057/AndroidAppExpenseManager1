package com.crazytrends.expensemanager.appBase.adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.databinding.RowCategoryBinding;
import com.crazytrends.expensemanager.databinding.RowCategoryManageBinding;
import java.util.ArrayList;

import static com.crazytrends.expensemanager.databinding.RowCategoryBinding.bind;

public class CategoryAdapter extends RecyclerView.Adapter {

    public ArrayList<CategoryRowModel> arrayList;
    private Context context;
    private boolean isManage;

    public RecyclerItemClick recyclerItemClick;

    private class RowHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public RowCategoryBinding binding;

        public RowHolder(View view) {
            super(view);
            this.binding =  bind(view);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            CategoryAdapter.this.selectionAll(false);
            ((CategoryRowModel) CategoryAdapter.this.arrayList.get(getAdapterPosition())).setSelected(!((CategoryRowModel) CategoryAdapter.this.arrayList.get(getAdapterPosition())).isSelected());
            CategoryAdapter.this.recyclerItemClick.onClick(getAdapterPosition(), 1);
        }
    }

    private class RowManageHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public RowCategoryManageBinding binding;

        public RowManageHolder(View view) {
            super(view);
            this.binding =  (RowCategoryManageBinding) DataBindingUtil.bind(view);
            view.setOnClickListener(this);
            this.binding.imgEdit.setOnClickListener(this);
            this.binding.imgDelete.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (view.getId() == R.id.imgEdit) {
                CategoryAdapter.this.recyclerItemClick.onClick(getAdapterPosition(), 1);
            } else if (view.getId() == R.id.imgDelete) {
                CategoryAdapter.this.recyclerItemClick.onClick(getAdapterPosition(), 2);
            }
        }
    }

    public CategoryAdapter(Context context2, boolean z, ArrayList<CategoryRowModel> arrayList2, RecyclerItemClick recyclerItemClick2) {
        this.context = context2;
        this.arrayList = arrayList2;
        this.recyclerItemClick = recyclerItemClick2;
        this.isManage = z;

    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (!this.isManage) {
            return new RowHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_category_act, viewGroup, false));
        }
        return new RowManageHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_category_manage_act, viewGroup, false));
    }



    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof RowHolder) {

            RowHolder rowHolder = (RowHolder) viewHolder;
            rowHolder.binding.setCategoryRowModel((CategoryRowModel) this.arrayList.get(i));
            rowHolder.binding.executePendingBindings();
        } else if (viewHolder instanceof RowManageHolder) {
            RowManageHolder rowManageHolder = (RowManageHolder) viewHolder;
            rowManageHolder.binding.setCategoryRowModel((CategoryRowModel) this.arrayList.get(i));
            rowManageHolder.binding.executePendingBindings();
        }
    }

    public int getItemCount() {
        Log.e("xfighfhfgoh",""+arrayList.size());
        return this.arrayList.size();
    }


    public void selectionAll(boolean z) {
        for (int i = 0; i < this.arrayList.size(); i++) {
            ((CategoryRowModel) this.arrayList.get(i)).setSelected(z);
        }
    }

    private void showMenu(ImageView imageView, int i) {
        PopupMenu popupMenu = new PopupMenu(this.context, imageView);
        popupMenu.inflate(R.menu.category_options_menu);
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menuDelete :
                        return true;
                    case R.id.menuEdit :
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
}
